package com.family.backend.config

import com.family.backend.security.JwtService
import com.family.backend.service.FamilyService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class RawChatWebSocketConfig(
    private val familyService: FamilyService,
    private val jwtService: JwtService,
    private val objectMapper: ObjectMapper
) : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(RawChatWebSocketHandler(familyService, jwtService, objectMapper), "/ws-chat-raw")
            .setAllowedOriginPatterns("*")
    }
}
