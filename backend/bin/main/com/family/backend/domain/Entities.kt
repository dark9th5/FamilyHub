package com.family.backend.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "family_member")
data class FamilyMember(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(unique = true, nullable = false)
    val username: String,
    @JsonIgnore
    @Column(nullable = false)
    val passwordHash: String,
    val fullName: String,
    val bio: String = "",
    val birthDate: LocalDate? = null,
    val avatarUrl: String = "",
    @Enumerated(EnumType.STRING)
    val role: MemberRole = MemberRole.MEMBER,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

@Entity
@Table(name = "member_relationship")
data class MemberRelationship(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val fromMemberId: Long,
    val toMemberId: Long,
    @Enumerated(EnumType.STRING)
    val type: RelationshipType,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

@Entity
@Table(name = "timeline_post")
data class TimelinePost(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val authorId: Long,
    @Column(length = 2000)
    val content: String,
    val imageUrl: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now()
)

@Entity
@Table(name = "timeline_comment")
data class TimelineComment(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val postId: Long,
    val authorId: Long,
    @Column(length = 1000)
    val content: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

@Entity
@Table(name = "family_event")
data class FamilyEvent(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val title: String,
    @Column(length = 1000)
    val description: String,
    val eventTime: LocalDateTime,
    val location: String,
    val createdBy: Long,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

@Entity
@Table(name = "event_rsvp")
data class EventRsvp(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val eventId: Long,
    val memberId: Long,
    @Enumerated(EnumType.STRING)
    val status: RsvpStatus,
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

@Entity
@Table(name = "chat_message")
data class ChatMessage(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val senderId: Long,
    @Column(length = 2000)
    val message: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
