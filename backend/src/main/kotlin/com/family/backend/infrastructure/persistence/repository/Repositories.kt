package com.family.backend.infrastructure.persistence.repository

import com.family.backend.domain.model.ChatMessage
import com.family.backend.domain.model.EventRsvp
import com.family.backend.domain.model.FamilyEvent
import com.family.backend.domain.model.FamilyMember
import com.family.backend.domain.model.MemberRelationship
import com.family.backend.domain.model.SocialComment
import com.family.backend.domain.model.SocialLike
import com.family.backend.domain.model.TimelineComment
import com.family.backend.domain.model.TimelinePost
import org.springframework.data.jpa.repository.JpaRepository

interface FamilyMemberRepository : JpaRepository<FamilyMember, Long> {
    fun findByUsername(username: String): FamilyMember?
    fun findByUsernameIgnoreCase(username: String): FamilyMember?
    fun findByEmailIgnoreCase(email: String): FamilyMember?
    fun existsByUsernameIgnoreCase(username: String): Boolean
    fun existsByEmailIgnoreCase(email: String): Boolean
}

interface MemberRelationshipRepository : JpaRepository<MemberRelationship, Long> {
    fun findByFromMemberIdOrToMemberId(fromMemberId: Long, toMemberId: Long): List<MemberRelationship>
}

interface TimelinePostRepository : JpaRepository<TimelinePost, Long>

interface TimelineCommentRepository : JpaRepository<TimelineComment, Long> {
    fun findByPostId(postId: Long): List<TimelineComment>
}

interface SocialLikeRepository : JpaRepository<SocialLike, Long> {
    fun findByTargetTypeAndTargetId(targetType: String, targetId: Long): List<SocialLike>
    fun findByTargetTypeAndTargetIdAndMemberId(targetType: String, targetId: Long, memberId: Long): SocialLike?
    fun findByTargetTypeAndTargetIdIn(targetType: String, targetIds: List<Long>): List<SocialLike>
}

interface SocialCommentRepository : JpaRepository<SocialComment, Long> {
    fun findByTargetTypeAndTargetId(targetType: String, targetId: Long): List<SocialComment>
    fun findByTargetTypeAndTargetIdIn(targetType: String, targetIds: List<Long>): List<SocialComment>
}

interface FamilyEventRepository : JpaRepository<FamilyEvent, Long>

interface EventRsvpRepository : JpaRepository<EventRsvp, Long> {
    fun findByEventId(eventId: Long): List<EventRsvp>
    fun findByEventIdAndMemberId(eventId: Long, memberId: Long): EventRsvp?
}

interface ChatMessageRepository : JpaRepository<ChatMessage, Long>
