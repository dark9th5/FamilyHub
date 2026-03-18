package com.family.backend.application.model

import com.family.backend.domain.model.ChatMessage
import com.family.backend.domain.model.EventRsvp
import com.family.backend.domain.model.FamilyEvent
import com.family.backend.domain.model.FamilyMember
import com.family.backend.domain.model.MemberRelationship
import com.family.backend.domain.model.TimelineComment
import com.family.backend.domain.model.TimelinePost

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
