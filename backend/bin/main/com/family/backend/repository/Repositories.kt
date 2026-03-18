package com.family.backend.repository

import com.family.backend.domain.ChatMessage
import com.family.backend.domain.EventRsvp
import com.family.backend.domain.FamilyEvent
import com.family.backend.domain.FamilyMember
import com.family.backend.domain.MemberRelationship
import com.family.backend.domain.TimelineComment
import com.family.backend.domain.TimelinePost
import org.springframework.data.jpa.repository.JpaRepository

interface FamilyMemberRepository : JpaRepository<FamilyMember, Long> {
    fun findByUsername(username: String): FamilyMember?
}

interface MemberRelationshipRepository : JpaRepository<MemberRelationship, Long> {
    fun findByFromMemberIdOrToMemberId(fromMemberId: Long, toMemberId: Long): List<MemberRelationship>
}

interface TimelinePostRepository : JpaRepository<TimelinePost, Long>

interface TimelineCommentRepository : JpaRepository<TimelineComment, Long> {
    fun findByPostId(postId: Long): List<TimelineComment>
}

interface FamilyEventRepository : JpaRepository<FamilyEvent, Long>

interface EventRsvpRepository : JpaRepository<EventRsvp, Long> {
    fun findByEventId(eventId: Long): List<EventRsvp>
    fun findByEventIdAndMemberId(eventId: Long, memberId: Long): EventRsvp?
}

interface ChatMessageRepository : JpaRepository<ChatMessage, Long>
