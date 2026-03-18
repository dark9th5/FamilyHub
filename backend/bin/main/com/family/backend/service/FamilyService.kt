package com.family.backend.service

import com.family.backend.domain.ChatMessage
import com.family.backend.domain.EventRsvp
import com.family.backend.domain.FamilyEvent
import com.family.backend.domain.FamilyMember
import com.family.backend.domain.MemberRelationship
import com.family.backend.domain.MemberRole
import com.family.backend.domain.RelationshipType
import com.family.backend.domain.RsvpStatus
import com.family.backend.domain.TimelineComment
import com.family.backend.domain.TimelinePost
import com.family.backend.repository.ChatMessageRepository
import com.family.backend.repository.EventRsvpRepository
import com.family.backend.repository.FamilyEventRepository
import com.family.backend.repository.FamilyMemberRepository
import com.family.backend.repository.MemberRelationshipRepository
import com.family.backend.repository.TimelineCommentRepository
import com.family.backend.repository.TimelinePostRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FamilyService(
    private val memberRepository: FamilyMemberRepository,
    private val relationshipRepository: MemberRelationshipRepository,
    private val postRepository: TimelinePostRepository,
    private val commentRepository: TimelineCommentRepository,
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
}

data class MemberTree(
    val center: FamilyMember,
    val members: List<FamilyMember>,
    val relationships: List<MemberRelationship>
)

data class TimelineCommentView(
    val comment: TimelineComment,
    val author: FamilyMember?
)

data class TimelinePostView(
    val post: TimelinePost,
    val author: FamilyMember?,
    val comments: List<TimelineCommentView>
)

data class RsvpView(
    val rsvp: EventRsvp,
    val member: FamilyMember?
)

data class EventView(
    val event: FamilyEvent,
    val host: FamilyMember?,
    val rsvps: List<RsvpView>
)

data class ChatMessageView(
    val message: ChatMessage,
    val sender: FamilyMember?
)

data class DashboardView(
    val memberCount: Long,
    val upcomingEvents: List<FamilyEvent>,
    val latestPosts: List<TimelinePostView>,
    val recentMessages: List<ChatMessageView>
)
