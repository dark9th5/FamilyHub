package com.family.app.domain.repository

import com.family.app.domain.model.AuthSession
import com.family.app.domain.model.ChatEnvelope
import com.family.app.domain.model.ChatMessageView
import com.family.app.domain.model.Dashboard
import com.family.app.domain.model.EventView
import com.family.app.domain.model.FamilyMember
import com.family.app.domain.model.SocialCommentThread
import com.family.app.domain.model.SocialTargetLike
import com.family.app.domain.model.TimelinePostView
import com.family.app.domain.model.Tree
import com.family.app.domain.model.UserProfileSummary
import java.time.LocalDateTime

interface FamilyRepository {
    suspend fun login(username: String, password: String): AuthSession
    suspend fun register(username: String, password: String, fullName: String, cityProvince: String, email: String): String
    suspend fun verifyEmail(username: String, email: String, code: String): String
    suspend fun checkUsername(username: String): Pair<Boolean, String>
    suspend fun me(): UserProfileSummary
    suspend fun updateProfile(fullName: String, cityProvince: String, birthDate: String?, bio: String): UserProfileSummary
    suspend fun changePassword(currentPassword: String, newPassword: String): String
    suspend fun requestOldEmailChange(): String
    suspend fun confirmOldEmailChange(code: String): String
    suspend fun requestNewEmailChange(ticket: String, newEmail: String): String
    suspend fun confirmNewEmailChange(ticket: String, newEmail: String, code: String): String
    suspend fun dashboard(): Dashboard
    suspend fun members(): List<FamilyMember>
    suspend fun member(memberId: Long): FamilyMember
    suspend fun tree(memberId: Long): Tree
    suspend fun timeline(): List<TimelinePostView>
    suspend fun createPost(content: String, imageUrl: String = "")
    suspend fun addComment(postId: Long, content: String)
    suspend fun socialLikes(targetType: String, targetIds: List<Long>): List<SocialTargetLike>
    suspend fun socialComments(targetType: String, targetIds: List<Long>): List<SocialCommentThread>
    suspend fun toggleTargetLike(targetType: String, targetId: Long): SocialTargetLike
    suspend fun addSocialComment(targetType: String, targetId: Long, content: String, parentCommentId: Long? = null): SocialCommentThread
    suspend fun toggleCommentLike(commentId: Long): SocialTargetLike
    suspend fun events(): List<EventView>
    suspend fun createEvent(title: String, description: String, at: LocalDateTime, location: String)
    suspend fun rsvp(eventId: Long, status: String)
    suspend fun chat(limit: Int = 80): List<ChatMessageView>
    suspend fun sendChat(message: String): ChatEnvelope
    suspend fun createRelationship(fromId: Long, toId: Long, type: String)
}
