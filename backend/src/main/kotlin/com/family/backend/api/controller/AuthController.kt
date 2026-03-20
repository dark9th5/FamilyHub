package com.family.backend.api.controller

import com.family.backend.api.dto.MessageResponse
import com.family.backend.api.dto.LoginRequest
import com.family.backend.api.dto.LoginResponse
import com.family.backend.api.dto.MemberIdentityResponse
import com.family.backend.api.dto.UpdateProfileRequest
import com.family.backend.api.dto.ChangePasswordRequest
import com.family.backend.api.dto.ConfirmCodeRequest
import com.family.backend.api.dto.ConfirmNewEmailRequest
import com.family.backend.api.dto.RequestNewEmailCodeRequest
import com.family.backend.api.dto.RegisterRequest
import com.family.backend.api.dto.UsernameAvailabilityResponse
import com.family.backend.api.dto.VerifyEmailRequest
import com.family.backend.application.service.EmailChangeService
import com.family.backend.application.service.EmailVerificationService
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
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/auth")
@Validated
class AuthController(
    private val memberRepository: FamilyMemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val emailVerificationService: EmailVerificationService,
    private val emailChangeService: EmailChangeService
) {
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): LoginResponse {
        val member = memberRepository.findByUsernameIgnoreCase(request.username.trim())
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sai ten dang nhap hoac mat khau")
        if (!passwordEncoder.matches(request.password, member.passwordHash)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sai ten dang nhap hoac mat khau")
        }
        if (!member.emailVerified) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Tai khoan chua xac nhan email")
        }
        return LoginResponse(
            token = jwtService.generateToken(member.id, member.username, member.role),
            memberId = member.id,
            username = member.username,
            role = member.role.name,
            fullName = member.fullName
        )
    }

    @GetMapping("/check-username")
    fun checkUsername(@RequestParam username: String): UsernameAvailabilityResponse {
        val normalized = username.trim()
        if (normalized.length < 3) {
            return UsernameAvailabilityResponse(
                available = false,
                message = "Ten dang nhap toi thieu 3 ky tu"
            )
        }
        val isAvailable = !memberRepository.existsByUsernameIgnoreCase(normalized)
        return if (isAvailable) {
            UsernameAvailabilityResponse(available = true, message = "Ten dang nhap co san")
        } else {
            UsernameAvailabilityResponse(available = false, message = "Ten dang nhap da ton tai")
        }
    }

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): MessageResponse {
        val username = request.username.trim()
        val fullName = request.fullName.trim()
        val cityProvince = request.cityProvince.trim()
        val email = request.email.trim().lowercase()

        if (request.password.length < 6) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Mat khau phai tu 6 ky tu")
        }
        if (memberRepository.existsByUsernameIgnoreCase(username)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Ten dang nhap da ton tai")
        }
        if (memberRepository.existsByEmailIgnoreCase(email)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Email da duoc su dung")
        }

        runCatching {
            emailVerificationService.createPendingRegistration(
                username = username,
                fullName = fullName,
                cityProvince = cityProvince,
                email = email,
                passwordHash = passwordEncoder.encode(request.password)
            )
        }.onFailure { throwable ->
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, throwable.message ?: "Khong the gui ma xac nhan")
        }

        return MessageResponse("Da gui ma xac nhan 6 so toi email")
    }

    @PostMapping("/verify-email")
    fun verifyEmail(@RequestBody request: VerifyEmailRequest): MessageResponse {
        val pending = runCatching {
            emailVerificationService.verify(
                username = request.username.trim(),
                email = request.email.trim().lowercase(),
                code = request.code.trim()
            )
        }.getOrElse { throwable ->
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, throwable.message ?: "Xac nhan that bai")
        }

        if (memberRepository.existsByUsernameIgnoreCase(pending.username)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Ten dang nhap da ton tai")
        }
        if (memberRepository.existsByEmailIgnoreCase(pending.email)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Email da duoc su dung")
        }

        memberRepository.save(
            FamilyMember(
                username = pending.username,
                email = pending.email,
                emailVerified = true,
                passwordHash = pending.passwordHash,
                fullName = pending.fullName,
                cityProvince = pending.cityProvince,
                role = MemberRole.MEMBER
            )
        )
        return MessageResponse("Dang ky thanh cong, vui long dang nhap")
    }

    @GetMapping("/me")
    fun me(authentication: Authentication): MemberIdentityResponse {
        val principal = authentication.principal as FamilyPrincipal
        val member = memberRepository.findById(principal.memberId).orElseThrow()
        return MemberIdentityResponse(
            id = member.id,
            username = member.username,
            fullName = member.fullName,
            cityProvince = member.cityProvince,
            birthDate = member.birthDate,
            bio = member.bio,
            email = member.email,
            role = member.role.name,
            avatarUrl = member.avatarUrl
        )
    }

    @PutMapping("/profile")
    fun updateProfile(
        @RequestBody request: UpdateProfileRequest,
        authentication: Authentication
    ): MemberIdentityResponse {
        val principal = authentication.principal as FamilyPrincipal
        val member = memberRepository.findById(principal.memberId).orElseThrow()
        val updated = memberRepository.save(
            member.copy(
                fullName = request.fullName.trim(),
                cityProvince = request.cityProvince.trim(),
                birthDate = request.birthDate,
                bio = request.bio.trim()
            )
        )
        return MemberIdentityResponse(
            id = updated.id,
            username = updated.username,
            fullName = updated.fullName,
            cityProvince = updated.cityProvince,
            birthDate = updated.birthDate,
            bio = updated.bio,
            email = updated.email,
            role = updated.role.name,
            avatarUrl = updated.avatarUrl
        )
    }

    @PostMapping("/change-password")
    fun changePassword(
        @RequestBody request: ChangePasswordRequest,
        authentication: Authentication
    ): MessageResponse {
        val principal = authentication.principal as FamilyPrincipal
        val member = memberRepository.findById(principal.memberId).orElseThrow()
        if (!passwordEncoder.matches(request.currentPassword, member.passwordHash)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Mat khau hien tai khong dung")
        }
        if (request.newPassword.length < 6) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Mat khau moi toi thieu 6 ky tu")
        }
        memberRepository.save(member.copy(passwordHash = passwordEncoder.encode(request.newPassword)))
        return MessageResponse("Doi mat khau thanh cong")
    }

    @PostMapping("/email-change/request-old")
    fun requestOldEmailChange(authentication: Authentication): MessageResponse {
        val principal = authentication.principal as FamilyPrincipal
        val member = memberRepository.findById(principal.memberId).orElseThrow()
        val oldEmail = member.email ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Tai khoan chua lien ket email cu")
        runCatching { emailChangeService.requestOldEmailConfirmation(member.id, oldEmail) }
            .onFailure { throw ResponseStatusException(HttpStatus.BAD_REQUEST, it.message ?: "Khong gui duoc ma xac nhan") }
        return MessageResponse("Da gui ma xac nhan huy email cu")
    }

    @PostMapping("/email-change/confirm-old")
    fun confirmOldEmailChange(
        @RequestBody request: ConfirmCodeRequest,
        authentication: Authentication
    ): MessageResponse {
        val principal = authentication.principal as FamilyPrincipal
        val ticket = runCatching { emailChangeService.confirmOldEmail(principal.memberId, request.code.trim()) }
            .getOrElse { throw ResponseStatusException(HttpStatus.BAD_REQUEST, it.message ?: "Xac nhan that bai") }
        return MessageResponse("Xac nhan email cu thanh cong. TICKET:$ticket")
    }

    @PostMapping("/email-change/request-new")
    fun requestNewEmailChange(
        @RequestParam ticket: String,
        @RequestBody request: RequestNewEmailCodeRequest,
        authentication: Authentication
    ): MessageResponse {
        val principal = authentication.principal as FamilyPrincipal
        val normalized = request.newEmail.trim().lowercase()
        if (memberRepository.existsByEmailIgnoreCase(normalized)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Email da duoc su dung")
        }
        runCatching { emailChangeService.requestNewEmailConfirmation(principal.memberId, ticket, normalized) }
            .onFailure { throw ResponseStatusException(HttpStatus.BAD_REQUEST, it.message ?: "Khong gui duoc ma xac nhan") }
        return MessageResponse("Da gui ma xac nhan email moi")
    }

    @PostMapping("/email-change/confirm-new")
    fun confirmNewEmailChange(
        @RequestParam ticket: String,
        @RequestBody request: ConfirmNewEmailRequest,
        authentication: Authentication
    ): MessageResponse {
        val principal = authentication.principal as FamilyPrincipal
        val normalized = request.newEmail.trim().lowercase()
        if (memberRepository.existsByEmailIgnoreCase(normalized)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Email da duoc su dung")
        }
        val newEmail = runCatching {
            emailChangeService.confirmNewEmail(principal.memberId, ticket, normalized, request.code.trim())
        }.getOrElse { throw ResponseStatusException(HttpStatus.BAD_REQUEST, it.message ?: "Xac nhan that bai") }

        val member = memberRepository.findById(principal.memberId).orElseThrow()
        memberRepository.save(member.copy(email = newEmail, emailVerified = true))
        return MessageResponse("Cap nhat email moi thanh cong")
    }
}
