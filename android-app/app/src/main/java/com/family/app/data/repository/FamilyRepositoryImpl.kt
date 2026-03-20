package com.family.app.data.repository

import com.family.app.data.remote.mapper.toDomain
import com.family.app.data.remote.source.FamilyRemoteDataSource
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
import com.family.app.domain.repository.FamilyRepository
import java.time.LocalDateTime

class FamilyRepositoryImpl(
    private val remoteDataSource: FamilyRemoteDataSource
) : FamilyRepository {
    override suspend fun login(username: String, password: String): AuthSession {
        return remoteDataSource.login(username.trim(), password).toDomain()
    }

    override suspend fun register(username: String, password: String, fullName: String, cityProvince: String, email: String): String {
        return remoteDataSource.register(username.trim(), password, fullName.trim(), cityProvince.trim(), email.trim()).message
    }

    override suspend fun verifyEmail(username: String, email: String, code: String): String {
        return remoteDataSource.verifyEmail(username.trim(), email.trim(), code.trim()).message
    }

    override suspend fun checkUsername(username: String): Pair<Boolean, String> {
        val response = remoteDataSource.checkUsername(username.trim())
        return response.available to response.message
    }

    override suspend fun me(): UserProfileSummary {
        return remoteDataSource.me().toDomain()
    }

    override suspend fun updateProfile(fullName: String, cityProvince: String, birthDate: String?, bio: String): UserProfileSummary {
        return remoteDataSource.updateProfile(
            fullName = fullName.trim(),
            cityProvince = cityProvince.trim(),
            birthDate = birthDate,
            bio = bio
        ).toDomain()
    }

    override suspend fun changePassword(currentPassword: String, newPassword: String): String {
        return remoteDataSource.changePassword(currentPassword, newPassword).message
    }

    override suspend fun requestOldEmailChange(): String {
        return remoteDataSource.requestOldEmailChange().message
    }

    override suspend fun confirmOldEmailChange(code: String): String {
        return remoteDataSource.confirmOldEmailChange(code).message
    }

    override suspend fun requestNewEmailChange(ticket: String, newEmail: String): String {
        return remoteDataSource.requestNewEmailChange(ticket, newEmail.trim()).message
    }

    override suspend fun confirmNewEmailChange(ticket: String, newEmail: String, code: String): String {
        return remoteDataSource.confirmNewEmailChange(ticket, newEmail.trim(), code).message
    }

    override suspend fun dashboard(): Dashboard {
        return remoteDataSource.dashboard().toDomain()
    }

    override suspend fun members(): List<FamilyMember> {
        return remoteDataSource.members().map { it.toDomain() }
    }

    override suspend fun member(memberId: Long): FamilyMember {
        return remoteDataSource.member(memberId).toDomain()
    }

    override suspend fun tree(memberId: Long): Tree {
        return remoteDataSource.tree(memberId).toDomain()
    }

    override suspend fun timeline(): List<TimelinePostView> {
        return remoteDataSource.timeline().map { it.toDomain() }
    }

    override suspend fun createPost(content: String, imageUrl: String) {
        remoteDataSource.createPost(content, imageUrl)
    }

    override suspend fun addComment(postId: Long, content: String) {
        remoteDataSource.addComment(postId, content)
    }

    override suspend fun socialLikes(targetType: String, targetIds: List<Long>): List<SocialTargetLike> {
        return remoteDataSource.socialLikes(targetType.uppercase(), targetIds).map { it.toDomain() }
    }

    override suspend fun socialComments(targetType: String, targetIds: List<Long>): List<SocialCommentThread> {
        return remoteDataSource.socialComments(targetType.uppercase(), targetIds).map { it.toDomain() }
    }

    override suspend fun toggleTargetLike(targetType: String, targetId: Long): SocialTargetLike {
        return remoteDataSource.toggleTargetLike(targetType.uppercase(), targetId).toDomain()
    }

    override suspend fun addSocialComment(targetType: String, targetId: Long, content: String, parentCommentId: Long?): SocialCommentThread {
        return remoteDataSource.addSocialComment(
            targetType = targetType.uppercase(),
            targetId = targetId,
            content = content,
            parentCommentId = parentCommentId
        ).toDomain()
    }

    override suspend fun toggleCommentLike(commentId: Long): SocialTargetLike {
        return remoteDataSource.toggleCommentLike(commentId).toDomain()
    }

    override suspend fun events(): List<EventView> {
        return remoteDataSource.events().map { it.toDomain() }
    }

    override suspend fun createEvent(title: String, description: String, at: LocalDateTime, location: String) {
        remoteDataSource.createEvent(title, description, at, location)
    }

    override suspend fun rsvp(eventId: Long, status: String) {
        remoteDataSource.rsvp(eventId, status)
    }

    override suspend fun chat(limit: Int): List<ChatMessageView> {
        return remoteDataSource.chat(limit).map { it.toDomain() }
    }

    override suspend fun sendChat(message: String): ChatEnvelope {
        return remoteDataSource.sendChat(message).toDomain()
    }

    override suspend fun createRelationship(fromId: Long, toId: Long, type: String) {
        remoteDataSource.createRelationship(fromId, toId, type)
    }
}
