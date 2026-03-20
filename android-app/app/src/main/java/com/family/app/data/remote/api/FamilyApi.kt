package com.family.app.data.remote.api

import com.family.app.data.remote.dto.ChatEnvelopeDto
import com.family.app.data.remote.dto.ChatMessageDto
import com.family.app.data.remote.dto.ChatMessageViewDto
import com.family.app.data.remote.dto.CreateCommentRequestDto
import com.family.app.data.remote.dto.CreateEventRequestDto
import com.family.app.data.remote.dto.CreatePostRequestDto
import com.family.app.data.remote.dto.CreateRelationshipRequestDto
import com.family.app.data.remote.dto.CreateSocialCommentRequestDto
import com.family.app.data.remote.dto.DashboardDto
import com.family.app.data.remote.dto.EventViewDto
import com.family.app.data.remote.dto.FamilyMemberDto
import com.family.app.data.remote.dto.LoginRequestDto
import com.family.app.data.remote.dto.LoginResponseDto
import com.family.app.data.remote.dto.MessageResponseDto
import com.family.app.data.remote.dto.MeResponseDto
import com.family.app.data.remote.dto.ChangePasswordRequestDto
import com.family.app.data.remote.dto.ConfirmCodeRequestDto
import com.family.app.data.remote.dto.ConfirmNewEmailRequestDto
import com.family.app.data.remote.dto.RegisterRequestDto
import com.family.app.data.remote.dto.RequestNewEmailCodeRequestDto
import com.family.app.data.remote.dto.RsvpRequestDto
import com.family.app.data.remote.dto.SocialCommentThreadDto
import com.family.app.data.remote.dto.SocialTargetLikeDto
import com.family.app.data.remote.dto.TimelineCommentDto
import com.family.app.data.remote.dto.TimelinePostDto
import com.family.app.data.remote.dto.TimelinePostViewDto
import com.family.app.data.remote.dto.TreeDto
import com.family.app.data.remote.dto.UpdateProfileRequestDto
import com.family.app.data.remote.dto.UsernameAvailabilityResponseDto
import com.family.app.data.remote.dto.VerifyEmailRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface FamilyApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequestDto): MessageResponseDto

    @POST("auth/verify-email")
    suspend fun verifyEmail(@Body request: VerifyEmailRequestDto): MessageResponseDto

    @GET("auth/check-username")
    suspend fun checkUsername(@Query("username") username: String): UsernameAvailabilityResponseDto

    @GET("auth/me")
    suspend fun me(): MeResponseDto

    @PUT("auth/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequestDto): MeResponseDto

    @POST("auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequestDto): MessageResponseDto

    @POST("auth/email-change/request-old")
    suspend fun requestOldEmailChange(): MessageResponseDto

    @POST("auth/email-change/confirm-old")
    suspend fun confirmOldEmailChange(@Body request: ConfirmCodeRequestDto): MessageResponseDto

    @POST("auth/email-change/request-new")
    suspend fun requestNewEmailChange(
        @Query("ticket") ticket: String,
        @Body request: RequestNewEmailCodeRequestDto
    ): MessageResponseDto

    @POST("auth/email-change/confirm-new")
    suspend fun confirmNewEmailChange(
        @Query("ticket") ticket: String,
        @Body request: ConfirmNewEmailRequestDto
    ): MessageResponseDto

    @GET("api/dashboard")
    suspend fun getDashboard(): DashboardDto

    @GET("api/members")
    suspend fun getMembers(): List<FamilyMemberDto>

    @GET("api/members/{id}")
    suspend fun getMember(@Path("id") id: Long): FamilyMemberDto

    @POST("api/members/{id}/relationships")
    suspend fun createRelationship(
        @Path("id") id: Long,
        @Body request: CreateRelationshipRequestDto
    )

    @GET("api/tree/{memberId}")
    suspend fun getTree(@Path("memberId") memberId: Long): TreeDto

    @GET("api/timeline")
    suspend fun getTimeline(): List<TimelinePostViewDto>

    @POST("api/timeline/posts")
    suspend fun createPost(@Body request: CreatePostRequestDto): TimelinePostDto

    @POST("api/timeline/posts/{postId}/comments")
    suspend fun addComment(
        @Path("postId") postId: Long,
        @Body request: CreateCommentRequestDto
    ): TimelineCommentDto

    @GET("api/social/likes")
    suspend fun getSocialLikes(
        @Query("targetType") targetType: String,
        @Query("targetIds") targetIds: List<Long>
    ): List<SocialTargetLikeDto>

    @GET("api/social/comments")
    suspend fun getSocialComments(
        @Query("targetType") targetType: String,
        @Query("targetIds") targetIds: List<Long>
    ): List<SocialCommentThreadDto>

    @POST("api/social/targets/{targetType}/{targetId}/likes/toggle")
    suspend fun toggleTargetLike(
        @Path("targetType") targetType: String,
        @Path("targetId") targetId: Long
    ): SocialTargetLikeDto

    @POST("api/social/targets/{targetType}/{targetId}/comments")
    suspend fun addSocialComment(
        @Path("targetType") targetType: String,
        @Path("targetId") targetId: Long,
        @Body request: CreateSocialCommentRequestDto
    ): SocialCommentThreadDto

    @POST("api/social/comments/{commentId}/likes/toggle")
    suspend fun toggleCommentLike(
        @Path("commentId") commentId: Long
    ): SocialTargetLikeDto

    @GET("api/events")
    suspend fun getEvents(): List<EventViewDto>

    @POST("api/events")
    suspend fun createEvent(@Body request: CreateEventRequestDto)

    @POST("api/events/{eventId}/rsvp")
    suspend fun rsvp(
        @Path("eventId") eventId: Long,
        @Body request: RsvpRequestDto
    )

    @GET("api/chat/messages")
    suspend fun getChatMessages(@Query("limit") limit: Int = 80): List<ChatMessageViewDto>

    @POST("api/chat/messages")
    suspend fun sendChat(@Body request: com.family.app.data.remote.dto.SendChatRequestDto): ChatEnvelopeDto
}
