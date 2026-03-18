package com.family.app.data.remote.source

import com.family.app.data.remote.api.FamilyApi
import com.family.app.data.remote.dto.CreateCommentRequestDto
import com.family.app.data.remote.dto.CreateEventRequestDto
import com.family.app.data.remote.dto.CreatePostRequestDto
import com.family.app.data.remote.dto.CreateRelationshipRequestDto
import com.family.app.data.remote.dto.LoginRequestDto
import com.family.app.data.remote.dto.RegisterRequestDto
import com.family.app.data.remote.dto.RsvpRequestDto
import com.family.app.data.remote.dto.SendChatRequestDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FamilyRemoteDataSourceImpl(
    private val api: FamilyApi
) : FamilyRemoteDataSource {
    override suspend fun login(username: String, password: String) = withContext(Dispatchers.IO) {
        api.login(LoginRequestDto(username = username, password = password))
    }

    override suspend fun register(username: String, password: String, fullName: String) = withContext(Dispatchers.IO) {
        api.register(
            RegisterRequestDto(
                username = username,
                password = password,
                fullName = fullName
            )
        )
    }

    override suspend fun me() = withContext(Dispatchers.IO) {
        api.me()
    }

    override suspend fun dashboard() = withContext(Dispatchers.IO) {
        api.getDashboard()
    }

    override suspend fun members() = withContext(Dispatchers.IO) {
        api.getMembers()
    }

    override suspend fun member(memberId: Long) = withContext(Dispatchers.IO) {
        api.getMember(memberId)
    }

    override suspend fun tree(memberId: Long) = withContext(Dispatchers.IO) {
        api.getTree(memberId)
    }

    override suspend fun timeline() = withContext(Dispatchers.IO) {
        api.getTimeline()
    }

    override suspend fun createPost(content: String, imageUrl: String) {
        withContext(Dispatchers.IO) {
            api.createPost(CreatePostRequestDto(content = content, imageUrl = imageUrl))
        }
    }

    override suspend fun addComment(postId: Long, content: String) {
        withContext(Dispatchers.IO) {
            api.addComment(postId, CreateCommentRequestDto(content = content))
        }
    }

    override suspend fun events() = withContext(Dispatchers.IO) {
        api.getEvents()
    }

    override suspend fun createEvent(title: String, description: String, at: LocalDateTime, location: String) {
        withContext(Dispatchers.IO) {
            api.createEvent(
                CreateEventRequestDto(
                    title = title,
                    description = description,
                    eventTime = at.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    location = location
                )
            )
        }
    }

    override suspend fun rsvp(eventId: Long, status: String) {
        withContext(Dispatchers.IO) {
            api.rsvp(eventId, RsvpRequestDto(status = status))
        }
    }

    override suspend fun chat(limit: Int) = withContext(Dispatchers.IO) {
        api.getChatMessages(limit)
    }

    override suspend fun sendChat(message: String) = withContext(Dispatchers.IO) {
        api.sendChat(SendChatRequestDto(message = message))
    }

    override suspend fun createRelationship(fromId: Long, toId: Long, type: String) {
        withContext(Dispatchers.IO) {
            api.createRelationship(fromId, CreateRelationshipRequestDto(toMemberId = toId, type = type))
        }
    }
}
