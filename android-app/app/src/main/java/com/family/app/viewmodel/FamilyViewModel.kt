package com.family.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.family.app.data.reminder.EventReminderScheduler
import com.family.app.data.realtime.ChatRealtimeClient
import com.family.app.data.session.SessionStore
import com.family.app.domain.model.FamilyMember
import com.family.app.domain.model.FamilyMemberRoleAssignment
import com.family.app.domain.model.FamilyGroup
import com.family.app.domain.model.FamilyJoinRequest
import com.family.app.domain.model.FamilyLocalEvent
import com.family.app.domain.model.FamilyTask
import com.family.app.domain.model.FinanceTransaction
import com.family.app.domain.model.RewardPoint
import com.family.app.domain.model.SocialCommentThread
import com.family.app.domain.model.SocialTargetLike
import com.family.app.domain.model.TaskTemplate
import com.family.app.domain.model.ClanGroup
import com.family.app.domain.model.ClanJoinRequest
import com.family.app.domain.model.ClanPermissionDelegation
import com.family.app.domain.model.ClanTreeLink
import com.family.app.domain.model.ClanTreePerson
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
import com.family.app.viewmodel.state.FamilyUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import kotlin.random.Random

class FamilyViewModel(
    private val repository: FamilyRepository,
    private val sessionStore: SessionStore,
    private val chatRealtimeClient: ChatRealtimeClient,
    private val reminderScheduler: EventReminderScheduler,
    private val loadFamilySnapshot: LoadFamilySnapshotUseCase,
    private val createPostUseCase: CreatePostUseCase,
    private val addCommentUseCase: AddCommentUseCase,
    private val createEventUseCase: CreateEventUseCase,
    private val rsvpEventUseCase: RsvpEventUseCase,
    private val createRelationshipUseCase: CreateRelationshipUseCase,
    private val refreshTimelineUseCase: RefreshTimelineUseCase,
    private val refreshEventsUseCase: RefreshEventsUseCase,
    private val refreshChatUseCase: RefreshChatUseCase,
    private val sendChatFallbackUseCase: SendChatFallbackUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(FamilyUiState())
    val state: StateFlow<FamilyUiState> = _state.asStateFlow()

    init {
        val remembered = sessionStore.getRememberedLogin()
        val currentToken = sessionStore.currentToken()
        val hasActiveSession = !currentToken.isNullOrBlank()
        val authMemberId = sessionStore.getAuthMemberId()
        val tasks = sessionStore.getTasks()
        val templates = sessionStore.getTaskTemplates()
        val rewardPoints = sessionStore.getRewardPoints()
        val finance = sessionStore.getFinanceTransactions()
        val families = sessionStore.getFamilies()
        val familyJoinRequests = sessionStore.getFamilyJoinRequests()
        val familyRoles = sessionStore.getFamilyRoles()
        val clans = sessionStore.getClans()
        val clanJoinRequests = sessionStore.getClanJoinRequests()
        val clanDelegations = sessionStore.getClanDelegations()
        val socialLikes = sessionStore.getSocialLikes()
        val socialComments = sessionStore.getSocialComments()
        val activeFamilyId = sessionStore.getActiveFamilyId()
        val activeClanId = sessionStore.getActiveClanId()
        val familyEvents = sessionStore.getFamilyEvents()
        val clanTreePeople = sessionStore.getClanTreePeople()
        val clanTreeLinks = sessionStore.getClanTreeLinks()
        val ageYears = sessionStore.getAgeYears()
        _state.update {
            it.copy(
                isAuthenticated = hasActiveSession,
                authMemberId = authMemberId,
                authUsername = if (hasActiveSession) remembered.username else "",
                loginUsername = remembered.username,
                loginPassword = remembered.password,
                rememberLogin = remembered.remember,
                tasks = tasks,
                taskTemplates = templates,
                rewardPoints = rewardPoints,
                financeTransactions = finance,
                ageYears = ageYears,
                families = families,
                familyJoinRequests = familyJoinRequests,
                familyRoles = familyRoles,
                clans = clans,
                clanJoinRequests = clanJoinRequests,
                clanDelegations = clanDelegations,
                socialLikes = socialLikes,
                socialComments = socialComments,
                activeFamilyId = activeFamilyId,
                activeClanId = activeClanId,
                familyEvents = familyEvents,
                clanTreePeople = clanTreePeople,
                clanTreeLinks = clanTreeLinks
            )
        }
        if (hasActiveSession) {
            refreshAll()
            connectRealtimeChat(currentToken!!)
        }
        runDailyTaskResetIfNeeded()
    }

    fun updateLoginDraft(username: String? = null, password: String? = null, remember: Boolean? = null) {
        _state.update {
            it.copy(
                loginUsername = username ?: it.loginUsername,
                loginPassword = password ?: it.loginPassword,
                rememberLogin = remember ?: it.rememberLogin
            )
        }
    }

    fun clearNotice() {
        _state.update { it.copy(notice = null) }
    }

    fun selectMember(memberId: Long?) {
        _state.update { it.copy(selectedMemberId = memberId) }
    }

    fun selectedMember(): FamilyMember? {
        val selectedId = _state.value.selectedMemberId ?: return null
        return _state.value.members.firstOrNull { it.id == selectedId }
    }

    fun scheduleEventReminder(eventId: Long, minutesBefore: Int = 60) {
        val event = _state.value.events.firstOrNull { it.event.id == eventId } ?: return
        runCatching {
            reminderScheduler.schedule(
                eventId = event.event.id,
                eventTitle = event.event.title,
                eventTime = LocalDateTime.parse(event.event.eventTime),
                minutesBefore = minutesBefore
            )
        }.onSuccess {
            _state.update { it.copy(notice = "Đã đặt nhắc trước cho ${event.event.title}") }
        }.onFailure { throwable ->
            _state.update { it.copy(error = throwable.message ?: "Không thể đặt lịch nhắc") }
        }
    }

    fun login(username: String, password: String, rememberLogin: Boolean) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            runCatching { repository.login(username.trim(), password) }
                .onSuccess { auth ->
                    sessionStore.setToken(auth.token)
                    sessionStore.setAuthMemberId(auth.memberId)
                    sessionStore.saveRememberedLogin(username.trim(), password, rememberLogin)
                    _state.update {
                        it.copy(
                            isAuthenticated = true,
                            authMemberId = auth.memberId,
                            authUsername = auth.username,
                            currentUser = it.currentUser ?: FamilyMember(
                                id = auth.memberId,
                                username = auth.username,
                                fullName = auth.fullName,
                                email = null,
                                cityProvince = "",
                                bio = "",
                                birthDate = null,
                                avatarUrl = "",
                                role = auth.role,
                                createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                            ),
                            loginUsername = username.trim(),
                            loginPassword = if (rememberLogin) password else "",
                            rememberLogin = rememberLogin,
                            isLoading = false,
                            error = null,
                            awaitingEmailVerification = false
                        )
                    }
                    refreshAll()
                    connectRealtimeChat(auth.token)
                }
                .onFailure { throwable ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message ?: "Đăng nhập thất bại"
                        )
                    }
                }
        }
    }

    fun checkUsernameAvailability(username: String) {
        val normalized = username.trim()
        if (normalized.length < 3) {
            _state.update {
                it.copy(
                    usernameAvailability = false,
                    usernameAvailabilityMessage = "Tên đăng nhập tối thiểu 3 ký tự"
                )
            }
            return
        }
        viewModelScope.launch {
            runCatching { repository.checkUsername(normalized) }
                .onSuccess { (available, message) ->
                    _state.update {
                        it.copy(
                            usernameAvailability = available,
                            usernameAvailabilityMessage = message
                        )
                    }
                }
                .onFailure {
                    _state.update {
                        it.copy(
                            usernameAvailability = false,
                            usernameAvailabilityMessage = "Không thể kiểm tra tên đăng nhập"
                        )
                    }
                }
        }
    }

    fun register(fullName: String, username: String, cityProvince: String, email: String, password: String, birthDate: String) {
        viewModelScope.launch {
            val parsedBirthDate = parseBirthDate(birthDate)
            if (parsedBirthDate == null) {
                _state.update { it.copy(error = "Ngày sinh không hợp lệ. Dùng định dạng yyyy-MM-dd hoặc dd/MM/yyyy") }
                return@launch
            }
            if (cityProvince.isBlank()) {
                _state.update { it.copy(error = "Vui lòng nhập tỉnh/thành phố") }
                return@launch
            }
            if (password.length < 6) {
                _state.update { it.copy(error = "Mật khẩu tối thiểu 6 ký tự") }
                return@launch
            }
            _state.update { it.copy(isLoading = true, error = null) }
            runCatching { repository.register(username.trim(), password, fullName.trim(), cityProvince.trim(), email.trim()) }
                .onSuccess { message ->
                    val ageYears = calculateAgeYears(parsedBirthDate)
                    sessionStore.setAgeYears(ageYears)
                    _state.update {
                        it.copy(
                            loginUsername = username.trim(),
                            ageYears = ageYears,
                            isLoading = false,
                            error = null,
                            notice = message,
                            awaitingEmailVerification = true,
                            pendingRegisterUsername = username.trim(),
                            pendingRegisterEmail = email.trim(),
                            usernameAvailability = null,
                            usernameAvailabilityMessage = null
                        )
                    }
                }
                .onFailure { throwable ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message ?: "Đăng ký thất bại"
                        )
                    }
                }
        }
    }

    fun verifyEmail(code: String, rememberLogin: Boolean = false) {
        val username = _state.value.pendingRegisterUsername
        val email = _state.value.pendingRegisterEmail
        if (username.isBlank() || email.isBlank()) {
            _state.update { it.copy(error = "Thiếu thông tin đăng ký, vui lòng đăng ký lại") }
            return
        }
        if (code.length != 6 || !code.all { it.isDigit() }) {
            _state.update { it.copy(error = "Mã xác nhận gồm 6 chữ số") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            runCatching { repository.verifyEmail(username, email, code) }
                .onSuccess { message ->
                    sessionStore.saveRememberedLogin(username, "", rememberLogin)
                    _state.update {
                        it.copy(
                            isLoading = false,
                            awaitingEmailVerification = false,
                            pendingRegisterUsername = "",
                            pendingRegisterEmail = "",
                            notice = message
                        )
                    }
                }
                .onFailure { throwable ->
                    _state.update { it.copy(isLoading = false, error = throwable.message ?: "Xác nhận thất bại") }
                }
        }
    }

    fun resendVerificationCode(fullName: String, username: String, cityProvince: String, email: String, password: String) {
        if (password.length < 6) {
            _state.update { it.copy(error = "Mật khẩu tối thiểu 6 ký tự") }
            return
        }
        if (!email.trim().lowercase().endsWith("@gmail.com")) {
            _state.update { it.copy(error = "Email phải có đuôi @gmail.com") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            runCatching {
                repository.register(
                    username = username.trim(),
                    password = password,
                    fullName = fullName.trim(),
                    cityProvince = cityProvince.trim(),
                    email = email.trim()
                )
            }.onSuccess { message ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        notice = message,
                        awaitingEmailVerification = true,
                        pendingRegisterUsername = username.trim(),
                        pendingRegisterEmail = email.trim()
                    )
                }
            }.onFailure { throwable ->
                _state.update { it.copy(isLoading = false, error = throwable.message ?: "Gửi lại mã thất bại") }
            }
        }
    }

    fun logout() {
        chatRealtimeClient.disconnect()
        sessionStore.setToken(null)
        sessionStore.setAuthMemberId(null)
        val remembered = sessionStore.getRememberedLogin()
        _state.value = FamilyUiState(
            loginUsername = remembered.username,
            loginPassword = remembered.password,
            rememberLogin = remembered.remember,
            tasks = sessionStore.getTasks(),
            taskTemplates = sessionStore.getTaskTemplates(),
            rewardPoints = sessionStore.getRewardPoints(),
            financeTransactions = sessionStore.getFinanceTransactions(),
            ageYears = sessionStore.getAgeYears(),
            families = sessionStore.getFamilies(),
            familyJoinRequests = sessionStore.getFamilyJoinRequests(),
            familyRoles = sessionStore.getFamilyRoles(),
            clans = sessionStore.getClans(),
            clanJoinRequests = sessionStore.getClanJoinRequests(),
            clanDelegations = sessionStore.getClanDelegations(),
            socialLikes = sessionStore.getSocialLikes(),
            socialComments = sessionStore.getSocialComments(),
            activeFamilyId = sessionStore.getActiveFamilyId(),
            activeClanId = sessionStore.getActiveClanId(),
            familyEvents = sessionStore.getFamilyEvents(),
            clanTreePeople = sessionStore.getClanTreePeople(),
            clanTreeLinks = sessionStore.getClanTreeLinks(),
            pendingRegisterUsername = "",
            pendingRegisterEmail = "",
            awaitingEmailVerification = false
        )
    }

    fun setAgeYears(age: Int?) {
        val normalized = age?.coerceIn(0, 120)
        sessionStore.setAgeYears(normalized)
        _state.update {
            it.copy(
                ageYears = normalized,
                activeClanId = if ((normalized ?: 16) < 16) null else it.activeClanId
            )
        }
        if ((normalized ?: 16) < 16) {
            sessionStore.setActiveClanId(null)
        }
    }

    private fun runDailyTaskResetIfNeeded() {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val last = sessionStore.getLastDailyTaskResetDate()
        if (last == today) return

        val resetTasks = _state.value.tasks.map { task ->
            if (task.resetDaily && !task.canceled) {
                task.copy(
                    status = "TODO",
                    rewarded = false,
                    dueDate = today
                )
            } else {
                task
            }
        }
        sessionStore.saveTasks(resetTasks)
        sessionStore.setLastDailyTaskResetDate(today)
        _state.update { it.copy(tasks = resetTasks) }
    }

    private fun activeFamilyParentIds(state: FamilyUiState = _state.value): Set<Long> {
        val familyId = state.activeFamilyId ?: return emptySet()
        return state.familyRoles
            .filter { it.familyId == familyId && (it.role == "PARENT_FATHER" || it.role == "PARENT_MOTHER") }
            .map { it.memberId }
            .toSet()
    }

    private fun isCurrentUserParentInActiveFamily(): Boolean {
        val me = currentUserId() ?: return false
        return activeFamilyParentIds().contains(me)
    }

    private fun isCurrentUserClanHeadOrDelegate(permission: String): Boolean {
        val state = _state.value
        val me = currentUserId() ?: return false
        val clanId = state.activeClanId ?: return false
        val clan = state.clans.firstOrNull { it.id == clanId } ?: return false
        if (clan.ownerId == me) return true
        return state.clanDelegations.any {
            it.clanId == clanId && it.memberId == me && it.permissions.contains(permission)
        }
    }

    private fun currentUserId(): Long? {
        return _state.value.currentUser?.id ?: _state.value.authMemberId
    }

    private fun joinedFamilyForCurrentUser(state: FamilyUiState = _state.value): FamilyGroup? {
        val me = currentUserId() ?: return null
        return state.families.firstOrNull { me in it.memberIds }
    }

    private fun joinedClanForCurrentUser(state: FamilyUiState = _state.value): ClanGroup? {
        val me = currentUserId() ?: return null
        return state.clans.firstOrNull { me in it.memberIds }
    }

    fun createFamily(name: String, ownerRole: String = "PARENT_FATHER") {
        val currentUserId = currentUserId()
        if (currentUserId == null) {
            _state.update { it.copy(error = "Không xác định được người dùng hiện tại, vui lòng đăng nhập lại") }
            return
        }
        if (name.isBlank()) {
            _state.update { it.copy(error = "Tên gia đình không được để trống") }
            return
        }
        if (joinedFamilyForCurrentUser() != null) {
            _state.update { it.copy(error = "Bạn đã thuộc một gia đình, hãy rời gia đình hiện tại trước") }
            return
        }
        val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val id = System.currentTimeMillis()
        val code = generateUniqueCode("FAM", _state.value.families.map { it.code }.toSet())
        val family = FamilyGroup(
            id = id,
            name = name.trim(),
            code = code,
            ownerId = currentUserId,
            ownerRole = ownerRole,
            memberIds = listOf(currentUserId),
            createdAt = now
        )
        val updated = listOf(family) + _state.value.families
        val role = FamilyMemberRoleAssignment(
            familyId = id,
            memberId = currentUserId,
            role = ownerRole,
            assignedBy = currentUserId,
            assignedAt = now
        )
        val roles = listOf(role) + _state.value.familyRoles
        sessionStore.saveFamilies(updated)
        sessionStore.saveFamilyRoles(roles)
        sessionStore.setActiveFamilyId(family.id)
        _state.update { it.copy(families = updated, familyRoles = roles, activeFamilyId = family.id, notice = "Đã tạo gia đình mới") }
    }

    fun joinFamilyByCode(code: String) {
        requestJoinFamilyByCode(code)
    }

    fun requestJoinFamilyByCode(code: String) {
        val currentUserId = currentUserId() ?: return
        if (joinedFamilyForCurrentUser() != null) {
            _state.update { it.copy(error = "Bạn đã thuộc một gia đình") }
            return
        }
        val match = _state.value.families.firstOrNull { it.code.equals(code.trim(), ignoreCase = true) }
        if (match == null) {
            _state.update { it.copy(error = "Không tìm thấy mã gia đình") }
            return
        }
        val request = FamilyJoinRequest(
            id = System.currentTimeMillis(),
            familyId = match.id,
            memberId = currentUserId,
            status = "PENDING",
            createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
        val updatedRequests = _state.value.familyJoinRequests
            .filterNot { it.familyId == match.id && it.memberId == currentUserId && it.status == "PENDING" }
            .plus(request)
        val updatedFamilies = _state.value.families.map { family ->
            if (family.id == match.id) {
                family.copy(pendingMemberIds = (family.pendingMemberIds + currentUserId).distinct())
            } else {
                family
            }
        }
        sessionStore.saveFamilies(updatedFamilies)
        sessionStore.saveFamilyJoinRequests(updatedRequests)
        _state.update {
            it.copy(
                families = updatedFamilies,
                familyJoinRequests = updatedRequests,
                notice = "Đã gửi đơn xin vào gia đình"
            )
        }
    }

    fun leaveCurrentFamily() {
        val me = currentUserId() ?: return
        val state = _state.value
        val family = joinedFamilyForCurrentUser(state) ?: return
        val remaining = family.memberIds.filter { it != me }
        val updatedFamilies = state.families.mapNotNull {
            if (it.id != family.id) return@mapNotNull it
            if (remaining.isEmpty()) {
                null
            } else {
                val nextOwner = if (it.ownerId == me) remaining.first() else it.ownerId
                it.copy(ownerId = nextOwner, memberIds = remaining)
            }
        }
        val updatedRoles = state.familyRoles.filterNot { it.familyId == family.id && it.memberId == me }
        val updatedRequests = state.familyJoinRequests.filterNot { it.familyId == family.id && it.memberId == me }
        sessionStore.saveFamilies(updatedFamilies)
        sessionStore.saveFamilyRoles(updatedRoles)
        sessionStore.saveFamilyJoinRequests(updatedRequests)
        sessionStore.setActiveFamilyId(null)
        _state.update {
            it.copy(
                families = updatedFamilies,
                familyRoles = updatedRoles,
                familyJoinRequests = updatedRequests,
                activeFamilyId = null,
                notice = "Đã rời khỏi gia đình"
            )
        }
    }

    fun reviewFamilyJoinRequest(requestId: Long, approve: Boolean) {
        val state = _state.value
        val familyId = state.activeFamilyId ?: return
        val reviewerId = currentUserId() ?: return
        val family = state.families.firstOrNull { it.id == familyId } ?: return
        if (family.ownerId != reviewerId) return

        val request = state.familyJoinRequests.firstOrNull { it.id == requestId && it.familyId == familyId } ?: return
        val reviewed = state.familyJoinRequests.map {
            if (it.id == requestId) {
                it.copy(
                    status = if (approve) "APPROVED" else "REJECTED",
                    reviewedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    reviewedBy = reviewerId
                )
            } else {
                it
            }
        }
        val updatedFamilies = state.families.map {
            if (it.id == familyId) {
                val newMembers = if (approve) (it.memberIds + request.memberId).distinct() else it.memberIds
                it.copy(memberIds = newMembers, pendingMemberIds = it.pendingMemberIds - request.memberId)
            } else {
                it
            }
        }
        val updatedRoles = if (approve) {
            val existed = state.familyRoles.any { it.familyId == familyId && it.memberId == request.memberId }
            if (existed) {
                state.familyRoles
            } else {
                state.familyRoles + FamilyMemberRoleAssignment(
                    familyId = familyId,
                    memberId = request.memberId,
                    role = "CHILD",
                    assignedBy = reviewerId,
                    assignedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                )
            }
        } else {
            state.familyRoles
        }
        sessionStore.saveFamilyJoinRequests(reviewed)
        sessionStore.saveFamilies(updatedFamilies)
        sessionStore.saveFamilyRoles(updatedRoles)
        _state.update {
            it.copy(
                familyJoinRequests = reviewed,
                families = updatedFamilies,
                familyRoles = updatedRoles,
                notice = "Đã xử lý đơn xin vào gia đình"
            )
        }
    }

    fun addMemberToActiveFamily(@Suppress("UNUSED_PARAMETER") memberId: Long, @Suppress("UNUSED_PARAMETER") role: String = "CHILD") {
        _state.update { it.copy(error = "Thành viên chỉ có thể gia nhập gia đình bằng mã") }
    }

    fun transferFamilyOwnership(toMemberId: Long) {
        val state = _state.value
        val currentUserId = state.currentUser?.id ?: return
        val familyId = state.activeFamilyId ?: return
        val family = state.families.firstOrNull { it.id == familyId } ?: return
        if (family.ownerId != currentUserId || !family.memberIds.contains(toMemberId)) return
        val updated = state.families.map {
            if (it.id == familyId) it.copy(ownerId = toMemberId) else it
        }
        sessionStore.saveFamilies(updated)
        _state.update { it.copy(families = updated, notice = "Đã nhường vị trí gia chủ") }
    }

    fun createClan(name: String, ancestralAddress: String?) {
        val currentUserId = currentUserId()
        if (currentUserId == null) {
            _state.update { it.copy(error = "Không xác định được người dùng hiện tại, vui lòng đăng nhập lại") }
            return
        }
        if (name.isBlank()) {
            _state.update { it.copy(error = "Tên dòng họ không được để trống") }
            return
        }
        if (joinedClanForCurrentUser() != null) {
            _state.update { it.copy(error = "Bạn đã thuộc một dòng họ, hãy rời dòng họ hiện tại trước") }
            return
        }
        val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val id = System.currentTimeMillis()
        val code = generateUniqueCode("CLN", _state.value.clans.map { it.code }.toSet())
        val clan = ClanGroup(
            id = id,
            name = name.trim(),
            code = code,
            ancestralAddress = ancestralAddress?.trim()?.ifBlank { null },
            ownerId = currentUserId,
            memberIds = listOf(currentUserId),
            createdAt = now
        )
        val updated = listOf(clan) + _state.value.clans
        sessionStore.saveClans(updated)
        sessionStore.setActiveClanId(clan.id)
        _state.update { it.copy(clans = updated, activeClanId = clan.id, notice = "Đã tạo dòng họ mới") }
    }

    fun joinClanByCode(code: String) {
        requestJoinClanByCode(code)
    }

    fun requestJoinClanByCode(code: String) {
        val currentUser = _state.value.currentUser ?: return
        if (joinedClanForCurrentUser() != null) {
            _state.update { it.copy(error = "Bạn đã thuộc một dòng họ") }
            return
        }
        val match = _state.value.clans.firstOrNull { it.code.equals(code.trim(), ignoreCase = true) }
        if (match == null) {
            _state.update { it.copy(error = "Không tìm thấy mã dòng họ") }
            return
        }
        val request = ClanJoinRequest(
            id = System.currentTimeMillis(),
            clanId = match.id,
            memberId = currentUser.id,
            status = "PENDING",
            createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
        val updatedRequests = _state.value.clanJoinRequests
            .filterNot { it.clanId == match.id && it.memberId == currentUser.id && it.status == "PENDING" }
            .plus(request)
        val updatedClans = _state.value.clans.map { clan ->
            if (clan.id == match.id) clan.copy(pendingMemberIds = (clan.pendingMemberIds + currentUser.id).distinct()) else clan
        }
        sessionStore.saveClanJoinRequests(updatedRequests)
        sessionStore.saveClans(updatedClans)
        _state.update { it.copy(clans = updatedClans, clanJoinRequests = updatedRequests, notice = "Đã gửi đơn xin vào họ") }
    }

    fun leaveCurrentClan() {
        val me = currentUserId() ?: return
        val state = _state.value
        val clan = joinedClanForCurrentUser(state) ?: return
        val remaining = clan.memberIds.filter { it != me }
        val updatedClans = state.clans.mapNotNull {
            if (it.id != clan.id) return@mapNotNull it
            if (remaining.isEmpty()) {
                null
            } else {
                val nextOwner = if (it.ownerId == me) remaining.first() else it.ownerId
                it.copy(ownerId = nextOwner, memberIds = remaining, delegateIds = it.delegateIds - me, pendingMemberIds = it.pendingMemberIds - me)
            }
        }
        val updatedDelegations = state.clanDelegations.filterNot { it.clanId == clan.id && it.memberId == me }
        val updatedRequests = state.clanJoinRequests.filterNot { it.clanId == clan.id && it.memberId == me }
        sessionStore.saveClans(updatedClans)
        sessionStore.saveClanDelegations(updatedDelegations)
        sessionStore.saveClanJoinRequests(updatedRequests)
        sessionStore.setActiveClanId(null)
        _state.update {
            it.copy(
                clans = updatedClans,
                clanDelegations = updatedDelegations,
                clanJoinRequests = updatedRequests,
                activeClanId = null,
                notice = "Đã rời khỏi dòng họ"
            )
        }
    }

    fun addMemberToActiveClan(@Suppress("UNUSED_PARAMETER") memberId: Long) {
        _state.update { it.copy(error = "Thành viên chỉ có thể gia nhập dòng họ bằng mã") }
    }

    fun reviewClanJoinRequest(requestId: Long, approve: Boolean) {
        val state = _state.value
        val clanId = state.activeClanId ?: return
        val reviewerId = state.currentUser?.id ?: return
        if (!isCurrentUserClanHeadOrDelegate("MANAGE_MEMBERS")) return
        val request = state.clanJoinRequests.firstOrNull { it.id == requestId && it.clanId == clanId } ?: return
        val reviewed = state.clanJoinRequests.map {
            if (it.id == requestId) {
                it.copy(
                    status = if (approve) "APPROVED" else "REJECTED",
                    reviewedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    reviewedBy = reviewerId
                )
            } else {
                it
            }
        }
        val clans = state.clans.map { clan ->
            if (clan.id == clanId) {
                val members = if (approve) (clan.memberIds + request.memberId).distinct() else clan.memberIds
                clan.copy(memberIds = members, pendingMemberIds = clan.pendingMemberIds - request.memberId)
            } else {
                clan
            }
        }
        sessionStore.saveClanJoinRequests(reviewed)
        sessionStore.saveClans(clans)
        _state.update { it.copy(clanJoinRequests = reviewed, clans = clans, notice = "Đã xử lý đơn xin vào họ") }
    }

    fun setClanDelegation(memberId: Long, permissions: List<String>) {
        val state = _state.value
        val clanId = state.activeClanId ?: return
        val granterId = state.currentUser?.id ?: return
        val clan = state.clans.firstOrNull { it.id == clanId } ?: return
        if (clan.ownerId != granterId) return
        val updatedDelegations = state.clanDelegations
            .filterNot { it.clanId == clanId && it.memberId == memberId }
            .plus(
                ClanPermissionDelegation(
                    clanId = clanId,
                    memberId = memberId,
                    permissions = permissions,
                    grantedBy = granterId,
                    grantedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                )
            )
        val updatedClans = state.clans.map {
            if (it.id == clanId) it.copy(delegateIds = (it.delegateIds + memberId).distinct()) else it
        }
        sessionStore.saveClanDelegations(updatedDelegations)
        sessionStore.saveClans(updatedClans)
        _state.update { it.copy(clanDelegations = updatedDelegations, clans = updatedClans, notice = "Đã cấp quyền thay trưởng họ") }
    }

    fun revokeClanDelegation(memberId: Long) {
        val state = _state.value
        val clanId = state.activeClanId ?: return
        val me = state.currentUser?.id ?: return
        val clan = state.clans.firstOrNull { it.id == clanId } ?: return
        if (clan.ownerId != me) return
        val updatedDelegations = state.clanDelegations.filterNot { it.clanId == clanId && it.memberId == memberId }
        val updatedClans = state.clans.map {
            if (it.id == clanId) it.copy(delegateIds = it.delegateIds - memberId) else it
        }
        sessionStore.saveClanDelegations(updatedDelegations)
        sessionStore.saveClans(updatedClans)
        _state.update { it.copy(clanDelegations = updatedDelegations, clans = updatedClans, notice = "Đã thu hồi quyền đại diện") }
    }

    fun transferClanHead(toMemberId: Long) {
        val state = _state.value
        val clanId = state.activeClanId ?: return
        val me = state.currentUser?.id ?: return
        val clan = state.clans.firstOrNull { it.id == clanId } ?: return
        if (clan.ownerId != me || !clan.memberIds.contains(toMemberId)) return
        val updated = state.clans.map {
            if (it.id == clanId) it.copy(ownerId = toMemberId) else it
        }
        sessionStore.saveClans(updated)
        _state.update { it.copy(clans = updated, notice = "Đã nhường vị trí trưởng họ") }
    }

    fun addClanTreePerson(name: String, roleLabel: String, isDeceased: Boolean, linkedMemberId: Long? = null) {
        if (!isCurrentUserClanHeadOrDelegate("MANAGE_TREE")) return
        val clanId = _state.value.activeClanId ?: return
        val me = _state.value.currentUser?.id ?: return
        if (name.isBlank()) return
        val person = ClanTreePerson(
            id = System.currentTimeMillis(),
            clanId = clanId,
            name = name.trim(),
            roleLabel = roleLabel.trim().ifBlank { "Thành viên" },
            isDeceased = isDeceased,
            linkedMemberId = linkedMemberId,
            createdBy = me,
            createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
        val updated = listOf(person) + _state.value.clanTreePeople
        sessionStore.saveClanTreePeople(updated)
        _state.update { it.copy(clanTreePeople = updated, notice = "Đã thêm người vào sơ đồ họ") }
    }

    fun addClanTreeLink(fromPersonId: Long, toPersonId: Long, relationType: String) {
        if (!isCurrentUserClanHeadOrDelegate("MANAGE_TREE")) return
        val clanId = _state.value.activeClanId ?: return
        val me = _state.value.currentUser?.id ?: return
        val link = ClanTreeLink(
            id = System.currentTimeMillis(),
            clanId = clanId,
            fromPersonId = fromPersonId,
            toPersonId = toPersonId,
            relationType = relationType,
            createdBy = me,
            createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
        val updated = listOf(link) + _state.value.clanTreeLinks
        sessionStore.saveClanTreeLinks(updated)
        _state.update { it.copy(clanTreeLinks = updated, notice = "Đã liên kết cây gia phả") }
    }

    fun addClanRelative(
        anchorPersonId: Long,
        direction: String,
        name: String,
        roleLabel: String,
        isDeceased: Boolean
    ) {
        if (!isCurrentUserClanHeadOrDelegate("MANAGE_TREE")) return
        val state = _state.value
        val clanId = state.activeClanId ?: return
        val me = currentUserId() ?: return
        val anchor = state.clanTreePeople.firstOrNull { it.id == anchorPersonId && it.clanId == clanId } ?: return
        if (name.isBlank()) return

        val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val newPerson = ClanTreePerson(
            id = System.currentTimeMillis(),
            clanId = clanId,
            name = name.trim(),
            roleLabel = roleLabel.trim().ifBlank { "Thành viên" },
            isDeceased = isDeceased,
            linkedMemberId = null,
            createdBy = me,
            createdAt = now
        )

        val newLink = when (direction) {
            "PARENT" -> ClanTreeLink(
                id = System.currentTimeMillis() + 1,
                clanId = clanId,
                fromPersonId = newPerson.id,
                toPersonId = anchor.id,
                relationType = "PARENT_CHILD",
                createdBy = me,
                createdAt = now
            )

            "SPOUSE" -> ClanTreeLink(
                id = System.currentTimeMillis() + 2,
                clanId = clanId,
                fromPersonId = anchor.id,
                toPersonId = newPerson.id,
                relationType = "SPOUSE",
                createdBy = me,
                createdAt = now
            )

            else -> ClanTreeLink(
                id = System.currentTimeMillis() + 3,
                clanId = clanId,
                fromPersonId = anchor.id,
                toPersonId = newPerson.id,
                relationType = "PARENT_CHILD",
                createdBy = me,
                createdAt = now
            )
        }

        val updatedPeople = listOf(newPerson) + state.clanTreePeople
        val updatedLinks = listOf(newLink) + state.clanTreeLinks
        sessionStore.saveClanTreePeople(updatedPeople)
        sessionStore.saveClanTreeLinks(updatedLinks)
        _state.update {
            it.copy(
                clanTreePeople = updatedPeople,
                clanTreeLinks = updatedLinks,
                notice = "Đã thêm người vào cây gia phả"
            )
        }
    }

    fun createFamilyEvent(title: String, description: String, dateTime: LocalDateTime, location: String) {
        val state = _state.value
        val familyId = state.activeFamilyId ?: run {
            _state.update { it.copy(error = "Hãy chọn hoặc tạo gia đình trước") }
            return
        }
        val currentUserId = state.currentUser?.id ?: return
        if (title.isBlank() || location.isBlank()) return
        val event = FamilyLocalEvent(
            id = System.currentTimeMillis(),
            familyId = familyId,
            title = title.trim(),
            description = description.trim(),
            eventTime = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            location = location.trim(),
            createdBy = currentUserId,
            createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
        val updated = listOf(event) + state.familyEvents
        sessionStore.saveFamilyEvents(updated)
        _state.update { it.copy(familyEvents = updated, notice = "Đã tạo sự kiện gia đình") }
    }

    fun setActiveFamily(familyId: Long?) {
        sessionStore.setActiveFamilyId(familyId)
        _state.update { it.copy(activeFamilyId = familyId) }
        updateBirthdayAlerts()
    }

    fun setActiveClan(clanId: Long?) {
        sessionStore.setActiveClanId(clanId)
        _state.update { it.copy(activeClanId = clanId) }
    }

    fun toggleTargetLike(targetType: String, targetId: Long) {
        viewModelScope.launch {
            runCatching { repository.toggleTargetLike(targetType, targetId) }
                .onSuccess { refreshed ->
                    val others = _state.value.socialLikes.filterNot { it.targetType == refreshed.targetType && it.targetId == refreshed.targetId }
                    val updated = others + refreshed
                    sessionStore.saveSocialLikes(updated)
                    _state.update { it.copy(socialLikes = updated) }
                }
                .onFailure { throwable ->
                    _state.update { it.copy(error = throwable.message ?: "Thao tác thích thất bại") }
                }
        }
    }

    fun addSocialComment(targetType: String, targetId: Long, content: String, parentCommentId: Long? = null) {
        if (content.isBlank()) return
        viewModelScope.launch {
            runCatching { repository.addSocialComment(targetType, targetId, content.trim(), parentCommentId) }
                .onSuccess { saved ->
                    val updated = (_state.value.socialComments + saved).sortedByDescending { it.createdAt }
                    sessionStore.saveSocialComments(updated)
                    _state.update { it.copy(socialComments = updated) }
                }
                .onFailure { throwable ->
                    _state.update { it.copy(error = throwable.message ?: "Gửi bình luận thất bại") }
                }
        }
    }

    fun toggleSocialCommentLike(commentId: Long) {
        viewModelScope.launch {
            runCatching { repository.toggleCommentLike(commentId) }
                .onSuccess { refreshed ->
                    val updated = _state.value.socialComments.map { comment ->
                        if (comment.id == commentId) comment.copy(likedMemberIds = refreshed.memberIds) else comment
                    }
                    sessionStore.saveSocialComments(updated)
                    _state.update { it.copy(socialComments = updated) }
                }
                .onFailure { throwable ->
                    _state.update { it.copy(error = throwable.message ?: "Thao tác thích bình luận thất bại") }
                }
        }
    }

    fun updateProfile(fullName: String, cityProvince: String, birthDate: String?, bio: String) {
        if (fullName.isBlank() || cityProvince.isBlank()) {
            _state.update { it.copy(error = "Họ tên và quê quán không được để trống") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val normalizedBirthDate = parseBirthDate(birthDate)?.format(DateTimeFormatter.ISO_LOCAL_DATE)
            runCatching { repository.updateProfile(fullName.trim(), cityProvince.trim(), normalizedBirthDate, bio.trim()) }
                .onSuccess {
                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            notice = "Đã cập nhật thông tin cá nhân",
                            currentUser = state.currentUser?.copy(
                                fullName = it.fullName,
                                cityProvince = it.cityProvince,
                                birthDate = it.birthDate,
                                bio = it.bio
                            )
                        )
                    }
                    refreshAll()
                }
                .onFailure { throwable -> _state.update { it.copy(isLoading = false, error = throwable.message ?: "Cập nhật hồ sơ thất bại") } }
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        if (newPassword.length < 6) {
            _state.update { it.copy(error = "Mật khẩu mới tối thiểu 6 ký tự") }
            return
        }
        viewModelScope.launch {
            runCatching { repository.changePassword(currentPassword, newPassword) }
                .onSuccess { msg -> _state.update { it.copy(notice = msg, error = null) } }
                .onFailure { throwable -> _state.update { it.copy(error = throwable.message ?: "Đổi mật khẩu thất bại") } }
        }
    }

    fun requestOldEmailChange() {
        viewModelScope.launch {
            runCatching { repository.requestOldEmailChange() }
                .onSuccess { msg -> _state.update { it.copy(notice = msg, error = null) } }
                .onFailure { throwable -> _state.update { it.copy(error = throwable.message ?: "Không thể gửi mã xác nhận email cũ") } }
        }
    }

    fun confirmOldEmailChange(code: String) {
        viewModelScope.launch {
            runCatching { repository.confirmOldEmailChange(code.trim()) }
                .onSuccess { msg ->
                    val ticket = msg.substringAfter("TICKET:", missingDelimiterValue = "").trim().ifBlank { null }
                    _state.update { it.copy(notice = "Email cũ đã xác nhận", profileEmailChangeTicket = ticket, error = null) }
                }
                .onFailure { throwable -> _state.update { it.copy(error = throwable.message ?: "Xác nhận email cũ thất bại") } }
        }
    }

    fun requestNewEmailChange(newEmail: String) {
        val ticket = _state.value.profileEmailChangeTicket
        if (ticket.isNullOrBlank()) {
            _state.update { it.copy(error = "Bạn cần xác nhận email cũ trước") }
            return
        }
        viewModelScope.launch {
            runCatching { repository.requestNewEmailChange(ticket, newEmail.trim()) }
                .onSuccess { msg -> _state.update { it.copy(notice = msg, error = null) } }
                .onFailure { throwable -> _state.update { it.copy(error = throwable.message ?: "Không thể gửi mã xác nhận email mới") } }
        }
    }

    fun confirmNewEmailChange(newEmail: String, code: String) {
        val ticket = _state.value.profileEmailChangeTicket
        if (ticket.isNullOrBlank()) {
            _state.update { it.copy(error = "Bạn cần xác nhận email cũ trước") }
            return
        }
        viewModelScope.launch {
            runCatching { repository.confirmNewEmailChange(ticket, newEmail.trim(), code.trim()) }
                .onSuccess {
                    _state.update { it.copy(notice = "Đổi email thành công", profileEmailChangeTicket = null, error = null) }
                    refreshAll()
                }
                .onFailure { throwable -> _state.update { it.copy(error = throwable.message ?: "Xác nhận email mới thất bại") } }
        }
    }

    fun addTask(
        title: String,
        note: String,
        assignedMemberId: Long?,
        dueDate: LocalDate,
        points: Int,
        templateId: Long? = null,
        defaultStatus: String = "TODO"
    ) {
        if (!isCurrentUserParentInActiveFamily()) {
            _state.update { it.copy(error = "Chỉ cha/mẹ mới được giao việc") }
            return
        }
        if (title.isBlank()) return
        val now = LocalDateTime.now()
        val familyId = _state.value.activeFamilyId
        val creatorId = _state.value.currentUser?.id
        val task = FamilyTask(
            id = System.currentTimeMillis(),
            title = title.trim(),
            note = note.trim(),
            familyId = familyId,
            assignedMemberId = assignedMemberId,
            dueDate = dueDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
            status = defaultStatus,
            points = points.coerceAtLeast(1),
            rewarded = false,
            canceled = false,
            resetDaily = true,
            createdBy = creatorId,
            templateId = templateId,
            createdAt = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
        val updated = listOf(task) + _state.value.tasks
        sessionStore.saveTasks(updated)
        _state.update { it.copy(tasks = updated, notice = "Đã giao việc mới") }
    }

    fun addWeeklyTaskTemplate(title: String, note: String, weekday: Int, points: Int) {
        if (title.isBlank() || weekday !in 1..7) return
        val template = TaskTemplate(
            id = System.currentTimeMillis(),
            title = title.trim(),
            note = note.trim(),
            weekday = weekday,
            points = points.coerceAtLeast(1),
            active = true,
            createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
        val updated = listOf(template) + _state.value.taskTemplates
        sessionStore.saveTaskTemplates(updated)
        _state.update { it.copy(taskTemplates = updated, notice = "Đã lưu việc mẫu theo tuần") }
    }

    fun createTodayTasksFromTemplates() {
        val today = LocalDate.now()
        val weekday = today.dayOfWeek.value
        val current = _state.value
        val todayString = today.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val templatesToday = current.taskTemplates.filter { it.active && it.weekday == weekday }
        if (templatesToday.isEmpty()) {
            _state.update { it.copy(notice = "Hôm nay chưa có việc mẫu") }
            return
        }

        val existingTemplateIds = current.tasks
            .filter { it.dueDate == todayString && it.templateId != null }
            .mapNotNull { it.templateId }
            .toSet()

        val created = templatesToday
            .filterNot { it.id in existingTemplateIds }
            .map { template ->
                FamilyTask(
                    id = System.currentTimeMillis() + template.id,
                    title = template.title,
                    note = template.note,
                    familyId = current.activeFamilyId,
                    assignedMemberId = null,
                    dueDate = todayString,
                    status = "PENDING_ASSIGN",
                    points = template.points,
                    rewarded = false,
                    canceled = false,
                    resetDaily = true,
                    createdBy = current.currentUser?.id,
                    templateId = template.id,
                    createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                )
            }

        if (created.isEmpty()) {
            _state.update { it.copy(notice = "Việc mẫu hôm nay đã được tạo") }
            return
        }

        val updatedTasks = created + current.tasks
        sessionStore.saveTasks(updatedTasks)
        _state.update { it.copy(tasks = updatedTasks, notice = "Đã tạo việc mẫu cho hôm nay") }
    }

    fun assignTask(taskId: Long, memberId: Long) {
        val current = _state.value
        val updatedTasks = current.tasks.map { task ->
            if (task.id == taskId) {
                task.copy(
                    assignedMemberId = memberId,
                    status = if (task.status == "PENDING_ASSIGN") "TODO" else task.status
                )
            } else {
                task
            }
        }
        sessionStore.saveTasks(updatedTasks)
        _state.update { it.copy(tasks = updatedTasks, notice = "Đã phân công người làm") }
    }

    fun toggleTaskStatus(taskId: Long) {
        val current = _state.value
        val target = current.tasks.firstOrNull { it.id == taskId } ?: return
        if (target.canceled) {
            _state.update { it.copy(notice = "Nhiệm vụ đã hủy") }
            return
        }
        val me = current.currentUser?.id ?: return
        val parentIds = activeFamilyParentIds(current)
        val isParent = me in parentIds
        val isAssignee = target.assignedMemberId == me
        if (!isParent && !isAssignee) {
            _state.update { it.copy(error = "Bạn không có quyền cập nhật nhiệm vụ này") }
            return
        }
        if (target.status == "PENDING_ASSIGN" || target.assignedMemberId == null) {
            _state.update { it.copy(notice = "Cần phân công người làm trước") }
            return
        }
        val nextStatus = if (target.status == "DONE") "TODO" else "DONE"
        val nextRewarded = nextStatus == "DONE"

        val updatedTasks = current.tasks.map {
            if (it.id == taskId) it.copy(status = nextStatus, rewarded = nextRewarded) else it
        }

        val pointsByMember = current.rewardPoints.associate { it.memberId to it.points }.toMutableMap()
        val memberId = target.assignedMemberId
        val existing = pointsByMember[memberId] ?: 0
        val adjusted = when {
            target.status == "TODO" && nextStatus == "DONE" -> existing + target.points
            target.status == "DONE" && nextStatus == "TODO" -> (existing - target.points).coerceAtLeast(0)
            else -> existing
        }
        pointsByMember[memberId] = adjusted

        val updatedPoints = pointsByMember
            .map { RewardPoint(memberId = it.key, points = it.value) }
            .sortedByDescending { it.points }

        sessionStore.saveTasks(updatedTasks)
        sessionStore.saveRewardPoints(updatedPoints)
        _state.update { it.copy(tasks = updatedTasks, rewardPoints = updatedPoints) }
    }

    fun adjustTaskPoints(taskId: Long, newPoints: Int) {
        if (!isCurrentUserParentInActiveFamily()) return
        val current = _state.value
        val target = current.tasks.firstOrNull { it.id == taskId } ?: return
        if (target.canceled) return

        val oldPoints = target.points
        val updatedPointsValue = newPoints.coerceAtLeast(0)
        val updatedTasks = current.tasks.map {
            if (it.id == taskId) it.copy(points = updatedPointsValue) else it
        }

        var updatedRewardPoints = current.rewardPoints
        if (target.status == "DONE" && target.assignedMemberId != null) {
            val diff = updatedPointsValue - oldPoints
            if (diff != 0) {
                val map = updatedRewardPoints.associate { it.memberId to it.points }.toMutableMap()
                val memberId = target.assignedMemberId
                map[memberId] = ((map[memberId] ?: 0) + diff).coerceAtLeast(0)
                updatedRewardPoints = map.map { RewardPoint(memberId = it.key, points = it.value) }
                    .sortedByDescending { it.points }
            }
        }

        sessionStore.saveTasks(updatedTasks)
        sessionStore.saveRewardPoints(updatedRewardPoints)
        _state.update { it.copy(tasks = updatedTasks, rewardPoints = updatedRewardPoints, notice = "Đã điều chỉnh điểm") }
    }

    fun cancelTask(taskId: Long) {
        if (!isCurrentUserParentInActiveFamily()) return
        val current = _state.value
        val target = current.tasks.firstOrNull { it.id == taskId } ?: return
        if (target.canceled) return

        var updatedRewardPoints = current.rewardPoints
        if (target.status == "DONE" && target.assignedMemberId != null) {
            val map = updatedRewardPoints.associate { it.memberId to it.points }.toMutableMap()
            val memberId = target.assignedMemberId
            map[memberId] = ((map[memberId] ?: 0) - target.points).coerceAtLeast(0)
            updatedRewardPoints = map.map { RewardPoint(memberId = it.key, points = it.value) }
                .sortedByDescending { it.points }
        }

        val updatedTasks = current.tasks.map {
            if (it.id == taskId) it.copy(canceled = true, status = "CANCELED", rewarded = false) else it
        }

        sessionStore.saveTasks(updatedTasks)
        sessionStore.saveRewardPoints(updatedRewardPoints)
        _state.update { it.copy(tasks = updatedTasks, rewardPoints = updatedRewardPoints, notice = "Đã hủy nhiệm vụ") }
    }

    fun addFinanceTransaction(
        title: String,
        amount: Double,
        category: String,
        paidByMemberId: Long,
        participantIds: List<Long>,
        note: String
    ) {
        if (title.isBlank() || amount <= 0.0 || participantIds.isEmpty()) return
        val now = LocalDateTime.now()
        val tx = FinanceTransaction(
            id = System.currentTimeMillis(),
            title = title.trim(),
            amount = amount,
            category = category.trim().ifBlank { "Khác" },
            paidByMemberId = paidByMemberId,
            participantIds = participantIds.distinct(),
            note = note.trim(),
            isCanceled = false,
            createdAt = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
        val updated = listOf(tx) + _state.value.financeTransactions
        sessionStore.saveFinanceTransactions(updated)
        _state.update { it.copy(financeTransactions = updated, notice = "Đã thêm chi tiêu chung") }
    }

    fun cancelFinanceTransaction(transactionId: Long) {
        val updated = _state.value.financeTransactions.map { tx ->
            if (tx.id == transactionId) tx.copy(isCanceled = true) else tx
        }
        sessionStore.saveFinanceTransactions(updated)
        _state.update { it.copy(financeTransactions = updated, notice = "Đã hủy khoản chi") }
    }

    fun refreshAll() {
        if (!state.value.isAuthenticated) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            runCatching { loadFamilySnapshot() }
                .onSuccess { snapshot ->
                    sessionStore.setAuthMemberId(snapshot.currentUser?.id)
                    _state.update {
                        it.copy(
                            isLoading = false,
                            authMemberId = snapshot.currentUser?.id ?: it.authMemberId,
                            currentUser = snapshot.currentUser,
                            members = snapshot.members,
                            tree = snapshot.tree,
                            timeline = snapshot.timeline,
                            events = snapshot.events,
                            chatMessages = snapshot.chat,
                            dashboard = snapshot.dashboard,
                            birthdayAlerts = calculateBirthdayAlerts(snapshot.members, snapshot.currentUser?.id),
                            error = null
                        )
                    }
                    refreshSocialState(snapshot.timeline.map { it.post.id }, snapshot.events.map { it.event.id })
                }
                .onFailure { throwable ->
                    _state.update { it.copy(isLoading = false, error = throwable.message ?: "Đã xảy ra lỗi") }
                }
        }
    }

    fun createPost(content: String, imageUrl: String = "") {
        viewModelScope.launch {
            runCatching { createPostUseCase(content, imageUrl) }
                .onSuccess { refreshTimelineAndDashboard() }
                .onFailure { throwable -> _state.update { it.copy(error = throwable.message ?: "Đăng bài thất bại") } }
        }
    }

    fun addComment(postId: Long, content: String) {
        viewModelScope.launch {
            runCatching { addCommentUseCase(postId, content) }
                .onSuccess { refreshTimelineAndDashboard() }
                .onFailure { throwable -> _state.update { it.copy(error = throwable.message ?: "Gửi bình luận thất bại") } }
        }
    }

    fun createEvent(title: String, description: String, dateTime: LocalDateTime, location: String) {
        viewModelScope.launch {
            runCatching { createEventUseCase(title, description, dateTime, location) }
                .onSuccess { refreshEventsAndDashboard() }
                .onFailure { throwable -> _state.update { it.copy(error = throwable.message ?: "Tạo sự kiện thất bại") } }
        }
    }

    fun rsvp(eventId: Long, status: String) {
        viewModelScope.launch {
            runCatching { rsvpEventUseCase(eventId, status) }
                .onSuccess { refreshEventsAndDashboard() }
                .onFailure { throwable -> _state.update { it.copy(error = throwable.message ?: "Phản hồi tham gia thất bại") } }
        }
    }

    fun sendChat(message: String) {
        if (message.isBlank()) return
        chatRealtimeClient.send(message.trim())
    }

    fun createRelationship(toMemberId: Long, type: String) {
        viewModelScope.launch {
            val currentId = _state.value.currentUser?.id ?: return@launch
            runCatching { createRelationshipUseCase(currentId, toMemberId, type) }
                .onSuccess { refreshTreeOnly() }
                .onFailure { throwable -> _state.update { it.copy(error = throwable.message ?: "Cập nhật mối quan hệ thất bại") } }
        }
    }

    fun sendChatFallbackHttp(message: String) {
        if (message.isBlank()) return
        viewModelScope.launch {
            runCatching { sendChatFallbackUseCase(message.trim()) }
                .onSuccess { refreshChatAndDashboard() }
                .onFailure { throwable -> _state.update { it.copy(error = throwable.message ?: "Gửi tin nhắn thất bại") } }
        }
    }

    private fun refreshTimelineAndDashboard() {
        viewModelScope.launch {
            runCatching { refreshTimelineUseCase() }
                .onSuccess { (timeline, dashboard) ->
                    _state.update { it.copy(timeline = timeline, dashboard = dashboard) }
                    refreshSocialState(timeline.map { it.post.id }, _state.value.events.map { it.event.id })
                }
                .onFailure { throwable ->
                    _state.update { it.copy(error = throwable.message ?: "Làm mới bảng tin thất bại") }
                }
        }
    }

    private fun refreshEventsAndDashboard() {
        viewModelScope.launch {
            runCatching { refreshEventsUseCase() }
                .onSuccess { (events, dashboard) ->
                    _state.update { it.copy(events = events, dashboard = dashboard) }
                    refreshSocialState(_state.value.timeline.map { it.post.id }, events.map { it.event.id })
                }
                .onFailure { throwable ->
                    _state.update { it.copy(error = throwable.message ?: "Làm mới sự kiện thất bại") }
                }
        }
    }

    private fun refreshChatAndDashboard() {
        viewModelScope.launch {
            runCatching { refreshChatUseCase() }
                .onSuccess { (chat, dashboard) ->
                    _state.update { it.copy(chatMessages = chat, dashboard = dashboard) }
                }
                .onFailure { throwable ->
                    _state.update { it.copy(error = throwable.message ?: "Làm mới trò chuyện thất bại") }
                }
        }
    }

    private fun refreshTreeOnly() {
        viewModelScope.launch {
            val current = _state.value.currentUser?.id ?: return@launch
            runCatching { repository.tree(current) }
                .onSuccess { tree -> _state.update { it.copy(tree = tree) } }
                .onFailure { throwable ->
                    _state.update { it.copy(error = throwable.message ?: "Làm mới cây gia phả thất bại") }
                }
        }
    }

    private fun connectRealtimeChat(token: String) {
        chatRealtimeClient.connect(
            token = token,
            onMessage = {
                viewModelScope.launch {
                    refreshChatAndDashboard()
                }
            },
            onError = { error ->
                _state.update { it.copy(realtimeConnected = false, error = error) }
            }
        )
        _state.update { it.copy(realtimeConnected = true) }
    }

    private fun refreshSocialState(postIds: List<Long>, eventIds: List<Long>) {
        viewModelScope.launch {
            runCatching {
                val postLikes = if (postIds.isNotEmpty()) repository.socialLikes("POST", postIds) else emptyList()
                val eventLikes = if (eventIds.isNotEmpty()) repository.socialLikes("EVENT", eventIds) else emptyList()
                val postComments = if (postIds.isNotEmpty()) repository.socialComments("POST", postIds) else emptyList()
                val eventComments = if (eventIds.isNotEmpty()) repository.socialComments("EVENT", eventIds) else emptyList()
                postLikes + eventLikes to postComments + eventComments
            }.onSuccess { (likes, comments) ->
                sessionStore.saveSocialLikes(likes)
                sessionStore.saveSocialComments(comments)
                _state.update { it.copy(socialLikes = likes, socialComments = comments) }
            }
        }
    }

    private fun generateUniqueCode(prefix: String, existing: Set<String>, segmentLength: Int = 8): String {
        val alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        repeat(20) {
            val body = buildString {
                repeat(segmentLength) { append(alphabet[Random.nextInt(alphabet.length)]) }
            }
            val code = "$prefix-$body"
            if (code !in existing) return code
        }
        return "$prefix-${System.currentTimeMillis().toString().takeLast(segmentLength)}"
    }

    private fun updateBirthdayAlerts(members: List<FamilyMember> = _state.value.members, currentUserId: Long? = _state.value.currentUser?.id) {
        _state.update { it.copy(birthdayAlerts = calculateBirthdayAlerts(members, currentUserId)) }
    }

    private fun calculateBirthdayAlerts(members: List<FamilyMember>, currentUserId: Long?): List<String> {
        val state = _state.value
        val activeFamilyId = state.activeFamilyId ?: return emptyList()
        val family = state.families.firstOrNull { it.id == activeFamilyId } ?: return emptyList()
        val today = LocalDate.now()
        return members
            .asSequence()
            .filter { it.id in family.memberIds }
            .filter { it.id != currentUserId }
            .filter { member ->
                val birth = parseBirthDate(member.birthDate) ?: return@filter false
                birth.month == today.month && birth.dayOfMonth == today.dayOfMonth
            }
            .map { "Hôm nay là sinh nhật của ${it.fullName}. Hãy chuẩn bị một bất ngờ!" }
            .toList()
    }

    private fun parseBirthDate(raw: String?): LocalDate? {
        if (raw.isNullOrBlank()) return null
        val normalized = raw.trim()
        val patterns = listOf(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
        )
        patterns.forEach { formatter ->
            val asLocalDate = runCatching { LocalDate.parse(normalized, formatter) }.getOrNull()
            if (asLocalDate != null) return asLocalDate
            val asDateTime = runCatching { LocalDateTime.parse(normalized, formatter) }.getOrNull()
            if (asDateTime != null) return asDateTime.toLocalDate()
        }
        return null
    }

    private fun calculateAgeYears(birthDate: LocalDate): Int {
        val today = LocalDate.now()
        return Period.between(birthDate, today).years.coerceAtLeast(0)
    }

    override fun onCleared() {
        chatRealtimeClient.disconnect()
        super.onCleared()
    }
}
