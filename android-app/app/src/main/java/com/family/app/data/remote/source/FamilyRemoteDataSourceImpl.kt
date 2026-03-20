package com.family.app.data.remote.source

import com.family.app.data.remote.api.FamilyApi
import com.family.app.data.remote.dto.CreateCommentRequestDto
import com.family.app.data.remote.dto.CreateEventRequestDto
import com.family.app.data.remote.dto.CreatePostRequestDto
import com.family.app.data.remote.dto.CreateRelationshipRequestDto
import com.family.app.data.remote.dto.CreateSocialCommentRequestDto
import com.family.app.data.remote.dto.ChangePasswordRequestDto
import com.family.app.data.remote.dto.ConfirmCodeRequestDto
import com.family.app.data.remote.dto.ConfirmNewEmailRequestDto
import com.family.app.data.remote.dto.LoginRequestDto
import com.family.app.data.remote.dto.RequestNewEmailCodeRequestDto
import com.family.app.data.remote.dto.RegisterRequestDto
import com.family.app.data.remote.dto.RsvpRequestDto
import com.family.app.data.remote.dto.SendChatRequestDto
import com.family.app.data.remote.dto.UpdateProfileRequestDto
import com.family.app.data.remote.dto.VerifyEmailRequestDto
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

    override suspend fun register(username: String, password: String, fullName: String, cityProvince: String, email: String) = withContext(Dispatchers.IO) {
        api.register(
            RegisterRequestDto(
                username = username,
                password = password,
                fullName = fullName,
                cityProvince = cityProvince,
                email = email
            )
        )
    }

    override suspend fun verifyEmail(username: String, email: String, code: String) = withContext(Dispatchers.IO) {
        api.verifyEmail(
            VerifyEmailRequestDto(
                username = username,
                email = email,
                code = code
            )
        )
    }

    override suspend fun checkUsername(username: String) = withContext(Dispatchers.IO) {
        api.checkUsername(username)
    }

    override suspend fun me() = withContext(Dispatchers.IO) {
        api.me()
    }

    override suspend fun updateProfile(fullName: String, cityProvince: String, birthDate: String?, bio: String) = withContext(Dispatchers.IO) {
        api.updateProfile(
            UpdateProfileRequestDto(
                fullName = fullName,
                cityProvince = cityProvince,
                birthDate = birthDate,
                bio = bio
            )
        )
    }

    override suspend fun changePassword(currentPassword: String, newPassword: String) = withContext(Dispatchers.IO) {
        api.changePassword(ChangePasswordRequestDto(currentPassword = currentPassword, newPassword = newPassword))
    }

    override suspend fun requestOldEmailChange() = withContext(Dispatchers.IO) {
        api.requestOldEmailChange()
    }

    override suspend fun confirmOldEmailChange(code: String) = withContext(Dispatchers.IO) {
        api.confirmOldEmailChange(ConfirmCodeRequestDto(code = code))
    }

    override suspend fun requestNewEmailChange(ticket: String, newEmail: String) = withContext(Dispatchers.IO) {
        api.requestNewEmailChange(ticket = ticket, request = RequestNewEmailCodeRequestDto(newEmail = newEmail))
    }

    override suspend fun confirmNewEmailChange(ticket: String, newEmail: String, code: String) = withContext(Dispatchers.IO) {
        api.confirmNewEmailChange(ticket = ticket, request = ConfirmNewEmailRequestDto(newEmail = newEmail, code = code))
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

    override suspend fun socialLikes(targetType: String, targetIds: List<Long>) = withContext(Dispatchers.IO) {
        api.getSocialLikes(targetType = targetType, targetIds = targetIds)
    }

    override suspend fun socialComments(targetType: String, targetIds: List<Long>) = withContext(Dispatchers.IO) {
        api.getSocialComments(targetType = targetType, targetIds = targetIds)
    }

    override suspend fun toggleTargetLike(targetType: String, targetId: Long) = withContext(Dispatchers.IO) {
        api.toggleTargetLike(targetType = targetType, targetId = targetId)
    }

    override suspend fun addSocialComment(targetType: String, targetId: Long, content: String, parentCommentId: Long?) = withContext(Dispatchers.IO) {
        api.addSocialComment(
            targetType = targetType,
            targetId = targetId,
            request = CreateSocialCommentRequestDto(content = content, parentCommentId = parentCommentId)
        )
    }

    override suspend fun toggleCommentLike(commentId: Long) = withContext(Dispatchers.IO) {
        api.toggleCommentLike(commentId = commentId)
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
