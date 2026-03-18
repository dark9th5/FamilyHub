package com.family.app.data.session

import android.content.Context
import com.family.app.domain.model.ClanJoinRequest
import com.family.app.domain.model.ClanPermissionDelegation
import com.family.app.domain.model.ClanTreeLink
import com.family.app.domain.model.ClanTreePerson
import com.family.app.domain.model.ClanGroup
import com.family.app.domain.model.FamilyGroup
import com.family.app.domain.model.FamilyLocalEvent
import com.family.app.domain.model.FamilyMemberRoleAssignment
import com.family.app.domain.model.FamilyTask
import com.family.app.domain.model.FinanceTransaction
import com.family.app.domain.model.RewardPoint
import com.family.app.domain.model.TaskTemplate
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RememberedLogin(
    val username: String,
    val password: String,
    val remember: Boolean
)

class SessionStore(context: Context) {
    private val prefs = context.getSharedPreferences("family_session", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _token = MutableStateFlow<String?>(prefs.getString(KEY_TOKEN, null))
    val token: StateFlow<String?> = _token.asStateFlow()

    fun setToken(value: String?) {
        _token.value = value
        prefs.edit().putString(KEY_TOKEN, value).apply()
    }

    fun currentToken(): String? = _token.value

    fun saveRememberedLogin(username: String, password: String, remember: Boolean) {
        prefs.edit().apply {
            putBoolean(KEY_REMEMBER, remember)
            if (remember) {
                putString(KEY_USERNAME, username)
                putString(KEY_PASSWORD, password)
            } else {
                remove(KEY_USERNAME)
                remove(KEY_PASSWORD)
            }
        }.apply()
    }

    fun getRememberedLogin(): RememberedLogin {
        val remember = prefs.getBoolean(KEY_REMEMBER, false)
        return RememberedLogin(
            username = prefs.getString(KEY_USERNAME, "") ?: "",
            password = prefs.getString(KEY_PASSWORD, "") ?: "",
            remember = remember
        )
    }

    fun getTasks(): List<FamilyTask> {
        val json = prefs.getString(KEY_TASKS, null) ?: return emptyList()
        val type = object : TypeToken<List<FamilyTask>>() {}.type
        return runCatching { gson.fromJson<List<FamilyTask>>(json, type) ?: emptyList() }.getOrDefault(emptyList())
    }

    fun saveTasks(tasks: List<FamilyTask>) {
        prefs.edit().putString(KEY_TASKS, gson.toJson(tasks)).apply()
    }

    fun getRewardPoints(): List<RewardPoint> {
        val json = prefs.getString(KEY_POINTS, null) ?: return emptyList()
        val type = object : TypeToken<List<RewardPoint>>() {}.type
        return runCatching { gson.fromJson<List<RewardPoint>>(json, type) ?: emptyList() }.getOrDefault(emptyList())
    }

    fun getTaskTemplates(): List<TaskTemplate> {
        val json = prefs.getString(KEY_TASK_TEMPLATES, null) ?: return emptyList()
        val type = object : TypeToken<List<TaskTemplate>>() {}.type
        return runCatching { gson.fromJson<List<TaskTemplate>>(json, type) ?: emptyList() }.getOrDefault(emptyList())
    }

    fun saveRewardPoints(points: List<RewardPoint>) {
        prefs.edit().putString(KEY_POINTS, gson.toJson(points)).apply()
    }

    fun saveTaskTemplates(templates: List<TaskTemplate>) {
        prefs.edit().putString(KEY_TASK_TEMPLATES, gson.toJson(templates)).apply()
    }

    fun getFinanceTransactions(): List<FinanceTransaction> {
        val json = prefs.getString(KEY_FINANCE, null) ?: return emptyList()
        val type = object : TypeToken<List<FinanceTransaction>>() {}.type
        return runCatching { gson.fromJson<List<FinanceTransaction>>(json, type) ?: emptyList() }.getOrDefault(emptyList())
    }

    fun saveFinanceTransactions(transactions: List<FinanceTransaction>) {
        prefs.edit().putString(KEY_FINANCE, gson.toJson(transactions)).apply()
    }

    fun getFamilies(): List<FamilyGroup> {
        val json = prefs.getString(KEY_FAMILIES, null) ?: return emptyList()
        val type = object : TypeToken<List<FamilyGroup>>() {}.type
        return runCatching { gson.fromJson<List<FamilyGroup>>(json, type) ?: emptyList() }.getOrDefault(emptyList())
    }

    fun saveFamilies(families: List<FamilyGroup>) {
        prefs.edit().putString(KEY_FAMILIES, gson.toJson(families)).apply()
    }

    fun getFamilyRoles(): List<FamilyMemberRoleAssignment> {
        val json = prefs.getString(KEY_FAMILY_ROLES, null) ?: return emptyList()
        val type = object : TypeToken<List<FamilyMemberRoleAssignment>>() {}.type
        return runCatching { gson.fromJson<List<FamilyMemberRoleAssignment>>(json, type) ?: emptyList() }.getOrDefault(emptyList())
    }

    fun saveFamilyRoles(roles: List<FamilyMemberRoleAssignment>) {
        prefs.edit().putString(KEY_FAMILY_ROLES, gson.toJson(roles)).apply()
    }

    fun getClans(): List<ClanGroup> {
        val json = prefs.getString(KEY_CLANS, null) ?: return emptyList()
        val type = object : TypeToken<List<ClanGroup>>() {}.type
        return runCatching { gson.fromJson<List<ClanGroup>>(json, type) ?: emptyList() }.getOrDefault(emptyList())
    }

    fun saveClans(clans: List<ClanGroup>) {
        prefs.edit().putString(KEY_CLANS, gson.toJson(clans)).apply()
    }

    fun getClanJoinRequests(): List<ClanJoinRequest> {
        val json = prefs.getString(KEY_CLAN_JOIN_REQUESTS, null) ?: return emptyList()
        val type = object : TypeToken<List<ClanJoinRequest>>() {}.type
        return runCatching { gson.fromJson<List<ClanJoinRequest>>(json, type) ?: emptyList() }.getOrDefault(emptyList())
    }

    fun saveClanJoinRequests(requests: List<ClanJoinRequest>) {
        prefs.edit().putString(KEY_CLAN_JOIN_REQUESTS, gson.toJson(requests)).apply()
    }

    fun getClanDelegations(): List<ClanPermissionDelegation> {
        val json = prefs.getString(KEY_CLAN_DELEGATIONS, null) ?: return emptyList()
        val type = object : TypeToken<List<ClanPermissionDelegation>>() {}.type
        return runCatching { gson.fromJson<List<ClanPermissionDelegation>>(json, type) ?: emptyList() }.getOrDefault(emptyList())
    }

    fun saveClanDelegations(items: List<ClanPermissionDelegation>) {
        prefs.edit().putString(KEY_CLAN_DELEGATIONS, gson.toJson(items)).apply()
    }

    fun getActiveFamilyId(): Long? =
        if (prefs.contains(KEY_ACTIVE_FAMILY_ID)) prefs.getLong(KEY_ACTIVE_FAMILY_ID, -1L).takeIf { it > 0L } else null

    fun setActiveFamilyId(id: Long?) {
        prefs.edit().apply {
            if (id == null) remove(KEY_ACTIVE_FAMILY_ID) else putLong(KEY_ACTIVE_FAMILY_ID, id)
        }.apply()
    }

    fun getActiveClanId(): Long? =
        if (prefs.contains(KEY_ACTIVE_CLAN_ID)) prefs.getLong(KEY_ACTIVE_CLAN_ID, -1L).takeIf { it > 0L } else null

    fun setActiveClanId(id: Long?) {
        prefs.edit().apply {
            if (id == null) remove(KEY_ACTIVE_CLAN_ID) else putLong(KEY_ACTIVE_CLAN_ID, id)
        }.apply()
    }

    fun getFamilyEvents(): List<FamilyLocalEvent> {
        val json = prefs.getString(KEY_FAMILY_EVENTS, null) ?: return emptyList()
        val type = object : TypeToken<List<FamilyLocalEvent>>() {}.type
        return runCatching { gson.fromJson<List<FamilyLocalEvent>>(json, type) ?: emptyList() }.getOrDefault(emptyList())
    }

    fun saveFamilyEvents(events: List<FamilyLocalEvent>) {
        prefs.edit().putString(KEY_FAMILY_EVENTS, gson.toJson(events)).apply()
    }

    fun getClanTreePeople(): List<ClanTreePerson> {
        val json = prefs.getString(KEY_CLAN_TREE_PEOPLE, null) ?: return emptyList()
        val type = object : TypeToken<List<ClanTreePerson>>() {}.type
        return runCatching { gson.fromJson<List<ClanTreePerson>>(json, type) ?: emptyList() }.getOrDefault(emptyList())
    }

    fun saveClanTreePeople(items: List<ClanTreePerson>) {
        prefs.edit().putString(KEY_CLAN_TREE_PEOPLE, gson.toJson(items)).apply()
    }

    fun getClanTreeLinks(): List<ClanTreeLink> {
        val json = prefs.getString(KEY_CLAN_TREE_LINKS, null) ?: return emptyList()
        val type = object : TypeToken<List<ClanTreeLink>>() {}.type
        return runCatching { gson.fromJson<List<ClanTreeLink>>(json, type) ?: emptyList() }.getOrDefault(emptyList())
    }

    fun saveClanTreeLinks(items: List<ClanTreeLink>) {
        prefs.edit().putString(KEY_CLAN_TREE_LINKS, gson.toJson(items)).apply()
    }

    fun getAgeYears(): Int? =
        if (prefs.contains(KEY_AGE_YEARS)) prefs.getInt(KEY_AGE_YEARS, -1).takeIf { it >= 0 } else null

    fun setAgeYears(age: Int?) {
        prefs.edit().apply {
            if (age == null) remove(KEY_AGE_YEARS) else putInt(KEY_AGE_YEARS, age)
        }.apply()
    }

    fun getLastDailyTaskResetDate(): String? = prefs.getString(KEY_LAST_DAILY_TASK_RESET, null)

    fun setLastDailyTaskResetDate(date: String) {
        prefs.edit().putString(KEY_LAST_DAILY_TASK_RESET, date).apply()
    }

    companion object {
        private const val KEY_TOKEN = "token"
        private const val KEY_REMEMBER = "remember_login"
        private const val KEY_USERNAME = "remember_username"
        private const val KEY_PASSWORD = "remember_password"
        private const val KEY_TASKS = "family_tasks"
        private const val KEY_TASK_TEMPLATES = "family_task_templates"
        private const val KEY_POINTS = "family_reward_points"
        private const val KEY_FINANCE = "family_finance_transactions"
        private const val KEY_FAMILIES = "family_groups"
        private const val KEY_FAMILY_ROLES = "family_member_roles"
        private const val KEY_CLANS = "clan_groups"
        private const val KEY_CLAN_JOIN_REQUESTS = "clan_join_requests"
        private const val KEY_CLAN_DELEGATIONS = "clan_permission_delegations"
        private const val KEY_ACTIVE_FAMILY_ID = "active_family_id"
        private const val KEY_ACTIVE_CLAN_ID = "active_clan_id"
        private const val KEY_FAMILY_EVENTS = "family_local_events"
        private const val KEY_CLAN_TREE_PEOPLE = "clan_tree_people"
        private const val KEY_CLAN_TREE_LINKS = "clan_tree_links"
        private const val KEY_AGE_YEARS = "age_years"
        private const val KEY_LAST_DAILY_TASK_RESET = "last_daily_task_reset"
    }
}
