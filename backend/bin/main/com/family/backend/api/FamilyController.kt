package com.family.backend.api

import com.family.backend.domain.FamilyEvent
import com.family.backend.domain.FamilyMember
import com.family.backend.domain.MemberRole
import com.family.backend.domain.RelationshipType
import com.family.backend.domain.RsvpStatus
import com.family.backend.security.FamilyPrincipal
import com.family.backend.service.FamilyService
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.LocalDateTime

@RestController
@RequestMapping("/api")
@Validated
class FamilyController(
    private val service: FamilyService,
    private val messagingTemplate: SimpMessagingTemplate,
    private val passwordEncoder: PasswordEncoder
) {
    @GetMapping("/dashboard")
    fun dashboard(authentication: org.springframework.security.core.Authentication) =
        service.getDashboard(authMemberId(authentication))

    @GetMapping("/members")
    fun members() = service.getMembers()

    @GetMapping("/members/{id}")
    fun member(@PathVariable id: Long) = service.getMember(id)

    @PostMapping("/members")
    fun createMember(
        @RequestBody request: CreateMemberRequest,
        authentication: org.springframework.security.core.Authentication
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
        @RequestBody request: CreateRelationshipRequest,
        authentication: org.springframework.security.core.Authentication
    ) = service.createRelationship(id, request.toMemberId, request.type, authMemberId(authentication))

    @GetMapping("/tree/{memberId}")
    fun tree(@PathVariable memberId: Long) = service.getTree(memberId)

    @GetMapping("/timeline")
    fun timeline() = service.getTimeline()

    @PostMapping("/timeline/posts")
    fun createPost(
        @RequestBody request: CreatePostRequest,
        authentication: org.springframework.security.core.Authentication
    ) = service.createPost(authMemberId(authentication), request.content, request.imageUrl)

    @PostMapping("/timeline/posts/{postId}/comments")
    fun addComment(
        @PathVariable postId: Long,
        @RequestBody request: CreateCommentRequest,
        authentication: org.springframework.security.core.Authentication
    ) = service.addComment(postId, authMemberId(authentication), request.content)

    @GetMapping("/events")
    fun events() = service.getEvents()

    @PostMapping("/events")
    fun createEvent(
        @RequestBody request: CreateEventRequest,
        authentication: org.springframework.security.core.Authentication
    ) =
        service.createEvent(
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
        @RequestBody request: RsvpRequest,
        authentication: org.springframework.security.core.Authentication
    ) = service.rsvp(eventId, authMemberId(authentication), request.status)

    @GetMapping("/chat/messages")
    fun chatMessages(@RequestParam(defaultValue = "50") limit: Int) = service.getChat(limit)

    @PostMapping("/chat/messages")
    fun sendChat(
        @RequestBody request: SendChatRequest,
        authentication: org.springframework.security.core.Authentication
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

    private fun authMemberId(authentication: org.springframework.security.core.Authentication): Long {
        val principal = authentication.principal as FamilyPrincipal
        return principal.memberId
    }
}

data class CreateMemberRequest(
    @field:NotBlank val username: String,
    @field:NotBlank val password: String,
    @field:NotBlank val fullName: String,
    val bio: String = "",
    val birthDate: LocalDate? = null,
    val avatarUrl: String = "",
    val role: MemberRole = MemberRole.MEMBER
)

data class CreateRelationshipRequest(
    @field:NotNull val toMemberId: Long,
    @field:NotNull val type: RelationshipType
)

data class CreatePostRequest(
    @field:NotBlank val content: String,
    val imageUrl: String = ""
)

data class CreateCommentRequest(
    @field:NotBlank val content: String
)

data class CreateEventRequest(
    @field:NotBlank val title: String,
    @field:NotBlank val description: String,
    @field:NotNull val eventTime: LocalDateTime,
    @field:NotBlank val location: String
)

data class RsvpRequest(
    val status: RsvpStatus
)

data class SendChatRequest(
    val senderId: Long? = null,
    @field:NotBlank val message: String
)

data class ChatEnvelope(
    val id: Long,
    val senderId: Long,
    val message: String,
    val createdAt: LocalDateTime
)
