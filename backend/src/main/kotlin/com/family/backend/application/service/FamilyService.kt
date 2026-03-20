package com.family.backend.application.service

import com.family.backend.application.model.ChatMessageView
import com.family.backend.application.model.DashboardView
import com.family.backend.application.model.EventView
import com.family.backend.application.model.MemberTree
import com.family.backend.application.model.RsvpView
import com.family.backend.application.model.TimelineCommentView
import com.family.backend.application.model.TimelinePostView
import com.family.backend.domain.model.SocialComment
import com.family.backend.domain.model.SocialLike
import com.family.backend.domain.model.ChatMessage
import com.family.backend.domain.model.EventRsvp
import com.family.backend.domain.model.FamilyEvent
import com.family.backend.domain.model.FamilyMember
import com.family.backend.domain.model.MemberRelationship
import com.family.backend.domain.model.MemberRole
import com.family.backend.domain.model.RelationshipType
import com.family.backend.domain.model.RsvpStatus
import com.family.backend.domain.model.TimelineComment
import com.family.backend.domain.model.TimelinePost
import com.family.backend.infrastructure.persistence.repository.ChatMessageRepository
import com.family.backend.infrastructure.persistence.repository.EventRsvpRepository
import com.family.backend.infrastructure.persistence.repository.FamilyEventRepository
import com.family.backend.infrastructure.persistence.repository.FamilyMemberRepository
import com.family.backend.infrastructure.persistence.repository.MemberRelationshipRepository
import com.family.backend.infrastructure.persistence.repository.SocialCommentRepository
import com.family.backend.infrastructure.persistence.repository.SocialLikeRepository
import com.family.backend.infrastructure.persistence.repository.TimelineCommentRepository
import com.family.backend.infrastructure.persistence.repository.TimelinePostRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FamilyService(
    private val memberRepository: FamilyMemberRepository,
    private val relationshipRepository: MemberRelationshipRepository,
    private val postRepository: TimelinePostRepository,
    private val commentRepository: TimelineCommentRepository,
    private val socialLikeRepository: SocialLikeRepository,
    private val socialCommentRepository: SocialCommentRepository,
    private val eventRepository: FamilyEventRepository,
    private val rsvpRepository: EventRsvpRepository,
    private val chatRepository: ChatMessageRepository
) {
    fun getMembers(): List<FamilyMember> = memberRepository.findAll().sortedBy { it.fullName }

    fun getMember(id: Long): FamilyMember = memberRepository.findById(id)
        .orElseThrow { IllegalArgumentException("Member not found: $id") }

    fun createMember(member: FamilyMember, actorId: Long): FamilyMember {
        requireAdmin(actorId)
        return memberRepository.save(member)
    }

    @Transactional
    fun createRelationship(fromMemberId: Long, toMemberId: Long, type: RelationshipType, actorId: Long): MemberRelationship {
        requireAdmin(actorId)
        getMember(fromMemberId)
        getMember(toMemberId)
        val relation = relationshipRepository.save(
            MemberRelationship(fromMemberId = fromMemberId, toMemberId = toMemberId, type = type)
        )

        val reverse = when (type) {
            RelationshipType.PARENT -> RelationshipType.CHILD
            RelationshipType.CHILD -> RelationshipType.PARENT
            RelationshipType.SIBLING -> RelationshipType.SIBLING
            RelationshipType.SPOUSE -> RelationshipType.SPOUSE
        }
        relationshipRepository.save(
            MemberRelationship(fromMemberId = toMemberId, toMemberId = fromMemberId, type = reverse)
        )
        return relation
    }

    fun getTree(memberId: Long): MemberTree {
        val center = getMember(memberId)
        val allRelations = relationshipRepository.findByFromMemberIdOrToMemberId(memberId, memberId)
        val connected = allRelations
            .flatMap { listOf(it.fromMemberId, it.toMemberId) }
            .distinct()
            .map(::getMember)
        return MemberTree(center, connected, allRelations)
    }

    fun getTimeline(): List<TimelinePostView> {
        val members = memberRepository.findAll().associateBy { it.id }
        val comments = commentRepository.findAll().groupBy { it.postId }
        return postRepository.findAll()
            .sortedByDescending { it.createdAt }
            .map { post ->
                TimelinePostView(
                    post = post,
                    author = members[post.authorId],
                    comments = comments[post.id].orEmpty().sortedBy { it.createdAt }.map { comment ->
                        TimelineCommentView(comment, members[comment.authorId])
                    }
                )
            }
    }

    fun createPost(authorId: Long, content: String, imageUrl: String): TimelinePost {
        getMember(authorId)
        return postRepository.save(TimelinePost(authorId = authorId, content = content, imageUrl = imageUrl))
    }

    fun addComment(postId: Long, authorId: Long, content: String): TimelineComment {
        getMember(authorId)
        postRepository.findById(postId).orElseThrow { IllegalArgumentException("Post not found: $postId") }
        return commentRepository.save(TimelineComment(postId = postId, authorId = authorId, content = content))
    }

    fun socialLikes(targetType: String, targetIds: List<Long>): List<SocialLike> {
        if (targetIds.isEmpty()) return emptyList()
        return socialLikeRepository.findByTargetTypeAndTargetIdIn(targetType, targetIds)
    }

    fun socialComments(targetType: String, targetIds: List<Long>): List<SocialComment> {
        if (targetIds.isEmpty()) return emptyList()
        return socialCommentRepository.findByTargetTypeAndTargetIdIn(targetType, targetIds)
    }

    @Transactional
    fun toggleTargetLike(targetType: String, targetId: Long, memberId: Long): List<Long> {
        getMember(memberId)
        validateSocialTarget(targetType, targetId)
        val existing = socialLikeRepository.findByTargetTypeAndTargetIdAndMemberId(targetType, targetId, memberId)
        if (existing != null) {
            socialLikeRepository.delete(existing)
        } else {
            socialLikeRepository.save(SocialLike(targetType = targetType, targetId = targetId, memberId = memberId))
        }
        return socialLikeRepository.findByTargetTypeAndTargetId(targetType, targetId)
            .map { it.memberId }
            .distinct()
            .sorted()
    }

    @Transactional
    fun addSocialComment(targetType: String, targetId: Long, memberId: Long, content: String, parentCommentId: Long?): SocialComment {
        getMember(memberId)
        validateSocialTarget(targetType, targetId)
        if (parentCommentId != null) {
            val parent = socialCommentRepository.findById(parentCommentId).orElseThrow {
                IllegalArgumentException("Parent comment not found: $parentCommentId")
            }
            require(parent.targetType == targetType && parent.targetId == targetId) {
                "Parent comment must belong to same target"
            }
        }
        return socialCommentRepository.save(
            SocialComment(
                targetType = targetType,
                targetId = targetId,
                parentCommentId = parentCommentId,
                authorId = memberId,
                content = content.trim()
            )
        )
    }

    @Transactional
    fun toggleCommentLike(commentId: Long, memberId: Long): List<Long> {
        getMember(memberId)
        socialCommentRepository.findById(commentId).orElseThrow {
            IllegalArgumentException("Comment not found: $commentId")
        }
        val existing = socialLikeRepository.findByTargetTypeAndTargetIdAndMemberId("COMMENT", commentId, memberId)
        if (existing != null) {
            socialLikeRepository.delete(existing)
        } else {
            socialLikeRepository.save(SocialLike(targetType = "COMMENT", targetId = commentId, memberId = memberId))
        }
        return socialLikeRepository.findByTargetTypeAndTargetId("COMMENT", commentId)
            .map { it.memberId }
            .distinct()
            .sorted()
    }

    fun getEvents(): List<EventView> {
        val members = memberRepository.findAll().associateBy { it.id }
        val rsvpsByEvent = rsvpRepository.findAll().groupBy { it.eventId }
        return eventRepository.findAll().sortedBy { it.eventTime }.map { event ->
            EventView(
                event = event,
                host = members[event.createdBy],
                rsvps = rsvpsByEvent[event.id].orEmpty().map { RsvpView(it, members[it.memberId]) }
            )
        }
    }

    fun createEvent(event: FamilyEvent, actorId: Long): FamilyEvent {
        requireAdmin(actorId)
        return eventRepository.save(event)
    }

    fun rsvp(eventId: Long, memberId: Long, status: RsvpStatus): EventRsvp {
        getMember(memberId)
        eventRepository.findById(eventId).orElseThrow { IllegalArgumentException("Event not found: $eventId") }
        val existing = rsvpRepository.findByEventIdAndMemberId(eventId, memberId)
        val toSave = if (existing == null) {
            EventRsvp(eventId = eventId, memberId = memberId, status = status)
        } else {
            existing.copy(status = status)
        }
        return rsvpRepository.save(toSave)
    }

    fun getChat(limit: Int): List<ChatMessageView> {
        val members = memberRepository.findAll().associateBy { it.id }
        return chatRepository.findAll()
            .sortedByDescending { it.createdAt }
            .take(limit)
            .reversed()
            .map { ChatMessageView(it, members[it.senderId]) }
    }

    fun sendChat(senderId: Long, message: String): ChatMessage {
        getMember(senderId)
        return chatRepository.save(ChatMessage(senderId = senderId, message = message))
    }

    fun getDashboard(viewerId: Long): DashboardView {
        getMember(viewerId)
        val members = memberRepository.count()
        val upcomingEvents = eventRepository.findAll().sortedBy { it.eventTime }.take(3)
        val latestPosts = getTimeline().take(5)
        val recentMessages = getChat(8)
        return DashboardView(members, upcomingEvents, latestPosts, recentMessages)
    }

    private fun requireAdmin(actorId: Long) {
        val actor = getMember(actorId)
        require(actor.role == MemberRole.ADMIN) { "Permission denied. Admin role required." }
    }

    private fun validateSocialTarget(targetType: String, targetId: Long) {
        when (targetType.uppercase()) {
            "POST" -> postRepository.findById(targetId).orElseThrow { IllegalArgumentException("Post not found: $targetId") }
            "EVENT" -> eventRepository.findById(targetId).orElseThrow { IllegalArgumentException("Event not found: $targetId") }
            else -> throw IllegalArgumentException("Unsupported social target type: $targetType")
        }
    }
}
