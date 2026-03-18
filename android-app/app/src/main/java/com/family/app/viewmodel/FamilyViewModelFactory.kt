package com.family.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.family.app.data.reminder.EventReminderScheduler
import com.family.app.data.realtime.ChatRealtimeClient
import com.family.app.data.session.SessionStore
import com.family.app.domain.repository.FamilyRepository
import com.family.app.domain.usecase.AddCommentUseCase
import com.family.app.domain.usecase.CreateEventUseCase
import com.family.app.domain.usecase.CreatePostUseCase
import com.family.app.domain.usecase.CreateRelationshipUseCase
import com.family.app.domain.usecase.LoadFamilySnapshotUseCase
import com.family.app.domain.usecase.RefreshChatUseCase
import com.family.app.domain.usecase.RefreshEventsUseCase
import com.family.app.domain.usecase.RefreshTimelineUseCase
import com.family.app.domain.usecase.RsvpEventUseCase
import com.family.app.domain.usecase.SendChatFallbackUseCase

class FamilyViewModelFactory(
    private val repository: FamilyRepository,
    private val sessionStore: SessionStore,
    private val chatRealtimeClient: ChatRealtimeClient,
    private val reminderScheduler: EventReminderScheduler
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FamilyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FamilyViewModel(
                repository = repository,
                sessionStore = sessionStore,
                chatRealtimeClient = chatRealtimeClient,
                reminderScheduler = reminderScheduler,
                loadFamilySnapshot = LoadFamilySnapshotUseCase(repository),
                createPostUseCase = CreatePostUseCase(repository),
                addCommentUseCase = AddCommentUseCase(repository),
                createEventUseCase = CreateEventUseCase(repository),
                rsvpEventUseCase = RsvpEventUseCase(repository),
                createRelationshipUseCase = CreateRelationshipUseCase(repository),
                refreshTimelineUseCase = RefreshTimelineUseCase(repository),
                refreshEventsUseCase = RefreshEventsUseCase(repository),
                refreshChatUseCase = RefreshChatUseCase(repository),
                sendChatFallbackUseCase = SendChatFallbackUseCase(repository)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
