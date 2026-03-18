package com.family.backend.config

import com.family.backend.security.JwtService
import com.family.backend.service.FamilyService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.CopyOnWriteArraySet

class RawChatWebSocketHandler(
    private val familyService: FamilyService,
    private val jwtService: JwtService,
    private val objectMapper: ObjectMapper
) : TextWebSocketHandler() {
    private val sessions = CopyOnWriteArraySet<WebSocketSession>()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val token = parseQueryValue(session.uri?.query.orEmpty(), "token")
        if (token.isNullOrBlank()) {
            session.close(CloseStatus.POLICY_VIOLATION)
            return
        }

        val claims = runCatching { jwtService.parseClaims(token) }.getOrNull()
        if (claims == null) {
            session.close(CloseStatus.POLICY_VIOLATION)
            return
        }

        session.attributes["memberId"] = (claims["memberId"] as Number).toLong()
        sessions += session
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val memberId = session.attributes["memberId"] as? Long ?: return
        if (message.payload.isBlank()) return

        val saved = familyService.sendChat(memberId, message.payload.trim())
        val outgoing = objectMapper.writeValueAsString(
            mapOf(
                "id" to saved.id,
                "senderId" to saved.senderId,
                "message" to saved.message,
                "createdAt" to saved.createdAt.toString()
            )
        )

        sessions.forEach {
            if (it.isOpen) {
                it.sendMessage(TextMessage(outgoing))
            }
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessions -= session
    }

    private fun parseQueryValue(query: String, key: String): String? {
        return query.split("&")
            .mapNotNull {
                val idx = it.indexOf('=')
                if (idx <= 0) return@mapNotNull null
                val k = URLDecoder.decode(it.substring(0, idx), StandardCharsets.UTF_8)
                val v = URLDecoder.decode(it.substring(idx + 1), StandardCharsets.UTF_8)
                if (k == key) v else null
            }
            .firstOrNull()
    }
}
