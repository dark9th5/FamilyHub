package com.family.app.data.repository

import com.family.app.data.remote.mapper.toDomain
import com.family.app.data.remote.source.FamilyRemoteDataSource
import com.family.app.domain.model.AuthSession
import com.family.app.domain.model.ChatEnvelope
import com.family.app.domain.model.ChatMessageView
import com.family.app.domain.model.Dashboard
import com.family.app.domain.model.EventView
import com.family.app.domain.model.FamilyMember
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

    override suspend fun register(username: String, password: String, fullName: String): AuthSession {
        return remoteDataSource.register(username.trim(), password, fullName.trim()).toDomain()
    }

    override suspend fun me(): UserProfileSummary {
        return remoteDataSource.me().toDomain()
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
