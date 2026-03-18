package com.family.backend.api.dto

import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank val username: String,
    @field:NotBlank val password: String
)

data class RegisterRequest(
    @field:NotBlank val username: String,
    @field:NotBlank val password: String,
    @field:NotBlank val fullName: String
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
    val role: String,
    val avatarUrl: String
)
