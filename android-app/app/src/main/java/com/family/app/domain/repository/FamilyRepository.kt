package com.family.app.domain.repository

import com.family.app.domain.model.AuthSession
import com.family.app.domain.model.ChatEnvelope
import com.family.app.domain.model.ChatMessageView
import com.family.app.domain.model.Dashboard
import com.family.app.domain.model.EventView
import com.family.app.domain.model.FamilyMember
import com.family.app.domain.model.TimelinePostView
import com.family.app.domain.model.Tree
import com.family.app.domain.model.UserProfileSummary
import java.time.LocalDateTime

interface FamilyRepository {
    suspend fun login(username: String, password: String): AuthSession
    suspend fun register(username: String, password: String, fullName: String): AuthSession
    suspend fun me(): UserProfileSummary
    suspend fun dashboard(): Dashboard
    suspend fun members(): List<FamilyMember>
    suspend fun member(memberId: Long): FamilyMember
    suspend fun tree(memberId: Long): Tree
    suspend fun timeline(): List<TimelinePostView>
    suspend fun createPost(content: String, imageUrl: String = "")
    suspend fun addComment(postId: Long, content: String)
    suspend fun events(): List<EventView>
    suspend fun createEvent(title: String, description: String, at: LocalDateTime, location: String)
    suspend fun rsvp(eventId: Long, status: String)
    suspend fun chat(limit: Int = 80): List<ChatMessageView>
    suspend fun sendChat(message: String): ChatEnvelope
    suspend fun createRelationship(fromId: Long, toId: Long, type: String)
}
