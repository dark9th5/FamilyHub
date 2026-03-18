package com.family.app.domain.model

data class FamilyMember(
    val id: Long,
    val username: String,
    val fullName: String,
    val bio: String,
    val birthDate: String?,
    val avatarUrl: String,
    val role: String,
    val createdAt: String
)

data class MemberRelationship(
    val id: Long,
    val fromMemberId: Long,
    val toMemberId: Long,
    val type: String,
    val createdAt: String
)

data class Tree(
    val center: FamilyMember,
    val members: List<FamilyMember>,
    val relationships: List<MemberRelationship>
)

data class TimelineComment(
    val id: Long,
    val postId: Long,
    val authorId: Long,
    val content: String,
    val createdAt: String
)

data class TimelineCommentView(
    val comment: TimelineComment,
    val author: FamilyMember?
)

data class TimelinePost(
    val id: Long,
    val authorId: Long,
    val content: String,
    val imageUrl: String,
    val createdAt: String
)

data class TimelinePostView(
    val post: TimelinePost,
    val author: FamilyMember?,
    val comments: List<TimelineCommentView>
)

data class FamilyEvent(
    val id: Long,
    val title: String,
    val description: String,
    val eventTime: String,
    val location: String,
    val createdBy: Long,
    val createdAt: String
)

data class EventRsvp(
    val id: Long,
    val eventId: Long,
    val memberId: Long,
    val status: String,
    val updatedAt: String
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

data class ChatMessage(
    val id: Long,
    val senderId: Long,
    val message: String,
    val createdAt: String
)

data class ChatMessageView(
    val message: ChatMessage,
    val sender: FamilyMember?
)

data class Dashboard(
    val memberCount: Long,
    val upcomingEvents: List<FamilyEvent>,
    val latestPosts: List<TimelinePostView>,
    val recentMessages: List<ChatMessageView>
)

data class AuthSession(
    val token: String,
    val memberId: Long,
    val username: String,
    val role: String,
    val fullName: String
)

data class UserProfileSummary(
    val id: Long,
    val username: String,
    val fullName: String,
    val role: String,
    val avatarUrl: String
)

data class ChatEnvelope(
    val id: Long,
    val senderId: Long,
    val message: String,
    val createdAt: String
)

data class FamilyTask(
    val id: Long,
    val title: String,
    val note: String,
    val familyId: Long? = null,
    val assignedMemberId: Long?,
    val dueDate: String,
    val status: String,
    val points: Int,
    val rewarded: Boolean,
    val canceled: Boolean = false,
    val resetDaily: Boolean = true,
    val createdBy: Long? = null,
    val templateId: Long? = null,
    val createdAt: String
)

data class TaskTemplate(
    val id: Long,
    val title: String,
    val note: String,
    val weekday: Int,
    val points: Int,
    val active: Boolean,
    val createdAt: String
)

data class RewardPoint(
    val memberId: Long,
    val points: Int
)

data class FinanceTransaction(
    val id: Long,
    val title: String,
    val amount: Double,
    val category: String,
    val paidByMemberId: Long,
    val participantIds: List<Long>,
    val note: String,
    val isCanceled: Boolean,
    val createdAt: String
)

data class FamilyGroup(
    val id: Long,
    val name: String,
    val code: String,
    val ownerId: Long,
    val ownerRole: String = "PARENT_FATHER",
    val memberIds: List<Long>,
    val createdAt: String
)

data class FamilyMemberRoleAssignment(
    val familyId: Long,
    val memberId: Long,
    val role: String,
    val assignedBy: Long,
    val assignedAt: String
)

data class ClanGroup(
    val id: Long,
    val name: String,
    val code: String,
    val ownerId: Long,
    val memberIds: List<Long>,
    val delegateIds: List<Long> = emptyList(),
    val pendingMemberIds: List<Long> = emptyList(),
    val createdAt: String
)

data class ClanJoinRequest(
    val id: Long,
    val clanId: Long,
    val memberId: Long,
    val status: String,
    val createdAt: String,
    val reviewedAt: String? = null,
    val reviewedBy: Long? = null
)

data class ClanPermissionDelegation(
    val clanId: Long,
    val memberId: Long,
    val permissions: List<String>,
    val grantedBy: Long,
    val grantedAt: String
)

data class FamilyLocalEvent(
    val id: Long,
    val familyId: Long,
    val title: String,
    val description: String,
    val eventTime: String,
    val location: String,
    val createdBy: Long,
    val createdAt: String
)

data class ClanTreePerson(
    val id: Long,
    val clanId: Long,
    val name: String,
    val roleLabel: String,
    val isDeceased: Boolean = false,
    val linkedMemberId: Long? = null,
    val createdBy: Long,
    val createdAt: String
)

data class ClanTreeLink(
    val id: Long,
    val clanId: Long,
    val fromPersonId: Long,
    val toPersonId: Long,
    val relationType: String,
    val createdBy: Long,
    val createdAt: String
)
