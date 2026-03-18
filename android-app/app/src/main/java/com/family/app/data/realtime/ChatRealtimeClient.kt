package com.family.app.data.realtime

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class ChatRealtimeClient(
    private val realtimeUrl: String
) {
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    private var socket: WebSocket? = null

    fun connect(token: String, onMessage: (ChatRealtimeMessage) -> Unit, onError: (String) -> Unit) {
        disconnect()
        val request = Request.Builder()
            .url("$realtimeUrl?token=$token")
            .build()

        socket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                runCatching {
                    val obj = JSONObject(text)
                    ChatRealtimeMessage(
                        id = obj.optLong("id"),
                        senderId = obj.optLong("senderId"),
                        message = obj.optString("message"),
                        createdAt = obj.optString("createdAt")
                    )
                }.onSuccess(onMessage)
                    .onFailure { onError(it.message ?: "Khong doc duoc tin nhan realtime") }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                onError(t.message ?: "Ket noi realtime that bai")
            }
        })
    }

    fun send(message: String) {
        socket?.send(message)
    }

    fun disconnect() {
        socket?.close(1000, "Ngat ket noi")
        socket = null
    }
}

data class ChatRealtimeMessage(
    val id: Long,
    val senderId: Long,
    val message: String,
    val createdAt: String
)
