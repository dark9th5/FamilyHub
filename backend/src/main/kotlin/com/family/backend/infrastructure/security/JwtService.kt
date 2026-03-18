package com.family.backend.infrastructure.security

import com.family.backend.domain.model.MemberRole
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.Date

@Service
class JwtService(
    @Value("\${app.security.jwt-secret}") private val secret: String,
    @Value("\${app.security.access-token-seconds}") private val accessTokenSeconds: Long
) {
    private val key = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))

    fun generateToken(memberId: Long, username: String, role: MemberRole): String {
        val now = Instant.now()
        val expiry = now.plusSeconds(accessTokenSeconds)
        return Jwts.builder()
            .subject(username)
            .claim("memberId", memberId)
            .claim("role", role.name)
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiry))
            .signWith(key)
            .compact()
    }

    fun parseClaims(token: String): Claims =
        Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
}
