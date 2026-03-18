package com.family.backend.infrastructure.persistence.repository

import com.family.backend.domain.model.ChatMessage
import com.family.backend.domain.model.EventRsvp
import com.family.backend.domain.model.FamilyEvent
import com.family.backend.domain.model.FamilyMember
import com.family.backend.domain.model.MemberRelationship
import com.family.backend.domain.model.TimelineComment
import com.family.backend.domain.model.TimelinePost
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
