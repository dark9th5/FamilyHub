package com.family.app.domain.usecase

import com.family.app.domain.model.ChatMessageView
import com.family.app.domain.model.Dashboard
import com.family.app.domain.model.EventView
import com.family.app.domain.model.FamilyMember
import com.family.app.domain.model.TimelinePostView
import com.family.app.domain.model.Tree
import com.family.app.domain.model.UserProfileSummary
import com.family.app.domain.repository.FamilyRepository
import java.time.LocalDateTime

data class FamilySnapshot(
    val me: UserProfileSummary,
    val currentUser: FamilyMember?,
    val members: List<FamilyMember>,
    val tree: Tree,
    val timeline: List<TimelinePostView>,
    val events: List<EventView>,
    val chat: List<ChatMessageView>,
    val dashboard: Dashboard
)

class LoadFamilySnapshotUseCase(private val repository: FamilyRepository) {
    suspend operator fun invoke(): FamilySnapshot {
        val me = repository.me()
        val members = repository.members()
        val currentUser = members.firstOrNull { it.id == me.id }
        return FamilySnapshot(
            me = me,
            currentUser = currentUser,
            members = members,
            tree = repository.tree(me.id),
            timeline = repository.timeline(),
            events = repository.events(),
            chat = repository.chat(),
            dashboard = repository.dashboard()
        )
    }
}

class RefreshTimelineUseCase(private val repository: FamilyRepository) {
    suspend operator fun invoke(): Pair<List<TimelinePostView>, Dashboard> {
        return repository.timeline() to repository.dashboard()
    }
}

class RefreshEventsUseCase(private val repository: FamilyRepository) {
    suspend operator fun invoke(): Pair<List<EventView>, Dashboard> {
        return repository.events() to repository.dashboard()
    }
}

class RefreshChatUseCase(private val repository: FamilyRepository) {
    suspend operator fun invoke(): Pair<List<ChatMessageView>, Dashboard> {
        return repository.chat() to repository.dashboard()
    }
}

class CreatePostUseCase(private val repository: FamilyRepository) {
    suspend operator fun invoke(content: String, imageUrl: String = "") {
        repository.createPost(content, imageUrl)
    }
}

class AddCommentUseCase(private val repository: FamilyRepository) {
    suspend operator fun invoke(postId: Long, content: String) {
        repository.addComment(postId, content)
    }
}

class CreateEventUseCase(private val repository: FamilyRepository) {
    suspend operator fun invoke(title: String, description: String, at: LocalDateTime, location: String) {
        repository.createEvent(title, description, at, location)
    }
}

class RsvpEventUseCase(private val repository: FamilyRepository) {
    suspend operator fun invoke(eventId: Long, status: String) {
        repository.rsvp(eventId, status)
    }
}

class CreateRelationshipUseCase(private val repository: FamilyRepository) {
    suspend operator fun invoke(fromId: Long, toId: Long, type: String) {
        repository.createRelationship(fromId, toId, type)
    }
}

class SendChatFallbackUseCase(private val repository: FamilyRepository) {
    suspend operator fun invoke(message: String) {
        repository.sendChat(message)
    }
}
