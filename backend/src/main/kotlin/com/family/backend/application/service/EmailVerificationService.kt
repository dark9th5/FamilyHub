package com.family.backend.application.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

data class PendingRegistration(
    val username: String,
    val fullName: String,
    val cityProvince: String,
    val passwordHash: String,
    val email: String,
    val verificationCode: String,
    val expiresAt: LocalDateTime
)

@Service
class EmailVerificationService(
    @Value("\${spring.mail.username:}") private val senderAddress: String,
    @Value("\${app.auth.otp-expire-minutes:10}") private val otpExpireMinutes: Long,
    private val mailSender: JavaMailSender
) {
    private val pendingByUsername = ConcurrentHashMap<String, PendingRegistration>()
    private val pendingByEmail = ConcurrentHashMap<String, PendingRegistration>()

    fun createPendingRegistration(
        username: String,
        fullName: String,
        cityProvince: String,
        email: String,
        passwordHash: String
    ): PendingRegistration {
        val now = LocalDateTime.now()
        cleanupExpired(now)
        val code = Random.nextInt(100000, 1000000).toString()
        val pending = PendingRegistration(
            username = username,
            fullName = fullName,
            cityProvince = cityProvince,
            passwordHash = passwordHash,
            email = email,
            verificationCode = code,
            expiresAt = now.plusMinutes(otpExpireMinutes)
        )

        pendingByUsername[username.lowercase()] = pending
        pendingByEmail[email.lowercase()] = pending
        sendVerificationEmail(email, code)
        return pending
    }

    fun verify(username: String, email: String, code: String): PendingRegistration {
        val now = LocalDateTime.now()
        cleanupExpired(now)
        val pending = pendingByUsername[username.lowercase()]
            ?: throw IllegalArgumentException("Khong tim thay yeu cau dang ky")

        if (!pending.email.equals(email, ignoreCase = true)) {
            throw IllegalArgumentException("Email khong khop voi tai khoan dang ky")
        }
        if (pending.expiresAt.isBefore(now)) {
            remove(pending)
            throw IllegalArgumentException("Ma xac nhan da het han")
        }
        if (pending.verificationCode != code) {
            throw IllegalArgumentException("Ma xac nhan khong dung")
        }
        remove(pending)
        return pending
    }

    private fun remove(pending: PendingRegistration) {
        pendingByUsername.remove(pending.username.lowercase())
        pendingByEmail.remove(pending.email.lowercase())
    }

    private fun cleanupExpired(now: LocalDateTime) {
        val expired = pendingByUsername.values.filter { it.expiresAt.isBefore(now) }
        expired.forEach { remove(it) }
    }

    private fun sendVerificationEmail(email: String, code: String) {
        if (senderAddress.isBlank()) {
            throw IllegalStateException("SMTP chua duoc cau hinh: thieu spring.mail.username")
        }
        val message = SimpleMailMessage().apply {
            from = senderAddress
            setTo(email)
            subject = "FamilyHub - Ma xac nhan dang ky"
            text = "Ma xac nhan cua ban la: $code\nMa co hieu luc trong $otpExpireMinutes phut."
        }
        mailSender.send(message)
    }
}
