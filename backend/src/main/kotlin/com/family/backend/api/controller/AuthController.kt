package com.family.backend.api.controller

import com.family.backend.api.dto.LoginRequest
import com.family.backend.api.dto.LoginResponse
import com.family.backend.api.dto.MemberIdentityResponse
import com.family.backend.api.dto.RegisterRequest
import com.family.backend.domain.model.FamilyMember
import com.family.backend.domain.model.MemberRole
import com.family.backend.infrastructure.persistence.repository.FamilyMemberRepository
import com.family.backend.infrastructure.security.FamilyPrincipal
import com.family.backend.infrastructure.security.JwtService
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
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
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sai ten dang nhap hoac mat khau")
        if (!passwordEncoder.matches(request.password, member.passwordHash)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sai ten dang nhap hoac mat khau")
        }
        return LoginResponse(
            token = jwtService.generateToken(member.id, member.username, member.role),
            memberId = member.id,
            username = member.username,
            role = member.role.name,
            fullName = member.fullName
        )
    }

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): LoginResponse {
        val username = request.username.trim()
        val fullName = request.fullName.trim()
        if (username.length < 3) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Ten dang nhap phai tu 3 ky tu")
        }
        if (request.password.length < 3) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Mat khau phai tu 3 ky tu")
        }
        if (memberRepository.findByUsername(username) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Ten dang nhap da ton tai")
        }

        val member = memberRepository.save(
            FamilyMember(
                username = username,
                passwordHash = passwordEncoder.encode(request.password),
                fullName = fullName,
                role = MemberRole.MEMBER
            )
        )

        return LoginResponse(
            token = jwtService.generateToken(member.id, member.username, member.role),
            memberId = member.id,
            username = member.username,
            role = member.role.name,
            fullName = member.fullName
        )
    }

    @GetMapping("/me")
    fun me(authentication: Authentication): MemberIdentityResponse {
        val principal = authentication.principal as FamilyPrincipal
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
