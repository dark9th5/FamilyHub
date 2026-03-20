package com.family.backend.api.controller

import com.family.backend.api.dto.ChatEnvelope
import com.family.backend.api.dto.CreateCommentRequest
import com.family.backend.api.dto.CreateEventRequest
import com.family.backend.api.dto.CreateMemberRequest
import com.family.backend.api.dto.CreatePostRequest
import com.family.backend.api.dto.CreateRelationshipRequest
import com.family.backend.api.dto.CreateSocialCommentRequest
import com.family.backend.api.dto.RsvpRequest
import com.family.backend.api.dto.SendChatRequest
import com.family.backend.api.dto.SocialCommentResponse
import com.family.backend.api.dto.SocialTargetLikeResponse
import com.family.backend.application.service.FamilyService
import com.family.backend.domain.model.FamilyEvent
import com.family.backend.domain.model.FamilyMember
import com.family.backend.infrastructure.security.FamilyPrincipal
import jakarta.validation.Valid
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@Validated
class FamilyController(
    private val service: FamilyService,
    private val messagingTemplate: SimpMessagingTemplate,
    private val passwordEncoder: PasswordEncoder
) {
    @GetMapping("/dashboard")
    fun dashboard(authentication: Authentication) = service.getDashboard(authMemberId(authentication))

    @GetMapping("/members")
    fun members() = service.getMembers()

    @GetMapping("/members/{id}")
    fun member(@PathVariable id: Long) = service.getMember(id)

    @PostMapping("/members")
    fun createMember(
        @Valid @RequestBody request: CreateMemberRequest,
        authentication: Authentication
    ): FamilyMember {
        val member = FamilyMember(
            username = request.username,
            passwordHash = passwordEncoder.encode(request.password),
            fullName = request.fullName,
            bio = request.bio,
            birthDate = request.birthDate,
            avatarUrl = request.avatarUrl,
            role = request.role
        )
        return service.createMember(member, authMemberId(authentication))
    }

    @PostMapping("/members/{id}/relationships")
    fun createRelationship(
        @PathVariable id: Long,
        @Valid @RequestBody request: CreateRelationshipRequest,
        authentication: Authentication
    ) = service.createRelationship(id, request.toMemberId, request.type, authMemberId(authentication))

    @GetMapping("/tree/{memberId}")
    fun tree(@PathVariable memberId: Long) = service.getTree(memberId)

    @GetMapping("/timeline")
    fun timeline() = service.getTimeline()

    @PostMapping("/timeline/posts")
    fun createPost(
        @Valid @RequestBody request: CreatePostRequest,
        authentication: Authentication
    ) = service.createPost(authMemberId(authentication), request.content, request.imageUrl)

    @PostMapping("/timeline/posts/{postId}/comments")
    fun addComment(
        @PathVariable postId: Long,
        @Valid @RequestBody request: CreateCommentRequest,
        authentication: Authentication
    ) = service.addComment(postId, authMemberId(authentication), request.content)

    @GetMapping("/events")
    fun events() = service.getEvents()

    @PostMapping("/events")
    fun createEvent(
        @Valid @RequestBody request: CreateEventRequest,
        authentication: Authentication
    ) = service.createEvent(
        FamilyEvent(
            title = request.title,
            description = request.description,
            eventTime = request.eventTime,
            location = request.location,
            createdBy = authMemberId(authentication)
        ),
        authMemberId(authentication)
    )

    @PostMapping("/events/{eventId}/rsvp")
    fun rsvp(
        @PathVariable eventId: Long,
        @Valid @RequestBody request: RsvpRequest,
        authentication: Authentication
    ) = service.rsvp(eventId, authMemberId(authentication), request.status)

    @GetMapping("/chat/messages")
    fun chatMessages(@RequestParam(defaultValue = "50") limit: Int) = service.getChat(limit)

    @PostMapping("/chat/messages")
    fun sendChat(
        @Valid @RequestBody request: SendChatRequest,
        authentication: Authentication
    ): ChatEnvelope {
        val saved = service.sendChat(authMemberId(authentication), request.message)
        val envelope = ChatEnvelope(saved.id, saved.senderId, request.message, saved.createdAt)
        messagingTemplate.convertAndSend("/topic/family-chat", envelope)
        return envelope
    }

    @MessageMapping("/chat")
    fun receiveSocketMessage(@Payload request: SendChatRequest): ChatEnvelope {
        requireNotNull(request.senderId) { "senderId is required for STOMP fallback" }
        val saved = service.sendChat(request.senderId, request.message)
        val envelope = ChatEnvelope(saved.id, saved.senderId, saved.message, saved.createdAt)
        messagingTemplate.convertAndSend("/topic/family-chat", envelope)
        return envelope
    }

    private fun authMemberId(authentication: Authentication): Long {
        val principal = authentication.principal as FamilyPrincipal
        return principal.memberId
    }

    @GetMapping("/social/likes")
    fun socialLikes(
        @RequestParam targetType: String,
        @RequestParam targetIds: List<Long>
    ): List<SocialTargetLikeResponse> {
        val normalized = targetType.uppercase()
        return service.socialLikes(normalized, targetIds)
            .groupBy { it.targetId }
            .entries
            .sortedBy { it.key }
            .map { (targetId, rows) ->
                SocialTargetLikeResponse(
                    targetType = normalized,
                    targetId = targetId,
                    memberIds = rows.map { it.memberId }.distinct().sorted()
                )
            }
    }

    @GetMapping("/social/comments")
    fun socialComments(
        @RequestParam targetType: String,
        @RequestParam targetIds: List<Long>
    ): List<SocialCommentResponse> {
        val rows = service.socialComments(targetType.uppercase(), targetIds)
        return rows.sortedBy { it.createdAt }.map { comment ->
            val likedIds = service.socialLikes("COMMENT", listOf(comment.id)).map { it.memberId }.distinct().sorted()
            SocialCommentResponse(
                id = comment.id,
                targetType = comment.targetType,
                targetId = comment.targetId,
                parentCommentId = comment.parentCommentId,
                authorId = comment.authorId,
                content = comment.content,
                likedMemberIds = likedIds,
                createdAt = comment.createdAt
            )
        }
    }

    @PostMapping("/social/targets/{targetType}/{targetId}/likes/toggle")
    fun toggleTargetLike(
        @PathVariable targetType: String,
        @PathVariable targetId: Long,
        authentication: Authentication
    ): SocialTargetLikeResponse {
        val memberIds = service.toggleTargetLike(targetType.uppercase(), targetId, authMemberId(authentication))
        return SocialTargetLikeResponse(
            targetType = targetType.uppercase(),
            targetId = targetId,
            memberIds = memberIds
        )
    }

    @PostMapping("/social/targets/{targetType}/{targetId}/comments")
    fun addSocialComment(
        @PathVariable targetType: String,
        @PathVariable targetId: Long,
        @Valid @RequestBody request: CreateSocialCommentRequest,
        authentication: Authentication
    ): SocialCommentResponse {
        val saved = service.addSocialComment(
            targetType = targetType.uppercase(),
            targetId = targetId,
            memberId = authMemberId(authentication),
            content = request.content,
            parentCommentId = request.parentCommentId
        )
        return SocialCommentResponse(
            id = saved.id,
            targetType = saved.targetType,
            targetId = saved.targetId,
            parentCommentId = saved.parentCommentId,
            authorId = saved.authorId,
            content = saved.content,
            likedMemberIds = emptyList(),
            createdAt = saved.createdAt
        )
    }

    @PostMapping("/social/comments/{commentId}/likes/toggle")
    fun toggleCommentLike(
        @PathVariable commentId: Long,
        authentication: Authentication
    ): SocialTargetLikeResponse {
        val memberIds = service.toggleCommentLike(commentId, authMemberId(authentication))
        return SocialTargetLikeResponse(
            targetType = "COMMENT",
            targetId = commentId,
            memberIds = memberIds
        )
    }
}
