package com.family.backend.application.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

data class PendingEmailChange(
    val memberId: Long,
    val oldEmail: String,
    val oldCode: String,
    val oldCodeExpiresAt: LocalDateTime,
    val oldConfirmed: Boolean = false,
    val ticket: String? = null,
    val newEmail: String? = null,
    val newCode: String? = null,
    val newCodeExpiresAt: LocalDateTime? = null
)

@Service
class EmailChangeService(
    @Value("\${spring.mail.username:}") private val senderAddress: String,
    @Value("\${app.auth.otp-expire-minutes:10}") private val otpExpireMinutes: Long,
    private val mailSender: JavaMailSender
) {
    private val pendingByMemberId = ConcurrentHashMap<Long, PendingEmailChange>()

    fun requestOldEmailConfirmation(memberId: Long, oldEmail: String) {
        val code = randomCode()
        val pending = PendingEmailChange(
            memberId = memberId,
            oldEmail = oldEmail,
            oldCode = code,
            oldCodeExpiresAt = LocalDateTime.now().plusMinutes(otpExpireMinutes)
        )
        pendingByMemberId[memberId] = pending
        sendMail(
            to = oldEmail,
            subject = "FamilyHub - Xac nhan huy email hien tai",
            body = "Ma xac nhan huy email hien tai cua ban: $code\nMa co hieu luc trong $otpExpireMinutes phut."
        )
    }

    fun confirmOldEmail(memberId: Long, code: String): String {
        val pending = pendingByMemberId[memberId] ?: throw IllegalArgumentException("Khong tim thay yeu cau doi email")
        if (pending.oldCodeExpiresAt.isBefore(LocalDateTime.now())) {
            pendingByMemberId.remove(memberId)
            throw IllegalArgumentException("Ma xac nhan da het han")
        }
        if (pending.oldCode != code) {
            throw IllegalArgumentException("Ma xac nhan khong dung")
        }
        val ticket = UUID.randomUUID().toString()
        pendingByMemberId[memberId] = pending.copy(oldConfirmed = true, ticket = ticket)
        return ticket
    }

    fun requestNewEmailConfirmation(memberId: Long, ticket: String, newEmail: String) {
        val pending = pendingByMemberId[memberId] ?: throw IllegalArgumentException("Khong tim thay yeu cau doi email")
        if (!pending.oldConfirmed || pending.ticket != ticket) {
            throw IllegalArgumentException("Ban can xac nhan email cu truoc")
        }
        val newCode = randomCode()
        pendingByMemberId[memberId] = pending.copy(
            newEmail = newEmail,
            newCode = newCode,
            newCodeExpiresAt = LocalDateTime.now().plusMinutes(otpExpireMinutes)
        )
        sendMail(
            to = newEmail,
            subject = "FamilyHub - Xac nhan lien ket email moi",
            body = "Ma xac nhan lien ket email moi cua ban: $newCode\nMa co hieu luc trong $otpExpireMinutes phut."
        )
    }

    fun confirmNewEmail(memberId: Long, ticket: String, newEmail: String, code: String): String {
        val pending = pendingByMemberId[memberId] ?: throw IllegalArgumentException("Khong tim thay yeu cau doi email")
        if (!pending.oldConfirmed || pending.ticket != ticket) {
            throw IllegalArgumentException("Ban can xac nhan email cu truoc")
        }
        if (!pending.newEmail.equals(newEmail, ignoreCase = true)) {
            throw IllegalArgumentException("Email moi khong khop")
        }
        val expiresAt = pending.newCodeExpiresAt ?: throw IllegalArgumentException("Ban can gui ma xac nhan email moi")
        if (expiresAt.isBefore(LocalDateTime.now())) {
            pendingByMemberId.remove(memberId)
            throw IllegalArgumentException("Ma xac nhan da het han")
        }
        if (pending.newCode != code) {
            throw IllegalArgumentException("Ma xac nhan khong dung")
        }
        pendingByMemberId.remove(memberId)
        return newEmail
    }

    private fun randomCode(): String = Random.nextInt(100000, 1000000).toString()

    private fun sendMail(to: String, subject: String, body: String) {
        if (senderAddress.isBlank()) {
            throw IllegalStateException("SMTP chua duoc cau hinh: thieu spring.mail.username")
        }
        val message = SimpleMailMessage().apply {
            from = senderAddress
            setTo(to)
            this.subject = subject
            text = body
        }
        mailSender.send(message)
    }
}
