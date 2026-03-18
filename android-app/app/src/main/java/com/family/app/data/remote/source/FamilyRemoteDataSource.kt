package com.family.app.data.remote.source

import com.family.app.data.remote.dto.ChatEnvelopeDto
import com.family.app.data.remote.dto.ChatMessageViewDto
import com.family.app.data.remote.dto.DashboardDto
import com.family.app.data.remote.dto.EventViewDto
import com.family.app.data.remote.dto.FamilyMemberDto
import com.family.app.data.remote.dto.LoginResponseDto
import com.family.app.data.remote.dto.MeResponseDto
import com.family.app.data.remote.dto.TimelinePostViewDto
import com.family.app.data.remote.dto.TreeDto
import java.time.LocalDateTime

interface FamilyRemoteDataSource {
    suspend fun login(username: String, password: String): LoginResponseDto
    suspend fun register(username: String, password: String, fullName: String): LoginResponseDto
    suspend fun me(): MeResponseDto
    suspend fun dashboard(): DashboardDto
    suspend fun members(): List<FamilyMemberDto>
    suspend fun member(memberId: Long): FamilyMemberDto
    suspend fun tree(memberId: Long): TreeDto
    suspend fun timeline(): List<TimelinePostViewDto>
    suspend fun createPost(content: String, imageUrl: String = "")
    suspend fun addComment(postId: Long, content: String)
    suspend fun events(): List<EventViewDto>
    suspend fun createEvent(title: String, description: String, at: LocalDateTime, location: String)
    suspend fun rsvp(eventId: Long, status: String)
    suspend fun chat(limit: Int = 80): List<ChatMessageViewDto>
    suspend fun sendChat(message: String): ChatEnvelopeDto
    suspend fun createRelationship(fromId: Long, toId: Long, type: String)
}
