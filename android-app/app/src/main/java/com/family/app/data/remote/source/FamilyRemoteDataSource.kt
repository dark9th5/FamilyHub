package com.family.app.data.remote.source

import com.family.app.data.remote.dto.ChatEnvelopeDto
import com.family.app.data.remote.dto.ChatMessageViewDto
import com.family.app.data.remote.dto.DashboardDto
import com.family.app.data.remote.dto.EventViewDto
import com.family.app.data.remote.dto.FamilyMemberDto
import com.family.app.data.remote.dto.LoginResponseDto
import com.family.app.data.remote.dto.MessageResponseDto
import com.family.app.data.remote.dto.MeResponseDto
import com.family.app.data.remote.dto.SocialCommentThreadDto
import com.family.app.data.remote.dto.SocialTargetLikeDto
import com.family.app.data.remote.dto.TimelinePostViewDto
import com.family.app.data.remote.dto.TreeDto
import com.family.app.data.remote.dto.UsernameAvailabilityResponseDto
import java.time.LocalDateTime

interface FamilyRemoteDataSource {
    suspend fun login(username: String, password: String): LoginResponseDto
    suspend fun register(username: String, password: String, fullName: String, cityProvince: String, email: String): MessageResponseDto
    suspend fun verifyEmail(username: String, email: String, code: String): MessageResponseDto
    suspend fun checkUsername(username: String): UsernameAvailabilityResponseDto
    suspend fun me(): MeResponseDto
    suspend fun updateProfile(fullName: String, cityProvince: String, birthDate: String?, bio: String): MeResponseDto
    suspend fun changePassword(currentPassword: String, newPassword: String): MessageResponseDto
    suspend fun requestOldEmailChange(): MessageResponseDto
    suspend fun confirmOldEmailChange(code: String): MessageResponseDto
    suspend fun requestNewEmailChange(ticket: String, newEmail: String): MessageResponseDto
    suspend fun confirmNewEmailChange(ticket: String, newEmail: String, code: String): MessageResponseDto
    suspend fun dashboard(): DashboardDto
    suspend fun members(): List<FamilyMemberDto>
    suspend fun member(memberId: Long): FamilyMemberDto
    suspend fun tree(memberId: Long): TreeDto
    suspend fun timeline(): List<TimelinePostViewDto>
    suspend fun createPost(content: String, imageUrl: String = "")
    suspend fun addComment(postId: Long, content: String)
    suspend fun socialLikes(targetType: String, targetIds: List<Long>): List<SocialTargetLikeDto>
    suspend fun socialComments(targetType: String, targetIds: List<Long>): List<SocialCommentThreadDto>
    suspend fun toggleTargetLike(targetType: String, targetId: Long): SocialTargetLikeDto
    suspend fun addSocialComment(targetType: String, targetId: Long, content: String, parentCommentId: Long?): SocialCommentThreadDto
    suspend fun toggleCommentLike(commentId: Long): SocialTargetLikeDto
    suspend fun events(): List<EventViewDto>
    suspend fun createEvent(title: String, description: String, at: LocalDateTime, location: String)
    suspend fun rsvp(eventId: Long, status: String)
    suspend fun chat(limit: Int = 80): List<ChatMessageViewDto>
    suspend fun sendChat(message: String): ChatEnvelopeDto
    suspend fun createRelationship(fromId: Long, toId: Long, type: String)
}
