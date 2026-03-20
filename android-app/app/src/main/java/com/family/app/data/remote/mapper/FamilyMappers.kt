package com.family.app.data.remote.mapper

import com.family.app.data.remote.dto.ChatEnvelopeDto
import com.family.app.data.remote.dto.ChatMessageDto
import com.family.app.data.remote.dto.ChatMessageViewDto
import com.family.app.data.remote.dto.DashboardDto
import com.family.app.data.remote.dto.EventRsvpDto
import com.family.app.data.remote.dto.EventViewDto
import com.family.app.data.remote.dto.FamilyEventDto
import com.family.app.data.remote.dto.FamilyMemberDto
import com.family.app.data.remote.dto.LoginResponseDto
import com.family.app.data.remote.dto.MeResponseDto
import com.family.app.data.remote.dto.MemberRelationshipDto
import com.family.app.data.remote.dto.RsvpViewDto
import com.family.app.data.remote.dto.SocialCommentThreadDto
import com.family.app.data.remote.dto.SocialTargetLikeDto
import com.family.app.data.remote.dto.TimelineCommentDto
import com.family.app.data.remote.dto.TimelineCommentViewDto
import com.family.app.data.remote.dto.TimelinePostDto
import com.family.app.data.remote.dto.TimelinePostViewDto
import com.family.app.data.remote.dto.TreeDto
import com.family.app.domain.model.AuthSession
import com.family.app.domain.model.ChatEnvelope
import com.family.app.domain.model.ChatMessage
import com.family.app.domain.model.ChatMessageView
import com.family.app.domain.model.Dashboard
import com.family.app.domain.model.EventRsvp
import com.family.app.domain.model.EventView
import com.family.app.domain.model.FamilyEvent
import com.family.app.domain.model.FamilyMember
import com.family.app.domain.model.MemberRelationship
import com.family.app.domain.model.RsvpView
import com.family.app.domain.model.SocialCommentThread
import com.family.app.domain.model.SocialTargetLike
import com.family.app.domain.model.TimelineComment
import com.family.app.domain.model.TimelineCommentView
import com.family.app.domain.model.TimelinePost
import com.family.app.domain.model.TimelinePostView
import com.family.app.domain.model.Tree
import com.family.app.domain.model.UserProfileSummary

fun LoginResponseDto.toDomain(): AuthSession = AuthSession(
    token = token,
    memberId = memberId,
    username = username,
    role = role,
    fullName = fullName
)

fun MeResponseDto.toDomain(): UserProfileSummary = UserProfileSummary(
    id = id,
    username = username,
    fullName = fullName,
    cityProvince = cityProvince,
    birthDate = birthDate,
    bio = bio,
    email = email,
    role = role,
    avatarUrl = avatarUrl
)

fun FamilyMemberDto.toDomain(): FamilyMember = FamilyMember(
    id = id,
    username = username,
    fullName = fullName,
    email = email,
    cityProvince = cityProvince,
    bio = bio,
    birthDate = birthDate,
    avatarUrl = avatarUrl,
    role = role,
    createdAt = createdAt
)

fun MemberRelationshipDto.toDomain(): MemberRelationship = MemberRelationship(
    id = id,
    fromMemberId = fromMemberId,
    toMemberId = toMemberId,
    type = type,
    createdAt = createdAt
)

fun TreeDto.toDomain(): Tree = Tree(
    center = center.toDomain(),
    members = members.map { it.toDomain() },
    relationships = relationships.map { it.toDomain() }
)

fun TimelineCommentDto.toDomain(): TimelineComment = TimelineComment(
    id = id,
    postId = postId,
    authorId = authorId,
    content = content,
    createdAt = createdAt
)

fun TimelineCommentViewDto.toDomain(): TimelineCommentView = TimelineCommentView(
    comment = comment.toDomain(),
    author = author?.toDomain()
)

fun TimelinePostDto.toDomain(): TimelinePost = TimelinePost(
    id = id,
    authorId = authorId,
    content = content,
    imageUrl = imageUrl,
    createdAt = createdAt
)

fun TimelinePostViewDto.toDomain(): TimelinePostView = TimelinePostView(
    post = post.toDomain(),
    author = author?.toDomain(),
    comments = comments.map { it.toDomain() }
)

fun FamilyEventDto.toDomain(): FamilyEvent = FamilyEvent(
    id = id,
    title = title,
    description = description,
    eventTime = eventTime,
    location = location,
    createdBy = createdBy,
    createdAt = createdAt
)

fun EventRsvpDto.toDomain(): EventRsvp = EventRsvp(
    id = id,
    eventId = eventId,
    memberId = memberId,
    status = status,
    updatedAt = updatedAt
)

fun RsvpViewDto.toDomain(): RsvpView = RsvpView(
    rsvp = rsvp.toDomain(),
    member = member?.toDomain()
)

fun EventViewDto.toDomain(): EventView = EventView(
    event = event.toDomain(),
    host = host?.toDomain(),
    rsvps = rsvps.map { it.toDomain() }
)

fun ChatMessageDto.toDomain(): ChatMessage = ChatMessage(
    id = id,
    senderId = senderId,
    message = message,
    createdAt = createdAt
)

fun ChatMessageViewDto.toDomain(): ChatMessageView = ChatMessageView(
    message = message.toDomain(),
    sender = sender?.toDomain()
)

fun DashboardDto.toDomain(): Dashboard = Dashboard(
    memberCount = memberCount,
    upcomingEvents = upcomingEvents.map { it.toDomain() },
    latestPosts = latestPosts.map { it.toDomain() },
    recentMessages = recentMessages.map { it.toDomain() }
)

fun ChatEnvelopeDto.toDomain(): ChatEnvelope = ChatEnvelope(
    id = id,
    senderId = senderId,
    message = message,
    createdAt = createdAt
)

fun SocialTargetLikeDto.toDomain(): SocialTargetLike = SocialTargetLike(
    targetType = targetType,
    targetId = targetId,
    memberIds = memberIds
)

fun SocialCommentThreadDto.toDomain(): SocialCommentThread = SocialCommentThread(
    id = id,
    targetType = targetType,
    targetId = targetId,
    parentCommentId = parentCommentId,
    authorId = authorId,
    content = content,
    likedMemberIds = likedMemberIds,
    createdAt = createdAt
)
