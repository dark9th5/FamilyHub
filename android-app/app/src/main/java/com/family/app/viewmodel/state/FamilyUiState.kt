package com.family.app.viewmodel.state

import com.family.app.domain.model.ChatMessageView
import com.family.app.domain.model.ClanJoinRequest
import com.family.app.domain.model.ClanPermissionDelegation
import com.family.app.domain.model.ClanTreeLink
import com.family.app.domain.model.ClanTreePerson
import com.family.app.domain.model.ClanGroup
import com.family.app.domain.model.Dashboard
import com.family.app.domain.model.EventView
import com.family.app.domain.model.FamilyGroup
import com.family.app.domain.model.FamilyLocalEvent
import com.family.app.domain.model.FamilyMemberRoleAssignment
import com.family.app.domain.model.FamilyMember
import com.family.app.domain.model.FamilyTask
import com.family.app.domain.model.FinanceTransaction
import com.family.app.domain.model.RewardPoint
import com.family.app.domain.model.TaskTemplate
import com.family.app.domain.model.TimelinePostView
import com.family.app.domain.model.Tree

data class FamilyUiState(
    val isAuthenticated: Boolean = false,
    val authUsername: String = "",
    val realtimeConnected: Boolean = false,
    val isLoading: Boolean = false,
    val currentUser: FamilyMember? = null,
    val members: List<FamilyMember> = emptyList(),
    val tree: Tree? = null,
    val timeline: List<TimelinePostView> = emptyList(),
    val events: List<EventView> = emptyList(),
    val chatMessages: List<ChatMessageView> = emptyList(),
    val tasks: List<FamilyTask> = emptyList(),
    val taskTemplates: List<TaskTemplate> = emptyList(),
    val rewardPoints: List<RewardPoint> = emptyList(),
    val financeTransactions: List<FinanceTransaction> = emptyList(),
    val ageYears: Int? = null,
    val families: List<FamilyGroup> = emptyList(),
    val familyRoles: List<FamilyMemberRoleAssignment> = emptyList(),
    val clans: List<ClanGroup> = emptyList(),
    val clanJoinRequests: List<ClanJoinRequest> = emptyList(),
    val clanDelegations: List<ClanPermissionDelegation> = emptyList(),
    val activeFamilyId: Long? = null,
    val activeClanId: Long? = null,
    val familyEvents: List<FamilyLocalEvent> = emptyList(),
    val clanTreePeople: List<ClanTreePerson> = emptyList(),
    val clanTreeLinks: List<ClanTreeLink> = emptyList(),
    val birthdayAlerts: List<String> = emptyList(),
    val dashboard: Dashboard? = null,
    val loginUsername: String = "",
    val loginPassword: String = "",
    val rememberLogin: Boolean = false,
    val error: String? = null,
    val notice: String? = null,
    val selectedMemberId: Long? = null
)
