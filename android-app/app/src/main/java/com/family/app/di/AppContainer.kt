package com.family.app.di

import android.content.Context
import com.family.app.BuildConfig
import com.family.app.data.remote.api.ApiFactory
import com.family.app.data.remote.source.FamilyRemoteDataSource
import com.family.app.data.remote.source.FamilyRemoteDataSourceImpl
import com.family.app.data.realtime.ChatRealtimeClient
import com.family.app.data.repository.FamilyRepositoryImpl
import com.family.app.data.session.SessionStore
import com.family.app.domain.repository.FamilyRepository

object AppContainer {
    private var initialized = false

    lateinit var sessionStore: SessionStore
        private set
    lateinit var chatRealtimeClient: ChatRealtimeClient
        private set
    lateinit var repository: FamilyRepository
        private set

    fun initialize(context: Context) {
        if (initialized) return

        val baseUrl = BuildConfig.NGROK_HTTPS_BASE_URL
        val realtimeUrl = BuildConfig.NGROK_WSS_CHAT_URL

        sessionStore = SessionStore(context.applicationContext)
        chatRealtimeClient = ChatRealtimeClient(realtimeUrl)

        val remoteDataSource: FamilyRemoteDataSource = FamilyRemoteDataSourceImpl(
            api = ApiFactory.create(baseUrl) { sessionStore.currentToken() }
        )

        repository = FamilyRepositoryImpl(remoteDataSource)
        initialized = true
    }
}
