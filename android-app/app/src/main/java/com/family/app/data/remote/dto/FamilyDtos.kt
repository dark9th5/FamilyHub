package com.family.app.data.remote.dto

data class FamilyMemberDto(
    val id: Long,
    val username: String,
    val fullName: String,
    val bio: String,
    val birthDate: String?,
    val avatarUrl: String,
    val role: String,
    val createdAt: String
)

data class MemberRelationshipDto(
    val id: Long,
    val fromMemberId: Long,
    val toMemberId: Long,
    val type: String,
    val createdAt: String
)

data class TreeDto(
    val center: FamilyMemberDto,
    val members: List<FamilyMemberDto>,
    val relationships: List<MemberRelationshipDto>
)

data class TimelineCommentDto(
    val id: Long,
    val postId: Long,
    val authorId: Long,
    val content: String,
    val createdAt: String
)

data class TimelineCommentViewDto(
    val comment: TimelineCommentDto,
    val author: FamilyMemberDto?
)

data class TimelinePostDto(
    val id: Long,
    val authorId: Long,
    val content: String,
    val imageUrl: String,
    val createdAt: String
)

data class TimelinePostViewDto(
    val post: TimelinePostDto,
    val author: FamilyMemberDto?,
    val comments: List<TimelineCommentViewDto>
)

data class FamilyEventDto(
    val id: Long,
    val title: String,
    val description: String,
    val eventTime: String,
    val location: String,
    val createdBy: Long,
    val createdAt: String
)

data class EventRsvpDto(
    val id: Long,
    val eventId: Long,
    val memberId: Long,
    val status: String,
    val updatedAt: String
)

data class RsvpViewDto(
    val rsvp: EventRsvpDto,
    val member: FamilyMemberDto?
)

data class EventViewDto(
    val event: FamilyEventDto,
    val host: FamilyMemberDto?,
    val rsvps: List<RsvpViewDto>
)

data class ChatMessageDto(
    val id: Long,
    val senderId: Long,
    val message: String,
    val createdAt: String
)

data class ChatMessageViewDto(
    val message: ChatMessageDto,
    val sender: FamilyMemberDto?
)

data class DashboardDto(
    val memberCount: Long,
    val upcomingEvents: List<FamilyEventDto>,
    val latestPosts: List<TimelinePostViewDto>,
    val recentMessages: List<ChatMessageViewDto>
)

data class LoginRequestDto(
    val username: String,
    val password: String
)

data class RegisterRequestDto(
    val username: String,
    val password: String,
    val fullName: String
)

data class LoginResponseDto(
    val token: String,
    val memberId: Long,
    val username: String,
    val role: String,
    val fullName: String
)

data class MeResponseDto(
    val id: Long,
    val username: String,
    val fullName: String,
    val role: String,
    val avatarUrl: String
)

data class CreatePostRequestDto(
    val content: String,
    val imageUrl: String = ""
)

data class CreateCommentRequestDto(
    val content: String
)

data class CreateEventRequestDto(
    val title: String,
    val description: String,
    val eventTime: String,
    val location: String
)

data class RsvpRequestDto(
    val status: String
)

data class CreateRelationshipRequestDto(
    val toMemberId: Long,
    val type: String
)

data class SendChatRequestDto(
    val senderId: Long? = null,
    val message: String
)

data class ChatEnvelopeDto(
    val id: Long,
    val senderId: Long,
    val message: String,
    val createdAt: String
)
