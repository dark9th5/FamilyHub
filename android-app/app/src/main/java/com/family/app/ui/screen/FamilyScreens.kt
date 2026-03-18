package com.family.app.ui.screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Cottage
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.family.app.domain.model.FamilyMember
import com.family.app.domain.model.Tree
import com.family.app.ui.components.EmptyStateCard
import com.family.app.ui.components.HeroHeader
import com.family.app.ui.components.PremiumCard
import com.family.app.ui.components.PremiumInput
import com.family.app.ui.components.PremiumScreenBackground
import com.family.app.ui.components.PrimaryActionButton
import com.family.app.ui.components.SecondaryActionButton
import com.family.app.ui.components.StatusChip
import com.family.app.ui.components.TertiaryGhostButton
import com.family.app.ui.theme.FamilyRadius
import com.family.app.ui.theme.FamilySpacing
import com.family.app.viewmodel.FamilyViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun LoginScreen(
    viewModel: FamilyViewModel,
    onLoggedIn: () -> Unit,
    onNavigateRegister: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var username by remember(state.loginUsername) { mutableStateOf(state.loginUsername) }
    var password by remember(state.loginPassword) { mutableStateOf(state.loginPassword) }
    var rememberLogin by remember(state.rememberLogin) { mutableStateOf(state.rememberLogin) }

    PremiumScreenBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(FamilySpacing.md),
            contentAlignment = Alignment.Center
        ) {
            PremiumCard(modifier = Modifier.fillMaxWidth()) {
                HeroHeader(
                    title = "FamilyNet",
                    subtitle = "Kết nối gia đình và dòng họ",
                    trailing = {
                        Surface(
                            shape = RoundedCornerShape(FamilyRadius.sm),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Login,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(FamilySpacing.xs)
                            )
                        }
                    }
                )

                PremiumInput(
                    value = username,
                    onValueChange = {
                        username = it
                        viewModel.updateLoginDraft(username = it)
                    },
                    label = "Tên đăng nhập"
                )
                PremiumInput(
                    value = password,
                    onValueChange = {
                        password = it
                        viewModel.updateLoginDraft(password = it)
                    },
                    label = "Mật khẩu",
                    isPassword = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = rememberLogin,
                            onCheckedChange = {
                                rememberLogin = it
                                viewModel.updateLoginDraft(remember = it)
                            }
                        )
                        Text("Ghi nhớ", style = MaterialTheme.typography.bodyMedium)
                    }
                    TertiaryGhostButton(text = "Đăng ký", onClick = onNavigateRegister)
                }

                PrimaryActionButton(
                    text = if (state.isLoading) "Đang đăng nhập..." else "Đăng nhập",
                    onClick = { viewModel.login(username, password, rememberLogin) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading && username.isNotBlank() && password.isNotBlank()
                )
                state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        }
    }

    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) onLoggedIn()
    }
}

@Composable
fun RegisterScreen(
    viewModel: FamilyViewModel,
    onRegistered: () -> Unit,
    onNavigateLogin: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf(LocalDate.now().minusYears(18).format(DateTimeFormatter.ISO_LOCAL_DATE)) }
    var rememberLogin by remember(state.rememberLogin) { mutableStateOf(state.rememberLogin) }

    PremiumScreenBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(FamilySpacing.md),
            contentAlignment = Alignment.Center
        ) {
            PremiumCard(modifier = Modifier.fillMaxWidth()) {
                HeroHeader(
                    title = "Tạo tài khoản",
                    subtitle = "Mỗi tài khoản đều là người dùng bình thường"
                )

                PremiumInput(fullName, { fullName = it }, label = "Họ và tên")
                PremiumInput(username, { username = it }, label = "Tên đăng nhập")
                PremiumInput(password, { password = it }, label = "Mật khẩu", isPassword = true)
                DatePickerRow(
                    label = "Ngày sinh",
                    value = birthDate,
                    onDateSelected = { birthDate = it }
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = rememberLogin, onCheckedChange = { rememberLogin = it })
                    Text("Ghi nhớ đăng nhập", style = MaterialTheme.typography.bodyMedium)
                }

                PrimaryActionButton(
                    text = if (state.isLoading) "Đang tạo tài khoản..." else "Đăng ký",
                    onClick = { viewModel.register(fullName, username, password, rememberLogin, birthDate) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading && fullName.isNotBlank() && username.isNotBlank() && password.isNotBlank() && birthDate.isNotBlank()
                )
                TertiaryGhostButton(
                    text = "Đã có tài khoản? Đăng nhập",
                    onClick = onNavigateLogin,
                    modifier = Modifier.fillMaxWidth()
                )
                state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        }
    }

    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) onRegistered()
    }
}

@Composable
fun HomeScreen(viewModel: FamilyViewModel) {
    val state by viewModel.state.collectAsState()
    val me = state.currentUser
    val activeClan = state.clans.firstOrNull { it.id == state.activeClanId }
    val ownPosts = state.timeline.filter { it.post.authorId == me?.id }
    val relations = relatedMemberLabels(me?.id ?: -1L, state.tree, state.members)
    val clanMemberIds = activeClan?.memberIds.orEmpty().toSet()
    val clanUpcoming = state.events.filter { e -> e.host?.id in clanMemberIds }.take(6)

    PremiumScreenBackground {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(FamilySpacing.md),
            verticalArrangement = Arrangement.spacedBy(FamilySpacing.md)
        ) {
            item {
                HeroHeader(
                    title = "Trang chủ",
                    subtitle = "Tổng quan cá nhân",
                    trailing = {
                        StatusChip(
                            text = if (state.realtimeConnected) "Trực tuyến" else "Ngoại tuyến",
                            isOnline = state.realtimeConnected
                        )
                    }
                )
            }

            item {
                if (state.birthdayAlerts.isNotEmpty()) {
                    PremiumCard {
                        SectionTitle("Nhắc sinh nhật hôm nay")
                        state.birthdayAlerts.forEach { alert ->
                            MetaLineCard("Nhắc nhở", alert)
                        }
                    }
                }
            }

            item {
                PremiumCard {
                    SectionTitle("Sự kiện sắp tới")
                    if (clanUpcoming.isEmpty()) {
                        EmptyStateCard("Chưa có sự kiện", "Vào màn Dòng họ hoặc Gia đình để tạo sự kiện")
                    } else {
                        clanUpcoming.forEach {
                            MetaLineCard(it.event.title, "${it.event.location} • ${it.event.eventTime}")
                        }
                    }
                }
            }

            item {
                PremiumCard {
                    SectionTitle("Bài viết của bản thân")
                    if (ownPosts.isEmpty()) {
                        EmptyStateCard("Bạn chưa có bài viết", "Vào Bảng tin để đăng bài")
                    } else {
                        ownPosts.take(5).forEach {
                            MetaLineCard(it.post.createdAt, it.post.content.take(120))
                        }
                    }
                }
            }

            item {
                PremiumCard {
                    SectionTitle("Quan hệ gia đình")
                    if (relations.isEmpty()) {
                        EmptyStateCard("Chưa có liên kết", "Vào màn Dòng họ để tạo quan hệ")
                    } else {
                        relations.take(8).forEach { (label, member) ->
                            MetaLineCard(label, member.fullName)
                        }
                    }
                }
            }

            item {
                PremiumCard {
                    SectionTitle("Dòng họ hiện tại")
                    Text(activeClan?.name ?: "Chưa tham gia dòng họ", style = MaterialTheme.typography.titleLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(FamilySpacing.xs)) {
                        MetaBadge("Mã", activeClan?.code ?: "-", Modifier.weight(1f))
                        MetaBadge("Thành viên", activeClan?.memberIds?.size?.toString() ?: "0", Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun FamilyHubScreen(viewModel: FamilyViewModel) {
    val state by viewModel.state.collectAsState()
    val me = state.currentUser
    val activeFamily = state.families.firstOrNull { it.id == state.activeFamilyId }
    val familyMemberIds = activeFamily?.memberIds.orEmpty().toSet()
    val isOwner = activeFamily?.ownerId == me?.id
    val familyId = activeFamily?.id
    val parentIds = state.familyRoles
        .filter { it.familyId == familyId && (it.role == "PARENT_FATHER" || it.role == "PARENT_MOTHER") }
        .map { it.memberId }
        .toSet()
    val isParent = me?.id in parentIds

    var familyName by remember { mutableStateOf("") }
    var ownerRole by remember { mutableStateOf("PARENT_FATHER") }
    var familyCode by remember { mutableStateOf("") }
    var selectedMemberId by remember { mutableStateOf<Long?>(null) }
    var selectedMemberRole by remember { mutableStateOf("CHILD") }
    var expandedMember by remember { mutableStateOf(false) }

    var taskTitle by remember { mutableStateOf("") }
    var taskNote by remember { mutableStateOf("") }
    var taskPoints by remember { mutableStateOf("10") }
    var taskDueDate by remember { mutableStateOf(LocalDate.now()) }

    var spendTitle by remember { mutableStateOf("") }
    var spendAmount by remember { mutableStateOf("") }
    var spendCategory by remember { mutableStateOf("Sinh hoạt") }
    var spendNote by remember { mutableStateOf("") }

    var eventTitle by remember { mutableStateOf("") }
    var eventDesc by remember { mutableStateOf("") }
    var eventLocation by remember { mutableStateOf("") }
    var familyEventDateTime by remember {
        mutableStateOf(
            LocalDateTime.now()
                .plusDays(2)
                .withHour(19)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
        )
    }

    val familyTasks = state.tasks.filter { it.assignedMemberId in familyMemberIds || it.assignedMemberId == null }
    val familySpending = state.financeTransactions.filterNot { it.isCanceled }
    val familyEvents = state.familyEvents.filter { it.familyId == activeFamily?.id }
    val categories = listOf("Sinh hoạt", "Ăn uống", "Giáo dục", "Y tế", "Di chuyển", "Khác")
    val memberRoles = listOf("PARENT_FATHER", "PARENT_MOTHER", "CHILD", "GRANDPARENT", "SPOUSE")

    PremiumScreenBackground {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(FamilySpacing.md),
            verticalArrangement = Arrangement.spacedBy(FamilySpacing.md)
        ) {
            item {
                HeroHeader(
                    title = "Gia đình",
                    subtitle = "Công việc gia đình • Chi tiêu gia đình • Sự kiện gia đình",
                    trailing = {
                        Surface(shape = RoundedCornerShape(FamilyRadius.sm), color = MaterialTheme.colorScheme.primaryContainer) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Cottage, contentDescription = null)
                                Icon(Icons.Filled.Favorite, contentDescription = null)
                            }
                        }
                    }
                )
            }

            item {
                if (state.birthdayAlerts.isNotEmpty()) {
                    PremiumCard {
                        SectionTitle("Nhắc sinh nhật người thân")
                        state.birthdayAlerts.forEach { alert ->
                            MetaLineCard("Hôm nay", alert)
                        }
                    }
                }
            }

            item {
                PremiumCard {
                    SectionTitle("Tạo/Gia nhập gia đình")
                    PremiumInput(familyName, { familyName = it }, label = "Tên gia đình")
                    SecondaryActionButton(
                        text = if (ownerRole == "PARENT_FATHER") "Gia chủ: Cha" else "Gia chủ: Mẹ",
                        onClick = {
                            ownerRole = if (ownerRole == "PARENT_FATHER") "PARENT_MOTHER" else "PARENT_FATHER"
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    PrimaryActionButton(
                        text = "Tạo gia đình",
                        onClick = {
                            viewModel.createFamily(familyName, ownerRole)
                            familyName = ""
                        },
                        enabled = familyName.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    PremiumInput(familyCode, { familyCode = it }, label = "Mã gia đình")
                    SecondaryActionButton(
                        text = "Gia nhập theo mã",
                        onClick = {
                            viewModel.joinFamilyByCode(familyCode)
                            familyCode = ""
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (state.families.isNotEmpty()) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(FamilySpacing.xs)) {
                            items(state.families) {
                                SecondaryActionButton(text = it.name, onClick = { viewModel.setActiveFamily(it.id) })
                            }
                        }
                    }
                    MetaLineCard("Đang chọn", activeFamily?.name ?: "Chưa có")
                    FamilyCodeCard(activeFamily?.code)
                }
            }

            if (activeFamily != null && isOwner) {
                item {
                    PremiumCard {
                        SectionTitle("Chủ nhà thêm người")
                        SecondaryActionButton(
                            text = when (selectedMemberRole) {
                                "PARENT_FATHER" -> "Vai trò: Cha"
                                "PARENT_MOTHER" -> "Vai trò: Mẹ"
                                "CHILD" -> "Vai trò: Con"
                                "GRANDPARENT" -> "Vai trò: Ông/Bà"
                                else -> "Vai trò: Vợ/Chồng"
                            },
                            onClick = {
                                val idx = memberRoles.indexOf(selectedMemberRole)
                                selectedMemberRole = memberRoles[(idx + 1) % memberRoles.size]
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        SecondaryActionButton(
                            text = state.members.firstOrNull { it.id == selectedMemberId }?.fullName ?: "Chọn thành viên",
                            onClick = { expandedMember = true },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(expanded = expandedMember, onDismissRequest = { expandedMember = false }) {
                            state.members.filter { it.id !in familyMemberIds }.forEach { m ->
                                DropdownMenuItem(
                                    text = { Text(m.fullName) },
                                    onClick = {
                                        selectedMemberId = m.id
                                        expandedMember = false
                                    }
                                )
                            }
                        }
                        PrimaryActionButton(
                            text = "Thêm thành viên",
                            onClick = {
                                selectedMemberId?.let { viewModel.addMemberToActiveFamily(it, selectedMemberRole) }
                                selectedMemberId = null
                            },
                            enabled = selectedMemberId != null,
                            modifier = Modifier.fillMaxWidth()
                        )
                        val transferTargets = activeFamily.memberIds.filter { it != activeFamily.ownerId }
                        if (transferTargets.isNotEmpty()) {
                            SecondaryActionButton(
                                text = "Nhường vị trí gia chủ cho ${state.members.firstOrNull { it.id == transferTargets.first() }?.fullName ?: transferTargets.first()}",
                                onClick = { viewModel.transferFamilyOwnership(transferTargets.first()) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            item {
                PremiumCard {
                    SectionTitle("Công việc gia đình")
                    Text("Mỗi sáng nhiệm vụ sẽ tự reset về trạng thái mới.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    PremiumInput(taskTitle, { taskTitle = it }, label = "Tên công việc")
                    PremiumInput(taskNote, { taskNote = it }, label = "Ghi chú", minLines = 2)
                    PremiumInput(taskPoints, { taskPoints = it.filter(Char::isDigit) }, label = "Điểm")
                    DatePickerRow(
                        label = "Hạn hoàn thành",
                        value = taskDueDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                        onDateSelected = { taskDueDate = LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE) }
                    )
                    PrimaryActionButton(
                        text = "Tạo việc",
                        onClick = {
                            viewModel.addTask(
                                title = taskTitle,
                                note = taskNote,
                                assignedMemberId = state.members.firstOrNull { it.id in familyMemberIds && it.id !in parentIds }?.id,
                                dueDate = taskDueDate,
                                points = taskPoints.toIntOrNull() ?: 10
                            )
                            taskTitle = ""
                            taskNote = ""
                        },
                        enabled = taskTitle.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (familyTasks.isEmpty()) {
                        EmptyStateCard("Chưa có công việc", "Tạo việc để cả nhà cùng theo dõi")
                    } else {
                        familyTasks.take(12).forEach { task ->
                            val assignee = state.members.firstOrNull { it.id == task.assignedMemberId }?.fullName ?: "Chưa phân công"
                            MetaLineCard(task.title, "$assignee • ${task.dueDate} • ${task.status} • ${task.points} điểm")
                            Row(horizontalArrangement = Arrangement.spacedBy(FamilySpacing.xs)) {
                                if (task.assignedMemberId == me?.id || isParent) {
                                    SecondaryActionButton(
                                        text = if (task.status == "DONE") "Hoàn tác" else "Hoàn thành",
                                        onClick = { viewModel.toggleTaskStatus(task.id) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (isParent) {
                                    SecondaryActionButton(
                                        text = "+5 điểm",
                                        onClick = { viewModel.adjustTaskPoints(task.id, task.points + 5) },
                                        modifier = Modifier.weight(1f)
                                    )
                                    SecondaryActionButton(
                                        text = "-5 điểm",
                                        onClick = { viewModel.adjustTaskPoints(task.id, (task.points - 5).coerceAtLeast(0)) },
                                        modifier = Modifier.weight(1f)
                                    )
                                    SecondaryActionButton(
                                        text = "Hủy nhiệm vụ",
                                        onClick = { viewModel.cancelTask(task.id) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                PremiumCard {
                    SectionTitle("Chi tiêu gia đình")
                    PremiumInput(spendTitle, { spendTitle = it }, label = "Khoản chi")
                    PremiumInput(spendAmount, { spendAmount = it.filter { c -> c.isDigit() || c == '.' } }, label = "Số tiền")
                    Row(horizontalArrangement = Arrangement.spacedBy(FamilySpacing.xs)) {
                        SecondaryActionButton(
                            text = spendCategory,
                            onClick = { spendCategory = categories[(categories.indexOf(spendCategory) + 1) % categories.size] },
                            modifier = Modifier.weight(1f)
                        )
                        PremiumInput(spendNote, { spendNote = it }, label = "Ghi chú", modifier = Modifier.weight(1f))
                    }
                    PrimaryActionButton(
                        text = "Thêm chi tiêu",
                        onClick = {
                            val amount = spendAmount.toDoubleOrNull() ?: return@PrimaryActionButton
                            val payer = me?.id ?: return@PrimaryActionButton
                            viewModel.addFinanceTransaction(
                                title = spendTitle,
                                amount = amount,
                                category = spendCategory,
                                paidByMemberId = payer,
                                participantIds = familyMemberIds.toList().ifEmpty { listOf(payer) },
                                note = spendNote
                            )
                            spendTitle = ""
                            spendAmount = ""
                            spendNote = ""
                        },
                        enabled = spendTitle.isNotBlank() && spendAmount.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Tổng chi: %,.0fđ".format(familySpending.sumOf { it.amount }))
                    familySpending.take(8).forEach { tx ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(tx.title, modifier = Modifier.weight(1f))
                            SecondaryActionButton(text = "Hủy", onClick = { viewModel.cancelFinanceTransaction(tx.id) })
                        }
                    }
                }
            }

            item {
                PremiumCard {
                    SectionTitle("Sự kiện gia đình")
                    PremiumInput(eventTitle, { eventTitle = it }, label = "Tên sự kiện")
                    PremiumInput(eventDesc, { eventDesc = it }, label = "Mô tả", minLines = 2)
                    PremiumInput(eventLocation, { eventLocation = it }, label = "Địa điểm")
                    DateTimePickerRow(
                        label = "Ngày giờ sự kiện",
                        value = familyEventDateTime,
                        onDateTimeSelected = { familyEventDateTime = it }
                    )
                    PrimaryActionButton(
                        text = "Tạo sự kiện gia đình",
                        onClick = {
                            viewModel.createFamilyEvent(
                                title = eventTitle,
                                description = eventDesc,
                                dateTime = familyEventDateTime,
                                location = eventLocation
                            )
                            eventTitle = ""
                            eventDesc = ""
                            eventLocation = ""
                        },
                        enabled = eventTitle.isNotBlank() && eventLocation.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (familyEvents.isEmpty()) {
                        EmptyStateCard("Chưa có sự kiện gia đình", "Tạo sự kiện đầu tiên")
                    } else {
                        familyEvents.take(8).forEach {
                            MetaLineCard(it.title, "${it.location} • ${it.eventTime}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClanHubScreen(viewModel: FamilyViewModel) {
    val state by viewModel.state.collectAsState()
    val me = state.currentUser
    val activeClan = state.clans.firstOrNull { it.id == state.activeClanId }
    val clanMemberIds = activeClan?.memberIds.orEmpty().toSet()
    val isHead = activeClan?.ownerId == me?.id
    val pendingRequests = state.clanJoinRequests.filter { it.clanId == activeClan?.id && it.status == "PENDING" }
    val clanPeople = state.clanTreePeople.filter { it.clanId == activeClan?.id }
    val clanLinks = state.clanTreeLinks.filter { it.clanId == activeClan?.id }

    var clanName by remember { mutableStateOf("") }
    var clanCode by remember { mutableStateOf("") }
    var selectedMemberId by remember { mutableStateOf<Long?>(null) }
    var expandedMember by remember { mutableStateOf(false) }
    var treeName by remember { mutableStateOf("") }
    var treeRole by remember { mutableStateOf("Tổ tiên") }
    var treeDeceased by remember { mutableStateOf(false) }
    var linkFromId by remember { mutableStateOf<Long?>(null) }
    var linkToId by remember { mutableStateOf<Long?>(null) }
    var relationType by remember { mutableStateOf("PARENT_CHILD") }
    var eventTitle by remember { mutableStateOf("") }
    var eventDesc by remember { mutableStateOf("") }
    var eventLocation by remember { mutableStateOf("") }
    var clanEventDateTime by remember {
        mutableStateOf(
            LocalDateTime.now()
                .plusDays(3)
                .withHour(19)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
        )
    }

    val clanEvents = state.events.filter { it.host?.id in clanMemberIds }

    PremiumScreenBackground {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(FamilySpacing.md),
            verticalArrangement = Arrangement.spacedBy(FamilySpacing.md)
        ) {
            item {
                HeroHeader(
                    title = "Dòng họ",
                    subtitle = "Tạo/gia nhập dòng họ • Sự kiện dòng họ • Quản lý thành viên",
                    trailing = {
                        Surface(shape = RoundedCornerShape(FamilyRadius.sm), color = MaterialTheme.colorScheme.primaryContainer) {
                            Icon(Icons.Filled.Diversity3, contentDescription = null, modifier = Modifier.padding(8.dp))
                        }
                    }
                )
            }

            item {
                PremiumCard {
                    SectionTitle("Tạo/Gia nhập dòng họ")
                    Row(horizontalArrangement = Arrangement.spacedBy(FamilySpacing.xs)) {
                        PremiumInput(clanName, { clanName = it }, label = "Tên dòng họ", modifier = Modifier.weight(1f))
                        PrimaryActionButton(
                            text = "Tạo",
                            onClick = {
                                viewModel.createClan(clanName)
                                clanName = ""
                            },
                            enabled = clanName.isNotBlank(),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(FamilySpacing.xs)) {
                        PremiumInput(clanCode, { clanCode = it }, label = "Mã dòng họ", modifier = Modifier.weight(1f))
                        SecondaryActionButton(
                            text = "Gia nhập",
                            onClick = {
                                viewModel.requestJoinClanByCode(clanCode)
                                clanCode = ""
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (state.clans.isNotEmpty()) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(FamilySpacing.xs)) {
                            items(state.clans) {
                                SecondaryActionButton(text = it.name, onClick = { viewModel.setActiveClan(it.id) })
                            }
                        }
                    }
                    MetaLineCard("Đang chọn", activeClan?.name ?: "Chưa có")
                    MetaLineCard("Mã", activeClan?.code ?: "-")
                }
            }

            if (activeClan != null && isHead) {
                item {
                    PremiumCard {
                        SectionTitle("Trưởng họ thêm người")
                        SecondaryActionButton(
                            text = state.members.firstOrNull { it.id == selectedMemberId }?.fullName ?: "Chọn thành viên",
                            onClick = { expandedMember = true },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(expanded = expandedMember, onDismissRequest = { expandedMember = false }) {
                            state.members.filter { it.id !in clanMemberIds }.forEach { m ->
                                DropdownMenuItem(
                                    text = { Text(m.fullName) },
                                    onClick = {
                                        selectedMemberId = m.id
                                        expandedMember = false
                                    }
                                )
                            }
                        }
                        PrimaryActionButton(
                            text = "Thêm thành viên",
                            onClick = {
                                selectedMemberId?.let { viewModel.addMemberToActiveClan(it) }
                                selectedMemberId = null
                            },
                            enabled = selectedMemberId != null,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (pendingRequests.isNotEmpty()) {
                            Text("Đơn xin vào họ", style = MaterialTheme.typography.titleMedium)
                            pendingRequests.forEach { req ->
                                val name = state.members.firstOrNull { it.id == req.memberId }?.fullName ?: req.memberId.toString()
                                Row(horizontalArrangement = Arrangement.spacedBy(FamilySpacing.xs)) {
                                    Text(name, modifier = Modifier.weight(1f))
                                    SecondaryActionButton(text = "Duyệt", onClick = { viewModel.reviewClanJoinRequest(req.id, true) })
                                    SecondaryActionButton(text = "Từ chối", onClick = { viewModel.reviewClanJoinRequest(req.id, false) })
                                }
                            }
                        }
                        val transferTargets = activeClan.memberIds.filter { it != activeClan.ownerId }
                        if (transferTargets.isNotEmpty()) {
                            SecondaryActionButton(
                                text = "Nhường vị trí trưởng họ",
                                onClick = { viewModel.transferClanHead(transferTargets.first()) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            if (activeClan != null && isHead) {
                item {
                    PremiumCard {
                        SectionTitle("Phân quyền đại diện")
                        SecondaryActionButton(
                            text = state.members.firstOrNull { it.id == selectedMemberId }?.fullName ?: "Chọn người nhận quyền",
                            onClick = { expandedMember = true },
                            modifier = Modifier.fillMaxWidth()
                        )
                        PrimaryActionButton(
                            text = "Cấp quyền thay trưởng họ",
                            onClick = {
                                selectedMemberId?.let {
                                    viewModel.setClanDelegation(it, listOf("MANAGE_EVENTS", "MANAGE_MEMBERS", "MANAGE_TREE"))
                                }
                            },
                            enabled = selectedMemberId != null,
                            modifier = Modifier.fillMaxWidth()
                        )
                        SecondaryActionButton(
                            text = "Thu hồi quyền",
                            onClick = { selectedMemberId?.let { viewModel.revokeClanDelegation(it) } },
                            enabled = selectedMemberId != null,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            item {
                PremiumCard {
                    SectionTitle("Sự kiện dòng họ")
                    PremiumInput(eventTitle, { eventTitle = it }, label = "Tên sự kiện")
                    PremiumInput(eventDesc, { eventDesc = it }, label = "Mô tả", minLines = 2)
                    PremiumInput(eventLocation, { eventLocation = it }, label = "Địa điểm")
                    DateTimePickerRow(
                        label = "Ngày giờ sự kiện",
                        value = clanEventDateTime,
                        onDateTimeSelected = { clanEventDateTime = it }
                    )
                    PrimaryActionButton(
                        text = "Tạo sự kiện dòng họ",
                        onClick = {
                            viewModel.createEvent(eventTitle, eventDesc, clanEventDateTime, eventLocation)
                            eventTitle = ""
                            eventDesc = ""
                            eventLocation = ""
                        },
                        enabled = eventTitle.isNotBlank() && eventLocation.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (clanEvents.isEmpty()) {
                        EmptyStateCard("Chưa có sự kiện dòng họ", "Tạo sự kiện để kết nối họ tộc")
                    } else {
                        clanEvents.take(10).forEach {
                            MetaLineCard(it.event.title, "${it.event.location} • ${it.event.eventTime}")
                        }
                    }
                }
            }

            item {
                PremiumCard {
                    SectionTitle("Sơ đồ gia phả dòng họ (trưởng tộc khai báo)")
                    if (isHead) {
                        PremiumInput(treeName, { treeName = it }, label = "Tên người trong sơ đồ")
                        PremiumInput(treeRole, { treeRole = it }, label = "Vai trò/ghi chú")
                        Row(horizontalArrangement = Arrangement.spacedBy(FamilySpacing.xs)) {
                            SecondaryActionButton(
                                text = if (treeDeceased) "Đã mất" else "Còn sống",
                                onClick = { treeDeceased = !treeDeceased },
                                modifier = Modifier.weight(1f)
                            )
                            PrimaryActionButton(
                                text = "Thêm người",
                                onClick = {
                                    viewModel.addClanTreePerson(treeName, treeRole, treeDeceased)
                                    treeName = ""
                                    treeRole = "Tổ tiên"
                                    treeDeceased = false
                                },
                                enabled = treeName.isNotBlank(),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (clanPeople.size >= 2) {
                            Row(horizontalArrangement = Arrangement.spacedBy(FamilySpacing.xs)) {
                                SecondaryActionButton(
                                    text = clanPeople.firstOrNull { it.id == linkFromId }?.name ?: "Chọn người A",
                                    onClick = { linkFromId = clanPeople.first().id },
                                    modifier = Modifier.weight(1f)
                                )
                                SecondaryActionButton(
                                    text = clanPeople.firstOrNull { it.id == linkToId }?.name ?: "Chọn người B",
                                    onClick = { linkToId = clanPeople.last().id },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            SecondaryActionButton(
                                text = "Quan hệ: $relationType",
                                onClick = {
                                    relationType = when (relationType) {
                                        "PARENT_CHILD" -> "SPOUSE"
                                        "SPOUSE" -> "SIBLING"
                                        else -> "PARENT_CHILD"
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                            PrimaryActionButton(
                                text = "Nối quan hệ",
                                onClick = {
                                    val a = linkFromId ?: return@PrimaryActionButton
                                    val b = linkToId ?: return@PrimaryActionButton
                                    if (a != b) viewModel.addClanTreeLink(a, b, relationType)
                                },
                                enabled = linkFromId != null && linkToId != null && linkFromId != linkToId,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    if (clanPeople.isEmpty()) {
                        EmptyStateCard("Sơ đồ chưa có dữ liệu", "Trưởng họ thêm người vào cây")
                    } else {
                        clanPeople.forEach {
                            MetaLineCard(
                                it.name,
                                "${it.roleLabel}${if (it.isDeceased) " • đã mất" else ""}"
                            )
                        }
                        if (clanLinks.isNotEmpty()) {
                            Text("Liên kết", style = MaterialTheme.typography.titleMedium)
                            clanLinks.forEach { link ->
                                val from = clanPeople.firstOrNull { it.id == link.fromPersonId }?.name ?: link.fromPersonId.toString()
                                val to = clanPeople.firstOrNull { it.id == link.toPersonId }?.name ?: link.toPersonId.toString()
                                MetaLineCard(from, "${link.relationType} -> $to")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimelineScreen(viewModel: FamilyViewModel) {
    val state by viewModel.state.collectAsState()
    var postText by remember { mutableStateOf("") }

    val me = state.currentUser
    val familyMemberIds = state.families.firstOrNull { it.id == state.activeFamilyId }?.memberIds.orEmpty()
    val clanMemberIds = state.clans.firstOrNull { it.id == state.activeClanId }?.memberIds.orEmpty()
    val visibleIds = (familyMemberIds + clanMemberIds + listOfNotNull(me?.id)).toSet()
    val visiblePosts = state.timeline.filter { it.post.authorId in visibleIds }

    PremiumScreenBackground {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(FamilySpacing.md),
            verticalArrangement = Arrangement.spacedBy(FamilySpacing.md)
        ) {
            item { HeroHeader("Bảng tin", "Chỉ hiển thị người cùng gia đình hoặc dòng họ") }

            item {
                PremiumCard {
                    PremiumInput(postText, { postText = it }, label = "Bạn muốn chia sẻ gì?", minLines = 3)
                    PrimaryActionButton(
                        text = "Đăng bài",
                        onClick = {
                            viewModel.createPost(postText)
                            postText = ""
                        },
                        enabled = postText.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (visiblePosts.isEmpty()) {
                item { EmptyStateCard("Chưa có bài viết", "Tạo bài viết đầu tiên") }
            }

            itemsIndexed(visiblePosts, key = { _, item -> item.post.id }) { _, item ->
                PremiumCard {
                    Text(item.author?.fullName ?: "Không rõ", style = MaterialTheme.typography.titleMedium)
                    Text(item.post.content, style = MaterialTheme.typography.bodyLarge)
                    Text(item.post.createdAt, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(viewModel: FamilyViewModel) {
    val state by viewModel.state.collectAsState()
    val user = state.currentUser

    PremiumScreenBackground {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(FamilySpacing.md),
            verticalArrangement = Arrangement.spacedBy(FamilySpacing.md)
        ) {
            item {
                HeroHeader(
                    title = "Tài khoản",
                    subtitle = "Thông tin cá nhân"
                )
            }

            item {
                PremiumCard {
                    Row(horizontalArrangement = Arrangement.spacedBy(FamilySpacing.xs), verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = RoundedCornerShape(FamilyRadius.md), color = MaterialTheme.colorScheme.surfaceContainer) {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = null,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                        Column {
                            Text(user?.fullName ?: "Không rõ", style = MaterialTheme.typography.titleLarge)
                            Text("@${user?.username ?: "-"}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Text(
                        if ((state.ageYears ?: 16) < 16) "Dưới 16 tuổi: chỉ hiển thị module Gia đình"
                        else "Từ 16 tuổi: mở đầy đủ module Dòng họ",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    SecondaryActionButton(
                        text = "Đăng xuất",
                        onClick = { viewModel.logout() },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun MetaLineCard(title: String, subtitle: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(FamilyRadius.sm),
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Column(modifier = Modifier.padding(FamilySpacing.sm)) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(subtitle, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun MetaBadge(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(FamilyRadius.sm),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(modifier = Modifier.padding(horizontal = FamilySpacing.sm, vertical = FamilySpacing.xs)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.78f))
            Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}

@Composable
private fun FamilyCodeCard(code: String?) {
    val clipboardManager = LocalClipboardManager.current
    val parts = (code ?: "-")
        .split("-")
        .filter { it.isNotBlank() }
        .let {
            when {
                it.size >= 3 -> it.take(3)
                it.isEmpty() -> listOf("---", "---", "---")
                else -> it + List(3 - it.size) { "---" }
            }
        }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(FamilyRadius.sm),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(
            modifier = Modifier.padding(FamilySpacing.sm),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(FamilySpacing.xs), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.AcUnit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text("Mã gia đình", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            parts.forEach { line ->
                Text(
                    text = line,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { clipboardManager.setText(AnnotatedString(line)) }
                )
            }
            SecondaryActionButton(
                text = "Sao chép toàn bộ mã",
                onClick = { clipboardManager.setText(AnnotatedString(parts.joinToString("-"))) },
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Chạm từng dòng để sao chép nhanh",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun DatePickerRow(
    label: String,
    value: String,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val parsedDate = runCatching { LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE) }.getOrNull() ?: LocalDate.now()
    val displayDate = parsedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(FamilySpacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MetaBadge(label, displayDate, Modifier.weight(1f))
        SecondaryActionButton(
            text = "Chọn ngày",
            onClick = {
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        val picked = LocalDate.of(year, month + 1, dayOfMonth)
                        onDateSelected(picked.format(DateTimeFormatter.ISO_LOCAL_DATE))
                    },
                    parsedDate.year,
                    parsedDate.monthValue - 1,
                    parsedDate.dayOfMonth
                ).show()
            }
        )
    }
}

@Composable
private fun DateTimePickerRow(
    label: String,
    value: LocalDateTime,
    onDateTimeSelected: (LocalDateTime) -> Unit
) {
    val context = LocalContext.current
    val display = value.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(FamilySpacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MetaBadge(label, display, Modifier.weight(1f))
        SecondaryActionButton(
            text = "Chọn ngày giờ",
            onClick = {
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        val pickedDate = LocalDate.of(year, month + 1, dayOfMonth)
                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                onDateTimeSelected(LocalDateTime.of(pickedDate, LocalTime.of(hour, minute)))
                            },
                            value.hour,
                            value.minute,
                            true
                        ).show()
                    },
                    value.year,
                    value.monthValue - 1,
                    value.dayOfMonth
                ).show()
            }
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(FamilySpacing.xs), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Filled.Groups, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text(text, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
    }
}

private fun relationTypeLabel(type: String): String = when (type) {
    "PARENT" -> "là cha/mẹ của"
    "CHILD" -> "là con của"
    "SIBLING" -> "là anh/chị/em của"
    "SPOUSE" -> "là vợ/chồng của"
    else -> type
}

private fun memberName(memberId: Long, members: List<FamilyMember>): String {
    return members.firstOrNull { it.id == memberId }?.fullName ?: memberId.toString()
}

private fun relatedMemberLabels(
    memberId: Long,
    tree: Tree?,
    members: List<FamilyMember>
): List<Pair<String, FamilyMember>> {
    if (tree == null) return emptyList()
    val byId = members.associateBy { it.id }
    return tree.relationships.mapNotNull { rel ->
        when {
            rel.fromMemberId == memberId -> {
                val target = byId[rel.toMemberId] ?: return@mapNotNull null
                relationTypeLabel(rel.type) to target
            }
            rel.toMemberId == memberId -> {
                val target = byId[rel.fromMemberId] ?: return@mapNotNull null
                "liên quan tới" to target
            }
            else -> null
        }
    }
}
