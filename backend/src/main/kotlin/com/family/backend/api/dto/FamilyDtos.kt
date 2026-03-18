package com.family.backend.api.dto

import com.family.backend.domain.model.MemberRole
import com.family.backend.domain.model.RelationshipType
import com.family.backend.domain.model.RsvpStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import java.time.LocalDateTime

data class CreateMemberRequest(
    @field:NotBlank val username: String,
    @field:NotBlank val password: String,
    @field:NotBlank val fullName: String,
    val bio: String = "",
    val birthDate: LocalDate? = null,
    val avatarUrl: String = "",
    val role: MemberRole = MemberRole.MEMBER
)

data class CreateRelationshipRequest(
    @field:NotNull val toMemberId: Long,
    @field:NotNull val type: RelationshipType
)

data class CreatePostRequest(
    @field:NotBlank val content: String,
    val imageUrl: String = ""
)

data class CreateCommentRequest(
    @field:NotBlank val content: String
)

data class CreateEventRequest(
    @field:NotBlank val title: String,
    @field:NotBlank val description: String,
    @field:NotNull val eventTime: LocalDateTime,
    @field:NotBlank val location: String
)

data class RsvpRequest(
    val status: RsvpStatus
)

data class SendChatRequest(
    val senderId: Long? = null,
    @field:NotBlank val message: String
)

data class ChatEnvelope(
    val id: Long,
    val senderId: Long,
    val message: String,
    val createdAt: LocalDateTime
)
