package com.family.backend.api.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class LoginRequest(
    @field:NotBlank val username: String,
    @field:NotBlank val password: String
)

data class RegisterRequest(
    @field:NotBlank
    @field:Size(min = 3, max = 32)
    val username: String,
    @field:NotBlank
    @field:Size(min = 6, max = 128)
    val password: String,
    @field:NotBlank
    @field:Size(min = 2, max = 120)
    val fullName: String,
    @field:NotBlank
    @field:Size(min = 2, max = 120)
    val cityProvince: String,
    @field:NotBlank
    @field:Email
    val email: String
)

data class VerifyEmailRequest(
    @field:NotBlank
    @field:Size(min = 3, max = 32)
    val username: String,
    @field:NotBlank
    @field:Email
    val email: String,
    @field:NotBlank
    @field:Pattern(regexp = "^\\d{6}$")
    val code: String
)

data class UsernameAvailabilityResponse(
    val available: Boolean,
    val message: String
)

data class MessageResponse(
    val message: String
)

data class LoginResponse(
    val token: String,
    val memberId: Long,
    val username: String,
    val role: String,
    val fullName: String
)

data class MemberIdentityResponse(
    val id: Long,
    val username: String,
    val fullName: String,
    val cityProvince: String,
    val birthDate: LocalDate?,
    val bio: String,
    val email: String?,
    val role: String,
    val avatarUrl: String
)

data class UpdateProfileRequest(
    @field:NotBlank
    @field:Size(min = 2, max = 120)
    val fullName: String,
    @field:NotBlank
    @field:Size(min = 2, max = 120)
    val cityProvince: String,
    val birthDate: LocalDate? = null,
    @field:Size(max = 500)
    val bio: String = ""
)

data class ChangePasswordRequest(
    @field:NotBlank
    val currentPassword: String,
    @field:NotBlank
    @field:Size(min = 6, max = 128)
    val newPassword: String
)

data class ConfirmCodeRequest(
    @field:NotBlank
    @field:Pattern(regexp = "^\\d{6}$")
    val code: String
)

data class RequestNewEmailCodeRequest(
    @field:NotBlank
    @field:Email
    val newEmail: String
)

data class ConfirmNewEmailRequest(
    @field:NotBlank
    @field:Email
    val newEmail: String,
    @field:NotBlank
    @field:Pattern(regexp = "^\\d{6}$")
    val code: String
)
