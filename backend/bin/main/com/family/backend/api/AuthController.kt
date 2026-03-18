package com.family.backend.api

import com.family.backend.repository.FamilyMemberRepository
import com.family.backend.security.JwtService
import jakarta.validation.constraints.NotBlank
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/auth")
@Validated
class AuthController(
    private val memberRepository: FamilyMemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService
) {
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): LoginResponse {
        val member = memberRepository.findByUsername(request.username)
            ?: throw ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid username or password")
        if (!passwordEncoder.matches(request.password, member.passwordHash)) {
            throw ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid username or password")
        }
        return LoginResponse(
            token = jwtService.generateToken(member.id, member.username, member.role),
            memberId = member.id,
            username = member.username,
            role = member.role.name,
            fullName = member.fullName
        )
    }

    @GetMapping("/me")
    fun me(authentication: org.springframework.security.core.Authentication): MemberIdentityResponse {
        val principal = authentication.principal as com.family.backend.security.FamilyPrincipal
        val member = memberRepository.findById(principal.memberId).orElseThrow()
        return MemberIdentityResponse(
            id = member.id,
            username = member.username,
            fullName = member.fullName,
            role = member.role.name,
            avatarUrl = member.avatarUrl
        )
    }
}

data class LoginRequest(
    @field:NotBlank val username: String,
    @field:NotBlank val password: String
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
