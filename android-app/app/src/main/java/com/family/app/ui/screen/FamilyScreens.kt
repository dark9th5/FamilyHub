package com.family.app.ui.screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Cottage
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VpnKey
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.family.app.domain.model.ClanTreeLink
import com.family.app.domain.model.ClanTreePerson
import com.family.app.domain.model.FamilyMember
import com.family.app.domain.model.SocialCommentThread
import com.family.app.domain.model.SocialTargetLike
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
import kotlin.math.max
import kotlin.math.roundToInt
import kotlinx.coroutines.delay

private val VIETNAM_PROVINCES = listOf(
    "An Giang", "Bà Rịa - Vũng Tàu", "Bắc Giang", "Bắc Kạn", "Bạc Liêu", "Bắc Ninh", "Bến Tre", "Bình Định", "Bình Dương", "Bình Phước", "Bình Thuận",
    "Cà Mau", "Cần Thơ", "Cao Bằng", "Đà Nẵng", "Đắk Lắk", "Đắk Nông", "Điện Biên", "Đồng Nai", "Đồng Tháp", "Gia Lai", "Hà Giang", "Hà Nam", "Hà Nội",
    "Hà Tĩnh", "Hải Dương", "Hải Phòng", "Hậu Giang", "Hòa Bình", "Hưng Yên", "Khánh Hòa", "Kiên Giang", "Kon Tum", "Lai Châu", "Lâm Đồng", "Lạng Sơn",
    "Lào Cai", "Long An", "Nam Định", "Nghệ An", "Ninh Bình", "Ninh Thuận", "Phú Thọ", "Phú Yên", "Quảng Bình", "Quảng Nam", "Quảng Ngãi", "Quảng Ninh",
    "Quảng Trị", "Sóc Trăng", "Sơn La", "Tây Ninh", "Thái Bình", "Thái Nguyên", "Thanh Hóa", "Thừa Thiên Huế", "Tiền Giang", "TP Hồ Chí Minh", "Trà Vinh",
    "Tuyên Quang", "Vĩnh Long", "Vĩnh Phúc", "Yên Bái"
)

private enum class FamilyHubTab {
    Overview,
    Members,
    Tasks,
    Events,
    Finance
}

private enum class ClanHubTab {
    Overview,
    Members,
    Events,
    Tree,
    Permissions
}

private enum class ProfileAction {
    EditInfo,
    ChangePassword,
    ChangeEmail
}

private enum class TreeAddDirection {
    Parent,
    Spouse,
    Child
}

@Composable
private fun HorizontalFunctionTabs(
    tabs: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(FamilySpacing.xs)) {
        itemsIndexed(tabs) { index, label ->
            val isSelected = index == selectedIndex
            SecondaryActionButton(
                text = if (isSelected) "• $label" else label,
                onClick = { onSelect(index) }
            )
        }
    }
}

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
                    title = "Đăng Nhập",
                    subtitle = "Chào mừng quay lại FamilyHub"
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
    var cityProvince by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var verifyCode by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf(LocalDate.now().minusYears(18).format(DateTimeFormatter.ISO_LOCAL_DATE)) }
    var justVerified by remember { mutableStateOf(false) }
    var otpCooldownSeconds by remember { mutableStateOf(0) }
    val normalizedEmail = email.trim().lowercase()
    val isGmail = normalizedEmail.endsWith("@gmail.com")
    val provinceSuggestions = remember(cityProvince) {
        if (cityProvince.isBlank()) VIETNAM_PROVINCES.take(8)
        else VIETNAM_PROVINCES.filter { it.contains(cityProvince, ignoreCase = true) }.take(8)
    }

    LaunchedEffect(otpCooldownSeconds) {
        if (otpCooldownSeconds <= 0) return@LaunchedEffect
        delay(1000)
        otpCooldownSeconds -= 1
    }

    LaunchedEffect(username) {
        if (username.isBlank()) return@LaunchedEffect
        delay(250)
        viewModel.checkUsernameAvailability(username)
    }

    PremiumScreenBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(FamilySpacing.md),
            contentAlignment = Alignment.Center
        ) {
            PremiumCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 620.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(FamilySpacing.sm)
                ) {
                    HeroHeader(
                        title = "Tạo tài khoản",
                        subtitle = "Mỗi tài khoản đều là người dùng bình thường"
                    )

                    PremiumInput(fullName, { fullName = it }, label = "Họ và tên")
                    PremiumInput(username, { username = it }, label = "Tên đăng nhập")
                    PremiumInput(cityProvince, { cityProvince = it }, label = "Tỉnh/Thành phố")
                    if (provinceSuggestions.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(FamilyRadius.sm),
                            color = MaterialTheme.colorScheme.surfaceContainerLow
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(FamilySpacing.xs),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                provinceSuggestions.take(3).forEach { p ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { cityProvince = p }
                                            .padding(horizontal = FamilySpacing.xs, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(FamilySpacing.xs)
                                    ) {
                                        Icon(Icons.Filled.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Text(text = p, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }
                    }
                    state.usernameAvailabilityMessage?.let {
                        Text(
                            text = it,
                            color = if (state.usernameAvailability == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    PremiumInput(email, { email = it }, label = "Gmail")
                    if (email.isNotBlank() && !isGmail) {
                        Text(
                            text = "Email phải có đuôi @gmail.com",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    PremiumInput(password, { password = it }, label = "Mật khẩu", isPassword = true)
                    DatePickerRow(
                        label = "Ngày sinh",
                        value = birthDate,
                        onDateSelected = { birthDate = it }
                    )

                    PrimaryActionButton(
                        text = if (state.isLoading) "Đang gửi mã..." else "Đăng ký và gửi mã",
                        onClick = { viewModel.register(fullName, username, cityProvince, email, password, birthDate) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isLoading && fullName.isNotBlank() && username.isNotBlank() && cityProvince.isNotBlank() && email.isNotBlank() && isGmail && password.length >= 6 && birthDate.isNotBlank() && state.usernameAvailability != false
                    )

                    if (state.awaitingEmailVerification) {
                        PremiumInput(
                            value = verifyCode,
                            onValueChange = { verifyCode = it.filter(Char::isDigit).take(6) },
                            label = "Mã xác nhận 6 số"
                        )
                        PrimaryActionButton(
                            text = if (state.isLoading) "Đang xác nhận..." else "Xác nhận mã",
                            onClick = {
                                justVerified = true
                                viewModel.verifyEmail(verifyCode)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !state.isLoading && verifyCode.length == 6
                        )
                        SecondaryActionButton(
                            text = if (otpCooldownSeconds > 0) "Gửi lại mã OTP (${otpCooldownSeconds}s)" else "Gửi lại mã OTP",
                            onClick = {
                                viewModel.resendVerificationCode(
                                    fullName = fullName,
                                    username = username,
                                    cityProvince = cityProvince,
                                    email = email,
                                    password = password
                                )
                                otpCooldownSeconds = 30
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !state.isLoading && otpCooldownSeconds == 0
                        )
                    }

                    TertiaryGhostButton(
                        text = "Đã có tài khoản? Đăng nhập",
                        onClick = onNavigateLogin,
                        modifier = Modifier.fillMaxWidth()
                    )
                    state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            }
        }
    }

    LaunchedEffect(state.awaitingEmailVerification, justVerified) {
        if (justVerified && !state.awaitingEmailVerification && state.error == null) {
            onRegistered()
            justVerified = false
        }
    }
}

@Composable
fun HomeScreen(viewModel: FamilyViewModel) {
    val state by viewModel.state.collectAsState()
    val me = state.currentUser
    val currentUserId = me?.id ?: state.authMemberId
    val activeClan = state.clans.firstOrNull { currentUserId != null && currentUserId in it.memberIds }
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
                    if (activeClan == null) {
                        EmptyStateCard("Bạn chưa gia nhập dòng họ", "Vào tab Dòng họ để tạo hoặc gửi đơn gia nhập")
                    } else {
                        Text(activeClan.name, style = MaterialTheme.typography.titleLarge)
                        Row(horizontalArrangement = Arrangement.spacedBy(FamilySpacing.xs)) {
                            MetaBadge("Mã", activeClan.code, Modifier.weight(1f))
                            MetaBadge("Thành viên", activeClan.memberIds.size.toString(), Modifier.weight(1f))
                        }
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
    val currentUserId = me?.id ?: state.authMemberId
    val joinedFamily = state.families.firstOrNull { currentUserId != null && currentUserId in it.memberIds }
    val activeFamily = joinedFamily ?: state.families.firstOrNull { it.id == state.activeFamilyId }
    val familyMemberIds = activeFamily?.memberIds.orEmpty().toSet()
    val isOwner = activeFamily?.ownerId == currentUserId
    val familyId = activeFamily?.id
    val parentIds = state.familyRoles
        .filter { it.familyId == familyId && (it.role == "PARENT_FATHER" || it.role == "PARENT_MOTHER") }
        .map { it.memberId }
        .toSet()
    val isParent = currentUserId in parentIds
    val pendingFamilyRequests = state.familyJoinRequests.filter { it.familyId == activeFamily?.id && it.status == "PENDING" }
    var selectedTab by remember { mutableStateOf(FamilyHubTab.Overview) }

    var familyName by remember { mutableStateOf("") }
    var ownerRole by remember { mutableStateOf("PARENT_FATHER") }
    var familyCode by remember { mutableStateOf("") }
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
                HorizontalFunctionTabs(
                    tabs = listOf("Giới thiệu", "Thành viên", "Công việc", "Sự kiện", "Chi tiêu"),
                    selectedIndex = selectedTab.ordinal,
                    onSelect = { selectedTab = FamilyHubTab.values()[it] }
                )
            }

            if (selectedTab == FamilyHubTab.Overview) {
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
                        if (joinedFamily == null) {
                            SectionTitle("Khởi tạo/Gia nhập gia đình")
                            PremiumInput(familyName, { familyName = it }, label = "Tên gia đình")
                            SecondaryActionButton(
                                text = if (ownerRole == "PARENT_FATHER") "Gia chủ: Cha" else "Gia chủ: Mẹ",
                                onClick = {
                                    ownerRole = if (ownerRole == "PARENT_FATHER") "PARENT_MOTHER" else "PARENT_FATHER"
                                },
                                leadingIcon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                trailingIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                modifier = Modifier.fillMaxWidth()
                            )
                            PrimaryActionButton(
                                text = "Khởi tạo gia đình",
                                onClick = {
                                    viewModel.createFamily(familyName, ownerRole)
                                    familyName = ""
                                },
                                enabled = familyName.isNotBlank(),
                                modifier = Modifier.fillMaxWidth()
                            )
                            PremiumInput(familyCode, { familyCode = it }, label = "Mã gia đình")
                            SecondaryActionButton(
                                text = "Gửi đơn xin vào gia đình",
                                onClick = {
                                    viewModel.requestJoinFamilyByCode(familyCode)
                                    familyCode = ""
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            SectionTitle("Gia đình hiện tại")
                            MetaLineCard("Đang chọn", joinedFamily.name)
                            MetaLineCard("Số thành viên", joinedFamily.memberIds.size.toString())
                            FamilyCodeCard(joinedFamily.code)
                        }
                    }
                }
            }

            if (selectedTab == FamilyHubTab.Members && joinedFamily != null) {
                item {
                    PremiumCard {
                        SectionTitle("Quản lý thành viên gia đình")
                        activeFamily?.memberIds.orEmpty().forEach { memberId ->
                            val memberName = state.members.firstOrNull { it.id == memberId }?.fullName ?: memberId.toString()
                            val roleRaw = state.familyRoles
                                .lastOrNull { it.familyId == joinedFamily.id && it.memberId == memberId }
                                ?.role
                                ?: if (joinedFamily.ownerId == memberId) joinedFamily.ownerRole else "CHILD"
                            val roleLabel = when (roleRaw) {
                                "PARENT_FATHER" -> "Cha"
                                "PARENT_MOTHER" -> "Mẹ"
                                "CHILD" -> "Con"
                                else -> roleRaw
                            }
                            MetaLineCard(memberName, if (joinedFamily.ownerId == memberId) "$roleLabel • Gia chủ" else roleLabel)
                        }

                        if (isOwner && pendingFamilyRequests.isNotEmpty()) {
                            Text("Đơn xin vào gia đình", style = MaterialTheme.typography.titleMedium)
                            pendingFamilyRequests.forEach { req ->
                                val name = state.members.firstOrNull { it.id == req.memberId }?.fullName ?: req.memberId.toString()
                                Row(horizontalArrangement = Arrangement.spacedBy(FamilySpacing.xs)) {
                                    Text(name, modifier = Modifier.weight(1f))
                                    SecondaryActionButton(text = "Duyệt", onClick = { viewModel.reviewFamilyJoinRequest(req.id, true) })
                                    SecondaryActionButton(text = "Từ chối", onClick = { viewModel.reviewFamilyJoinRequest(req.id, false) })
                                }
                            }
                        }

                        if (isOwner) {
                            Text("Thành viên nhập mã phải chờ gia chủ duyệt.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            FamilyCodeCard(joinedFamily.code)
                        }

                        activeFamily?.let { family ->
                            val transferTargets = family.memberIds.filter { it != family.ownerId }
                            if (transferTargets.isNotEmpty()) {
                                SecondaryActionButton(
                                    text = "Nhường vị trí gia chủ cho ${state.members.firstOrNull { it.id == transferTargets.first() }?.fullName ?: transferTargets.first()}",
                                    onClick = { viewModel.transferFamilyOwnership(transferTargets.first()) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        SecondaryActionButton(
                            text = "Rời khỏi gia đình",
                            onClick = { viewModel.leaveCurrentFamily() },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            if (selectedTab == FamilyHubTab.Members && joinedFamily == null) {
                item { EmptyStateCard("Bạn chưa thuộc gia đình nào", "Vào tab Giới thiệu để khởi tạo hoặc gửi đơn gia nhập") }
            }

            if (selectedTab == FamilyHubTab.Tasks && joinedFamily != null) item {
                PremiumCard {
                    SectionTitle("Công việc gia đình")
                    Text("Mỗi sáng nhiệm vụ sẽ tự reset về trạng thái mới.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    PremiumInput(taskTitle, { taskTitle = it }, label = "Tên công việc")
                    PremiumInput(taskNote, { taskNote = it }, label = "Ghi chú", minLines = 2)
                    PremiumInput(taskPoints, { taskPoints = it.filter(Char::isDigit) }, label = "Điểm")
                    DatePickerRow(
                        label = "Hạn hoàn thành",
                        value = taskDueDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                        onDateSelected = { taskDueDate = parseDateTimeOrNow(it).toLocalDate() }
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

            if (selectedTab == FamilyHubTab.Tasks && joinedFamily == null) {
                item { EmptyStateCard("Bạn chưa thuộc gia đình nào", "Vào tab Giới thiệu để tham gia gia đình trước") }
            }

            if (selectedTab == FamilyHubTab.Finance && joinedFamily != null) item {
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

            if (selectedTab == FamilyHubTab.Finance && joinedFamily == null) {
                item { EmptyStateCard("Bạn chưa thuộc gia đình nào", "Vào tab Giới thiệu để tham gia gia đình trước") }
            }

            if (selectedTab == FamilyHubTab.Events && joinedFamily != null) item {
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

            if (selectedTab == FamilyHubTab.Events && joinedFamily == null) {
                item { EmptyStateCard("Bạn chưa thuộc gia đình nào", "Vào tab Giới thiệu để tham gia gia đình trước") }
            }
        }
    }
}

@Composable
fun ClanHubScreen(viewModel: FamilyViewModel) {
    val state by viewModel.state.collectAsState()
    val me = state.currentUser
    val currentUserId = me?.id ?: state.authMemberId
    val joinedClan = state.clans.firstOrNull { currentUserId != null && currentUserId in it.memberIds }
    val activeClan = joinedClan ?: state.clans.firstOrNull { it.id == state.activeClanId }
    val clanMemberIds = activeClan?.memberIds.orEmpty().toSet()
    val isHead = activeClan?.ownerId == currentUserId
    val pendingRequests = state.clanJoinRequests.filter { it.clanId == activeClan?.id && it.status == "PENDING" }
    val clanPeople = state.clanTreePeople.filter { it.clanId == activeClan?.id }
    val clanLinks = state.clanTreeLinks.filter { it.clanId == activeClan?.id }
    var selectedTab by remember { mutableStateOf(ClanHubTab.Overview) }

    var clanName by remember { mutableStateOf("") }
    var ancestralAddress by remember { mutableStateOf("") }
    var clanCode by remember { mutableStateOf("") }
    var selectedMemberId by remember { mutableStateOf<Long?>(null) }
    var expandedMember by remember { mutableStateOf(false) }
    var selectedTreeAnchorId by remember { mutableStateOf<Long?>(null) }
    var treeFullScreen by remember { mutableStateOf(false) }
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
                HorizontalFunctionTabs(
                    tabs = listOf("Giới thiệu", "Thành viên", "Sự kiện", "Sơ đồ", "Phân quyền"),
                    selectedIndex = selectedTab.ordinal,
                    onSelect = { selectedTab = ClanHubTab.values()[it] }
                )
            }

            if (selectedTab == ClanHubTab.Overview) {
                item {
                    PremiumCard {
                        if (joinedClan == null) {
                            SectionTitle("Khởi tạo/Gia nhập dòng họ")
                            PremiumInput(clanName, { clanName = it }, label = "Tên dòng họ")
                            PremiumInput(ancestralAddress, { ancestralAddress = it }, label = "Địa chỉ nhà thờ tổ (có thể bỏ qua)")
                            PrimaryActionButton(
                                text = "Khởi tạo dòng họ",
                                onClick = {
                                    viewModel.createClan(clanName, ancestralAddress)
                                    clanName = ""
                                    ancestralAddress = ""
                                },
                                enabled = clanName.isNotBlank(),
                                modifier = Modifier.fillMaxWidth()
                            )
                            PremiumInput(clanCode, { clanCode = it }, label = "Mã dòng họ")
                            SecondaryActionButton(
                                text = "Gửi đơn xin gia nhập",
                                onClick = {
                                    viewModel.requestJoinClanByCode(clanCode)
                                    clanCode = ""
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            SectionTitle("Dòng họ hiện tại")
                            MetaLineCard("Đang chọn", joinedClan.name)
                            MetaLineCard("Mã", joinedClan.code)
                            MetaLineCard("Nhà thờ tổ", joinedClan.ancestralAddress ?: "Chưa cập nhật")
                            MetaLineCard("Số thành viên", joinedClan.memberIds.size.toString())
                            SecondaryActionButton(
                                text = "Rời khỏi dòng họ",
                                onClick = { viewModel.leaveCurrentClan() },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            if (selectedTab == ClanHubTab.Members && joinedClan != null) {
                item {
                    PremiumCard {
                        SectionTitle("Quản lý thành viên dòng họ")
                        joinedClan.memberIds.forEach { memberId ->
                            val name = state.members.firstOrNull { it.id == memberId }?.fullName ?: memberId.toString()
                            val roleLabel = when {
                                memberId == joinedClan.ownerId -> "Trưởng họ"
                                memberId in joinedClan.delegateIds -> "Đại diện"
                                else -> "Thành viên"
                            }
                            MetaLineCard(name, roleLabel)
                        }

                        if (pendingRequests.isNotEmpty() && (isHead || currentUserId in joinedClan.delegateIds)) {
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

                        if (isHead) {
                            Text("Thành viên nhập mã sẽ chờ người có quyền duyệt.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            MetaLineCard("Mã dòng họ", joinedClan.code)
                        }

                        activeClan?.let { clan ->
                            val transferTargets = clan.memberIds.filter { it != clan.ownerId }
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
            }

            if (selectedTab == ClanHubTab.Members && joinedClan == null) {
                item { EmptyStateCard("Bạn chưa thuộc dòng họ nào", "Vào tab Giới thiệu để khởi tạo hoặc xin gia nhập") }
            }

            if (selectedTab == ClanHubTab.Permissions && joinedClan != null && isHead) {
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

            if (selectedTab == ClanHubTab.Permissions && joinedClan != null && !isHead) {
                item { EmptyStateCard("Chỉ trưởng họ được phân quyền", "Bạn có thể xem ở tab Thành viên và Sự kiện") }
            }

            if (selectedTab == ClanHubTab.Permissions && joinedClan == null) {
                item { EmptyStateCard("Bạn chưa thuộc dòng họ nào", "Vào tab Giới thiệu để tham gia dòng họ trước") }
            }

            if (selectedTab == ClanHubTab.Events && joinedClan != null) item {
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
                            val agree = it.rsvps.filter { r -> r.rsvp.status == "GOING" }
                            val decline = it.rsvps.filter { r -> r.rsvp.status == "DECLINED" }
                            MetaLineCard(it.event.title, "${it.event.location} • ${it.event.eventTime}")
                            Row(horizontalArrangement = Arrangement.spacedBy(FamilySpacing.xs)) {
                                SecondaryActionButton(
                                    text = "Đồng ý (${agree.size})",
                                    onClick = { viewModel.rsvp(it.event.id, "GOING") },
                                    modifier = Modifier.weight(1f)
                                )
                                SecondaryActionButton(
                                    text = "Từ chối (${decline.size})",
                                    onClick = { viewModel.rsvp(it.event.id, "DECLINED") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (agree.isNotEmpty()) {
                                Text("Đồng ý: ${agree.joinToString { a -> a.member?.fullName ?: a.rsvp.memberId.toString() }}", style = MaterialTheme.typography.bodySmall)
                            }
                            if (decline.isNotEmpty()) {
                                Text("Từ chối: ${decline.joinToString { a -> a.member?.fullName ?: a.rsvp.memberId.toString() }}", style = MaterialTheme.typography.bodySmall)
                            }
                            SocialInteractionPanel(
                                targetType = "EVENT",
                                targetId = it.event.id,
                                members = state.members,
                                likes = state.socialLikes,
                                comments = state.socialComments,
                                currentUserId = currentUserId,
                                onToggleTargetLike = viewModel::toggleTargetLike,
                                onAddComment = viewModel::addSocialComment,
                                onToggleCommentLike = viewModel::toggleSocialCommentLike
                            )
                        }
                    }
                }
            }

            if (selectedTab == ClanHubTab.Events && joinedClan == null) {
                item { EmptyStateCard("Bạn chưa thuộc dòng họ nào", "Vào tab Giới thiệu để tham gia dòng họ trước") }
            }

            if (selectedTab == ClanHubTab.Tree && joinedClan != null) item {
                PremiumCard {
                    val canManageTree = isHead || state.clanDelegations.any {
                        it.clanId == activeClan?.id && it.memberId == currentUserId && it.permissions.contains("MANAGE_TREE")
                    }

                    LaunchedEffect(clanPeople) {
                        if (selectedTreeAnchorId == null && clanPeople.isNotEmpty()) {
                            selectedTreeAnchorId = clanPeople.first().id
                        }
                        if (selectedTreeAnchorId != null && clanPeople.none { it.id == selectedTreeAnchorId }) {
                            selectedTreeAnchorId = clanPeople.firstOrNull()?.id
                        }
                    }

                    SectionTitle("Gia phả dòng họ")
                    Text(
                        text = "Bấm vào một người để chọn. Dùng điểm phía trên để thêm Cha/Mẹ, bên phải để thêm Vợ/Chồng, phía dưới để thêm Con.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    ClanTreeExplorer(
                        clanId = activeClan?.id,
                        people = clanPeople,
                        links = clanLinks,
                        selectedPersonId = selectedTreeAnchorId,
                        canManageTree = canManageTree,
                        isFullScreen = treeFullScreen,
                        onToggleFullScreen = { treeFullScreen = !treeFullScreen },
                        onSelectPerson = { selectedTreeAnchorId = it },
                        onCreateRoot = { name, roleLabel, isDeceased ->
                            viewModel.addClanTreePerson(name, roleLabel, isDeceased)
                        },
                        onAddRelative = { anchorId, direction, name, roleLabel, isDeceased ->
                            val relation = when (direction) {
                                TreeAddDirection.Parent -> "PARENT"
                                TreeAddDirection.Spouse -> "SPOUSE"
                                TreeAddDirection.Child -> "CHILD"
                            }
                            viewModel.addClanRelative(
                                anchorPersonId = anchorId,
                                direction = relation,
                                name = name,
                                roleLabel = roleLabel,
                                isDeceased = isDeceased
                            )
                        }
                    )

                    if (!canManageTree) {
                        Text(
                            text = "Bạn đang xem cây gia phả. Chỉ trưởng họ hoặc người có quyền MANAGE_TREE mới chỉnh sửa.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (selectedTab == ClanHubTab.Tree && joinedClan == null) {
                item { EmptyStateCard("Bạn chưa thuộc dòng họ nào", "Vào tab Giới thiệu để tham gia dòng họ trước") }
            }
        }
    }
}

@Composable
fun TimelineScreen(viewModel: FamilyViewModel) {
    val state by viewModel.state.collectAsState()
    var postText by remember { mutableStateOf("") }
    var openCommentsForPostId by remember { mutableStateOf<Long?>(null) }

    val me = state.currentUser
    val currentUserId = me?.id ?: state.authMemberId
    val familyMemberIds = state.families.firstOrNull { it.id == state.activeFamilyId }?.memberIds.orEmpty()
    val clanMemberIds = state.clans.firstOrNull { it.id == state.activeClanId }?.memberIds.orEmpty()
    val visibleIds = (familyMemberIds + clanMemberIds + listOfNotNull(currentUserId)).toSet()
    val visiblePosts = state.timeline.filter { it.post.authorId in visibleIds }
    val focusedPost = openCommentsForPostId?.let { id -> visiblePosts.firstOrNull { it.post.id == id } }

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
                    SocialInteractionPanel(
                        targetType = "POST",
                        targetId = item.post.id,
                        members = state.members,
                        likes = state.socialLikes,
                        comments = state.socialComments,
                        currentUserId = currentUserId,
                        onToggleTargetLike = viewModel::toggleTargetLike,
                        onAddComment = viewModel::addSocialComment,
                        onToggleCommentLike = viewModel::toggleSocialCommentLike,
                        showCommentsInline = false,
                        onOpenComments = { openCommentsForPostId = item.post.id }
                    )
                }
            }
        }

        if (focusedPost != null) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(FamilySpacing.md),
                    verticalArrangement = Arrangement.spacedBy(FamilySpacing.sm)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(FamilySpacing.xs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SecondaryActionButton(
                            text = "Quay lại",
                            onClick = { openCommentsForPostId = null },
                            leadingIcon = Icons.AutoMirrored.Filled.KeyboardArrowLeft
                        )
                        Text("Bình luận", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    }

                    PremiumCard(modifier = Modifier.fillMaxWidth()) {
                        Text(focusedPost.author?.fullName ?: "Không rõ", style = MaterialTheme.typography.titleMedium)
                        Text(focusedPost.post.content, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            focusedPost.post.createdAt,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    SocialInteractionPanel(
                        targetType = "POST",
                        targetId = focusedPost.post.id,
                        members = state.members,
                        likes = state.socialLikes,
                        comments = state.socialComments,
                        currentUserId = currentUserId,
                        onToggleTargetLike = viewModel::toggleTargetLike,
                        onAddComment = viewModel::addSocialComment,
                        onToggleCommentLike = viewModel::toggleSocialCommentLike,
                        showCommentsInline = true,
                        onOpenComments = null
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(viewModel: FamilyViewModel) {
    val state by viewModel.state.collectAsState()
    val user = state.currentUser
    var fullName by remember(user?.fullName) { mutableStateOf(user?.fullName.orEmpty()) }
    var cityProvince by remember(user?.cityProvince) { mutableStateOf(user?.cityProvince.orEmpty()) }
    var birthDate by remember(user?.birthDate) { mutableStateOf(user?.birthDate.orEmpty()) }
    var bio by remember(user?.bio) { mutableStateOf(user?.bio.orEmpty()) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var oldEmailCode by remember { mutableStateOf("") }
    var newEmail by remember { mutableStateOf("") }
    var newEmailCode by remember { mutableStateOf("") }
    var activeAction by remember { mutableStateOf<ProfileAction?>(null) }
    val ageLabel = user?.birthDate?.let {
        runCatching {
            val normalized = it.substringBefore("T").substringBefore(" ")
            val years = java.time.Period.between(LocalDate.parse(normalized), LocalDate.now()).years.coerceAtLeast(0)
            "$years tuổi • $it"
        }.getOrNull()
    } ?: "Chưa cập nhật ngày sinh"

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
                    Spacer(modifier = Modifier.height(FamilySpacing.sm))
                    MetaLineCard("Tuổi - Ngày sinh", ageLabel)
                    MetaLineCard("Quê quán", user?.cityProvince?.ifBlank { "Chưa cập nhật" } ?: "Chưa cập nhật")
                    MetaLineCard("Email liên kết", user?.email ?: "Chưa có")
                }
            }

            item {
                PremiumCard {
                    SectionTitle("Chức năng tài khoản")
                    SecondaryActionButton(
                        text = "Đổi thông tin cá nhân",
                        onClick = { activeAction = ProfileAction.EditInfo },
                        modifier = Modifier.fillMaxWidth()
                    )
                    SecondaryActionButton(
                        text = "Đổi mật khẩu",
                        onClick = { activeAction = ProfileAction.ChangePassword },
                        modifier = Modifier.fillMaxWidth()
                    )
                    SecondaryActionButton(
                        text = "Đổi Gmail liên kết",
                        onClick = { activeAction = ProfileAction.ChangeEmail },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        if (activeAction != null) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(FamilySpacing.md),
                    verticalArrangement = Arrangement.spacedBy(FamilySpacing.sm)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(FamilySpacing.xs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SecondaryActionButton(
                            text = "Quay lại",
                            onClick = { activeAction = null },
                            leadingIcon = Icons.AutoMirrored.Filled.KeyboardArrowLeft
                        )
                        Text(
                            text = when (activeAction) {
                                ProfileAction.EditInfo -> "Đổi thông tin cá nhân"
                                ProfileAction.ChangePassword -> "Đổi mật khẩu"
                                ProfileAction.ChangeEmail -> "Đổi Gmail liên kết"
                                null -> ""
                            },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    PremiumCard(modifier = Modifier.fillMaxWidth()) {
                        when (activeAction) {
                            ProfileAction.EditInfo -> {
                                PremiumInput(fullName, { fullName = it }, label = "Họ và tên")
                                PremiumInput(cityProvince, { cityProvince = it }, label = "Quê quán")
                                DatePickerRow(
                                    label = "Ngày sinh",
                                    value = birthDate.ifBlank { LocalDate.now().minusYears(18).format(DateTimeFormatter.ISO_LOCAL_DATE) },
                                    onDateSelected = { birthDate = it }
                                )
                                PremiumInput(bio, { bio = it }, label = "Giới thiệu", minLines = 2)
                                PrimaryActionButton(
                                    text = "Lưu thông tin",
                                    onClick = { viewModel.updateProfile(fullName, cityProvince, birthDate, bio) },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = fullName.isNotBlank() && cityProvince.isNotBlank() && !state.isLoading
                                )
                            }

                            ProfileAction.ChangePassword -> {
                                PremiumInput(currentPassword, { currentPassword = it }, label = "Mật khẩu hiện tại", isPassword = true)
                                PremiumInput(newPassword, { newPassword = it }, label = "Mật khẩu mới", isPassword = true)
                                PrimaryActionButton(
                                    text = "Đổi mật khẩu",
                                    onClick = {
                                        viewModel.changePassword(currentPassword, newPassword)
                                        currentPassword = ""
                                        newPassword = ""
                                    },
                                    enabled = currentPassword.isNotBlank() && newPassword.length >= 6,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            ProfileAction.ChangeEmail -> {
                                SecondaryActionButton(
                                    text = "B1: Gửi mã xác nhận email cũ",
                                    onClick = { viewModel.requestOldEmailChange() },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                PremiumInput(oldEmailCode, { oldEmailCode = it.filter(Char::isDigit).take(6) }, label = "Mã email cũ")
                                SecondaryActionButton(
                                    text = "B1: Xác nhận email cũ",
                                    onClick = {
                                        viewModel.confirmOldEmailChange(oldEmailCode)
                                        oldEmailCode = ""
                                    },
                                    enabled = oldEmailCode.length == 6,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                PremiumInput(newEmail, { newEmail = it }, label = "Gmail mới")
                                SecondaryActionButton(
                                    text = "B2: Gửi mã xác nhận email mới",
                                    onClick = { viewModel.requestNewEmailChange(newEmail) },
                                    enabled = newEmail.trim().endsWith("@gmail.com", ignoreCase = true),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                PremiumInput(newEmailCode, { newEmailCode = it.filter(Char::isDigit).take(6) }, label = "Mã email mới")
                                PrimaryActionButton(
                                    text = "B2: Xác nhận email mới",
                                    onClick = {
                                        viewModel.confirmNewEmailChange(newEmail, newEmailCode)
                                        newEmailCode = ""
                                    },
                                    enabled = newEmailCode.length == 6,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            null -> Unit
                        }
                    }
                }
            }
        }
    }
}

private data class ClanTreeNodeVisual(
    val id: Long,
    val name: String,
    val role: String,
    val isDeceased: Boolean,
    val generation: Int,
    val x: Float,
    val y: Float,
    val isGhost: Boolean = false
)

private data class ClanTreeEdgeVisual(
    val fromId: Long,
    val toId: Long,
    val relationType: String
)

private data class ClanTreeVisualLayout(
    val nodes: List<ClanTreeNodeVisual>,
    val edges: List<ClanTreeEdgeVisual>,
    val worldWidth: Float,
    val worldHeight: Float
)

@Composable
private fun ClanTreeExplorer(
    clanId: Long?,
    people: List<ClanTreePerson>,
    links: List<ClanTreeLink>,
    selectedPersonId: Long?,
    canManageTree: Boolean,
    isFullScreen: Boolean,
    onToggleFullScreen: () -> Unit,
    onSelectPerson: (Long) -> Unit,
    onCreateRoot: (name: String, roleLabel: String, isDeceased: Boolean) -> Unit,
    onAddRelative: (anchorId: Long, direction: TreeAddDirection, name: String, roleLabel: String, isDeceased: Boolean) -> Unit
) {
    val clanStateKey = "clan-tree-${clanId ?: 0L}"
    val layout = remember(people, links) { buildClanTreeVisualLayout(people, links) }
    var scale by rememberSaveable(clanStateKey) { mutableStateOf(1f) }
    var translationX by rememberSaveable(clanStateKey) { mutableStateOf(0f) }
    var translationY by rememberSaveable(clanStateKey) { mutableStateOf(0f) }
    var viewportSize by remember { mutableStateOf(IntSize.Zero) }
    var generationFilter by rememberSaveable(clanStateKey) { mutableStateOf<Int?>(null) }
    var centerRequestTick by remember { mutableStateOf(0) }
    var editorVisible by remember { mutableStateOf(false) }
    var editorAnchorId by remember { mutableStateOf<Long?>(null) }
    var editorDirection by remember { mutableStateOf<TreeAddDirection?>(null) }
    var editorName by remember { mutableStateOf("") }
    var editorRole by remember { mutableStateOf("") }
    var editorDeceased by remember { mutableStateOf(false) }
    val pulse by rememberInfiniteTransition(label = "selected_pulse").animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )
    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(0.55f, 2.8f)
        translationX += panChange.x
        translationY += panChange.y
    }
    val density = LocalDensity.current
    val worldWidthDp = with(density) { layout.worldWidth.toDp() }
    val worldHeightDp = with(density) { layout.worldHeight.toDp() }
    val allGenerations = remember(layout.nodes) {
        layout.nodes.filterNot { it.isGhost }.map { it.generation }.distinct().sorted()
    }
    val visibleNodes = remember(layout.nodes, generationFilter) {
        if (generationFilter == null) layout.nodes
        else layout.nodes.filter { it.isGhost || it.generation == generationFilter }
    }
    val visibleNodeIds = remember(visibleNodes) { visibleNodes.map { it.id }.toSet() }
    val visibleEdges = remember(layout.edges, visibleNodeIds) {
        layout.edges.filter { it.fromId in visibleNodeIds && it.toId in visibleNodeIds }
    }

    fun openEditor(anchorId: Long?, direction: TreeAddDirection?) {
        editorAnchorId = anchorId
        editorDirection = direction
        editorName = ""
        editorRole = if (direction == null) "Tổ tiên" else "Thành viên"
        editorDeceased = false
        editorVisible = true
    }

    LaunchedEffect(selectedPersonId, viewportSize, layout.nodes, generationFilter, centerRequestTick) {
        if (selectedPersonId == null || viewportSize.width == 0 || viewportSize.height == 0) return@LaunchedEffect
        val selected = layout.nodes.firstOrNull { it.id == selectedPersonId } ?: return@LaunchedEffect
        val targetX = viewportSize.width / 2f - (selected.x * scale)
        val targetY = viewportSize.height / 2f - (selected.y * scale)
        val startX = translationX
        val startY = translationY
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(durationMillis = 360)
        ) { progress, _ ->
            translationX = startX + (targetX - startX) * progress
            translationY = startY + (targetY - startY) * progress
        }
    }

    val content: @Composable (Modifier) -> Unit = { baseModifier ->
        Box(
            modifier = baseModifier
                .fillMaxSize()
                .onSizeChanged { viewportSize = it }
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF18233C),
                            Color(0xFF101B31),
                            Color(0xFF0A1020)
                        )
                    )
                )
                .transformable(state = transformState)
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SecondaryActionButton(
                    text = if (generationFilter == null) "• Tất cả" else "Tất cả",
                    onClick = { generationFilter = null }
                )
                allGenerations.forEach { generation ->
                    SecondaryActionButton(
                        text = if (generationFilter == generation) "• Đời $generation" else "Đời $generation",
                        onClick = { generationFilter = generation }
                    )
                }
                SecondaryActionButton(
                    text = "Reset",
                    leadingIcon = Icons.Filled.Refresh,
                    onClick = {
                        scale = 1f
                        translationX = 0f
                        translationY = 0f
                        generationFilter = null
                    }
                )
                SecondaryActionButton(
                    text = "Căn giữa",
                    leadingIcon = Icons.Filled.MyLocation,
                    onClick = { centerRequestTick++ },
                    enabled = selectedPersonId != null
                )
                SecondaryActionButton(
                    text = "Toàn màn hình",
                    onClick = onToggleFullScreen
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SecondaryActionButton(
                    text = "+",
                    onClick = { scale = (scale * 1.15f).coerceIn(0.55f, 2.8f) }
                )
                SecondaryActionButton(
                    text = "-",
                    onClick = { scale = (scale / 1.15f).coerceIn(0.55f, 2.8f) }
                )
                if (isFullScreen) {
                    SecondaryActionButton(text = "X", onClick = onToggleFullScreen)
                }
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                val stars = listOf(
                    Offset(size.width * 0.1f, size.height * 0.14f),
                    Offset(size.width * 0.28f, size.height * 0.2f),
                    Offset(size.width * 0.46f, size.height * 0.1f),
                    Offset(size.width * 0.62f, size.height * 0.24f),
                    Offset(size.width * 0.78f, size.height * 0.16f),
                    Offset(size.width * 0.9f, size.height * 0.28f),
                    Offset(size.width * 0.18f, size.height * 0.48f),
                    Offset(size.width * 0.38f, size.height * 0.58f),
                    Offset(size.width * 0.56f, size.height * 0.46f),
                    Offset(size.width * 0.72f, size.height * 0.62f),
                    Offset(size.width * 0.86f, size.height * 0.52f),
                    Offset(size.width * 0.2f, size.height * 0.78f),
                    Offset(size.width * 0.42f, size.height * 0.84f),
                    Offset(size.width * 0.64f, size.height * 0.8f),
                    Offset(size.width * 0.84f, size.height * 0.88f)
                )
                stars.forEachIndexed { idx, pos ->
                    drawCircle(
                        color = if (idx % 3 == 0) Color(0x80FFFFFF) else Color(0x66BFE0FF),
                        radius = if (idx % 4 == 0) 2.6f else 1.6f,
                        center = pos
                    )
                }
            }

            Box(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.translationX = translationX
                        this.translationY = translationY
                    }
                    .size(worldWidthDp, worldHeightDp)
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val posById = visibleNodes.associateBy { it.id }
                    visibleEdges.forEach { edge ->
                        val from = posById[edge.fromId] ?: return@forEach
                        val to = posById[edge.toId] ?: return@forEach
                        val isSpouse = edge.relationType == "SPOUSE"
                        val start = Offset(from.x, from.y)
                        val end = Offset(to.x, to.y)
                        val control = if (isSpouse) {
                            Offset((start.x + end.x) / 2f, minOf(start.y, end.y) - 26f)
                        } else {
                            Offset((start.x + end.x) / 2f, (start.y + end.y) / 2f + 42f)
                        }
                        val path = Path().apply {
                            moveTo(start.x, start.y)
                            quadraticTo(control.x, control.y, end.x, end.y)
                        }
                        val glowColor = if (isSpouse) Color(0x667EE0FF) else Color(0x555DA7FF)
                        val coreColor = if (isSpouse) Color(0xFF9CDFFF) else Color(0xFF78B6FF)
                        drawPath(path = path, color = glowColor, style = Stroke(width = if (isSpouse) 10f else 8f))
                        drawPath(path = path, color = coreColor, style = Stroke(width = if (isSpouse) 4f else 3.2f))
                    }
                }

                visibleNodes.forEach { node ->
                    val selected = selectedPersonId == node.id && !node.isGhost
                    Surface(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    (node.x - 84f).roundToInt(),
                                    (node.y - 42f).roundToInt()
                                )
                            }
                            .clickable(enabled = !node.isGhost || (canManageTree && node.id == -1L)) {
                                if (node.isGhost && node.id == -1L && canManageTree) {
                                    openEditor(anchorId = null, direction = null)
                                } else if (!node.isGhost) {
                                    onSelectPerson(node.id)
                                }
                            },
                        shape = RoundedCornerShape(12.dp),
                        color = when {
                            node.isGhost -> Color(0x334F7CB3)
                            selected -> Color(0xFF2C5C99)
                            else -> Color(0xCC1F355C)
                        },
                        border = BorderStroke(
                            width = if (selected) 2.dp else 1.dp,
                            color = if (selected) Color(0xFF85C7FF).copy(alpha = pulse) else Color(0x665A9BD8)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .widthIn(min = 164.dp, max = 190.dp)
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = node.name,
                                style = MaterialTheme.typography.titleSmall,
                                color = Color(0xFFF4FAFF)
                            )
                            Text(
                                text = if (node.isGhost) "Vị trí gợi ý" else node.role + if (node.isDeceased) " • đã mất" else "",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFC8DCF7)
                            )
                        }
                    }

                    if (canManageTree && !node.isGhost && selectedPersonId == node.id) {
                        TreeAddHandle(
                            x = node.x,
                            y = node.y - 88f,
                            text = "+",
                            hint = "Cha/Mẹ",
                            onClick = { openEditor(anchorId = node.id, direction = TreeAddDirection.Parent) }
                        )
                        TreeAddHandle(
                            x = node.x + 116f,
                            y = node.y,
                            text = "+",
                            hint = "Vợ/Chồng",
                            onClick = { openEditor(anchorId = node.id, direction = TreeAddDirection.Spouse) }
                        )
                        TreeAddHandle(
                            x = node.x,
                            y = node.y + 88f,
                            text = "+",
                            hint = "Con",
                            onClick = { openEditor(anchorId = node.id, direction = TreeAddDirection.Child) }
                        )
                    }
                }
            }
        }
    }

    if (isFullScreen) {
        Dialog(
            onDismissRequest = onToggleFullScreen,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF060B16)) {
                content(Modifier.fillMaxSize())
            }
        }
    } else {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(460.dp),
            shape = RoundedCornerShape(FamilyRadius.md),
            color = Color.Transparent
        ) {
            content(Modifier.fillMaxSize())
        }
    }

    if (editorVisible) {
        TreePersonEditorDialog(
            title = if (editorDirection == null) "Tạo người gốc" else "Thêm ${
                when (editorDirection) {
                    TreeAddDirection.Parent -> "Cha/Mẹ"
                    TreeAddDirection.Spouse -> "Vợ/Chồng"
                    TreeAddDirection.Child -> "Con"
                    null -> ""
                }
            }",
            name = editorName,
            onNameChange = { editorName = it },
            role = editorRole,
            onRoleChange = { editorRole = it },
            isDeceased = editorDeceased,
            onToggleDeceased = { editorDeceased = !editorDeceased },
            onDismiss = { editorVisible = false },
            onSubmit = {
                if (editorName.isBlank()) return@TreePersonEditorDialog
                if (editorDirection == null) {
                    onCreateRoot(editorName, editorRole, editorDeceased)
                } else {
                    val anchor = editorAnchorId ?: return@TreePersonEditorDialog
                    val direction = editorDirection ?: return@TreePersonEditorDialog
                    onAddRelative(anchor, direction, editorName, editorRole, editorDeceased)
                }
                editorVisible = false
            }
        )
    }
}

@Composable
private fun TreeAddHandle(
    x: Float,
    y: Float,
    text: String,
    hint: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.offset {
            IntOffset(
                (x - 22f).roundToInt(),
                (y - 28f).roundToInt()
            )
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.clickable(onClick = onClick),
            shape = RoundedCornerShape(999.dp),
            color = Color(0xFF1F66D7),
            border = BorderStroke(1.dp, Color(0xFF9DD4FF))
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = hint,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFFC9E3FF)
        )
    }
}

@Composable
private fun TreePersonEditorDialog(
    title: String,
    name: String,
    onNameChange: (String) -> Unit,
    role: String,
    onRoleChange: (String) -> Unit,
    isDeceased: Boolean,
    onToggleDeceased: () -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(FamilyRadius.md),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(FamilySpacing.md),
                verticalArrangement = Arrangement.spacedBy(FamilySpacing.xs)
            ) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                PremiumInput(value = name, onValueChange = onNameChange, label = "Họ tên")
                PremiumInput(value = role, onValueChange = onRoleChange, label = "Vai trò/ghi chú")
                SecondaryActionButton(
                    text = if (isDeceased) "Đã mất" else "Còn sống",
                    onClick = onToggleDeceased,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(FamilySpacing.xs)) {
                    SecondaryActionButton(
                        text = "Hủy",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    )
                    PrimaryActionButton(
                        text = "Lưu",
                        onClick = onSubmit,
                        enabled = name.isNotBlank(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

private fun buildClanTreeVisualLayout(
    people: List<ClanTreePerson>,
    links: List<ClanTreeLink>
): ClanTreeVisualLayout {
    if (people.isEmpty()) {
        val nodes = listOf(
            ClanTreeNodeVisual(id = -1L, name = "Thủy tổ", role = "Điểm gốc", isDeceased = false, generation = 0, x = 560f, y = 240f, isGhost = true),
            ClanTreeNodeVisual(id = -2L, name = "Vợ/Chồng", role = "Mở rộng ngang", isDeceased = false, generation = 0, x = 820f, y = 240f, isGhost = true),
            ClanTreeNodeVisual(id = -3L, name = "Con cái", role = "Mở rộng xuống", isDeceased = false, generation = 1, x = 560f, y = 470f, isGhost = true),
            ClanTreeNodeVisual(id = -4L, name = "Cha/Mẹ", role = "Mở rộng lên", isDeceased = false, generation = -1, x = 560f, y = 80f, isGhost = true)
        )
        val edges = listOf(
            ClanTreeEdgeVisual(fromId = -1L, toId = -2L, relationType = "SPOUSE"),
            ClanTreeEdgeVisual(fromId = -1L, toId = -3L, relationType = "PARENT_CHILD"),
            ClanTreeEdgeVisual(fromId = -4L, toId = -1L, relationType = "PARENT_CHILD")
        )
        return ClanTreeVisualLayout(nodes = nodes, edges = edges, worldWidth = 1200f, worldHeight = 780f)
    }

    val peopleById = people.associateBy { it.id }
    val parentChildLinks = links.filter { it.relationType == "PARENT_CHILD" }
    val childIds = parentChildLinks.map { it.toPersonId }.toSet()
    val childrenByParent = parentChildLinks.groupBy({ it.fromPersonId }, { it.toPersonId })

    val roots = people.map { it.id }.filter { it !in childIds }.ifEmpty { listOf(people.first().id) }
    val levels = mutableMapOf<Long, Int>()
    val queue = ArrayDeque<Pair<Long, Int>>()
    roots.forEach { queue.addLast(it to 0) }
    while (queue.isNotEmpty()) {
        val (id, level) = queue.removeFirst()
        val oldLevel = levels[id]
        if (oldLevel != null && oldLevel <= level) continue
        levels[id] = level
        childrenByParent[id].orEmpty().forEach { child ->
            queue.addLast(child to (level + 1))
        }
    }

    people.map { it.id }
        .filterNot { levels.containsKey(it) }
        .forEach { levels[it] = 0 }

    val grouped = people.groupBy { levels[it.id] ?: 0 }.toSortedMap()
    val maxCountInGeneration = grouped.values.maxOfOrNull { it.size } ?: 1
    val worldWidthCandidate = max(1200f, (maxCountInGeneration * 245f) + 320f)
    val xSpacing = ((worldWidthCandidate - 360f) / max(1f, (maxCountInGeneration - 1).toFloat())).coerceIn(190f, 320f)
    val ySpacing = 230f
    val startY = 120f

    val nodes = mutableListOf<ClanTreeNodeVisual>()
    grouped.forEach { (level, persons) ->
        val ordered = persons.sortedBy { it.name }
        val levelWidth = (ordered.size - 1) * xSpacing
        val levelStartX = (worldWidthCandidate - levelWidth) / 2f
        ordered.forEachIndexed { idx, person ->
            nodes += ClanTreeNodeVisual(
                id = person.id,
                name = person.name,
                role = person.roleLabel,
                isDeceased = person.isDeceased,
                generation = level,
                x = levelStartX + (idx * xSpacing),
                y = startY + (level * ySpacing)
            )
        }
    }

    val edges = links
        .filter { peopleById.containsKey(it.fromPersonId) && peopleById.containsKey(it.toPersonId) }
        .map { ClanTreeEdgeVisual(fromId = it.fromPersonId, toId = it.toPersonId, relationType = it.relationType) }

    val maxX = max(worldWidthCandidate, (nodes.maxOfOrNull { it.x } ?: 800f) + 260f)
    val maxY = max(760f, (nodes.maxOfOrNull { it.y } ?: 500f) + 260f)
    return ClanTreeVisualLayout(nodes = nodes, edges = edges, worldWidth = maxX, worldHeight = maxY)
}

@Composable
private fun SocialInteractionPanel(
    targetType: String,
    targetId: Long,
    members: List<FamilyMember>,
    likes: List<SocialTargetLike>,
    comments: List<SocialCommentThread>,
    currentUserId: Long?,
    onToggleTargetLike: (String, Long) -> Unit,
    onAddComment: (String, Long, String, Long?) -> Unit,
    onToggleCommentLike: (Long) -> Unit,
    showCommentsInline: Boolean = true,
    onOpenComments: (() -> Unit)? = null
) {
    val likeEntry = likes.firstOrNull { it.targetType == targetType && it.targetId == targetId }
    val likedIds = likeEntry?.memberIds.orEmpty()
    val targetComments = comments.filter { it.targetType == targetType && it.targetId == targetId }
    val rootComments = targetComments.filter { it.parentCommentId == null }
    var newComment by remember(targetType, targetId) { mutableStateOf("") }
    var replyTo by remember(targetType, targetId) { mutableStateOf<Long?>(null) }
    var replyText by remember(targetType, targetId) { mutableStateOf("") }

    Spacer(Modifier.height(FamilySpacing.sm))
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(FamilyRadius.sm),
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Column(
            modifier = Modifier.padding(FamilySpacing.xs),
            verticalArrangement = Arrangement.spacedBy(FamilySpacing.xs)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(FamilySpacing.xs)) {
                SecondaryActionButton(
                    text = likedIds.size.toString(),
                    onClick = { onToggleTargetLike(targetType, targetId) },
                    leadingIcon = Icons.Filled.Favorite,
                    modifier = Modifier.weight(1f)
                )
                SecondaryActionButton(
                    text = "Binh luan (${targetComments.size})",
                    onClick = { onOpenComments?.invoke() },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    if (!showCommentsInline) return

    PremiumInput(
        value = newComment,
        onValueChange = { newComment = it },
        label = "Them binh luan"
    )
    PrimaryActionButton(
        text = "Gui binh luan",
        onClick = {
            onAddComment(targetType, targetId, newComment, null)
            newComment = ""
        },
        enabled = newComment.isNotBlank(),
        modifier = Modifier.fillMaxWidth()
    )

    if (rootComments.isEmpty()) {
        Text("Chua co binh luan", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    } else {
        rootComments.forEach { comment ->
            val replies = targetComments.filter { it.parentCommentId == comment.id }
            MetaLineCard(
                title = memberName(comment.authorId, members),
                subtitle = comment.content
            )
            Row(horizontalArrangement = Arrangement.spacedBy(FamilySpacing.xs)) {
                SecondaryActionButton(
                    text = if (currentUserId != null && currentUserId in comment.likedMemberIds) {
                        "Bo thich (${comment.likedMemberIds.size})"
                    } else {
                        "Thich (${comment.likedMemberIds.size})"
                    },
                    onClick = { onToggleCommentLike(comment.id) },
                    modifier = Modifier.weight(1f)
                )
                SecondaryActionButton(
                    text = "Tra loi",
                    onClick = { replyTo = if (replyTo == comment.id) null else comment.id },
                    modifier = Modifier.weight(1f)
                )
            }

            if (replyTo == comment.id) {
                PremiumInput(
                    value = replyText,
                    onValueChange = { replyText = it },
                    label = "Tra loi ${memberName(comment.authorId, members)}"
                )
                PrimaryActionButton(
                    text = "Gui tra loi",
                    onClick = {
                        onAddComment(targetType, targetId, replyText, comment.id)
                        replyText = ""
                        replyTo = null
                    },
                    enabled = replyText.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            replies.forEach { reply ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = FamilySpacing.md),
                    shape = RoundedCornerShape(FamilyRadius.sm),
                    color = MaterialTheme.colorScheme.surfaceContainer
                ) {
                    Column(modifier = Modifier.padding(FamilySpacing.sm)) {
                        Text(memberName(reply.authorId, members), style = MaterialTheme.typography.labelMedium)
                        Text(reply.content, style = MaterialTheme.typography.bodyMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(FamilySpacing.xs)) {
                            SecondaryActionButton(
                                text = if (currentUserId != null && currentUserId in reply.likedMemberIds) {
                                    "Bo thich (${reply.likedMemberIds.size})"
                                } else {
                                    "Thich (${reply.likedMemberIds.size})"
                                },
                                onClick = { onToggleCommentLike(reply.id) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
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
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Column(
            modifier = Modifier.padding(FamilySpacing.sm),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(FamilySpacing.xs), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.VpnKey, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text("Mã gia đình", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
            }
            parts.forEach { line ->
                Text(
                    text = line,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
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
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
    val parsedDateTime = parseDateTimeOrNow(value)
    val displayDate = parsedDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(FamilySpacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MetaBadge(label, displayDate, Modifier.weight(1f))
        SecondaryActionButton(
            text = "Chọn ngày giờ",
            onClick = {
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        val picked = LocalDate.of(year, month + 1, dayOfMonth)
                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                val pickedDateTime = LocalDateTime.of(picked, LocalTime.of(hour, minute))
                                onDateSelected(pickedDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                            },
                            parsedDateTime.hour,
                            parsedDateTime.minute,
                            true
                        ).show()
                    },
                    parsedDateTime.year,
                    parsedDateTime.monthValue - 1,
                    parsedDateTime.dayOfMonth
                ).show()
            }
        )
    }
}

private fun parseDateTimeOrNow(raw: String): LocalDateTime {
    val normalized = raw.trim()
    val candidates = listOf(
        normalized,
        normalized.replace(' ', 'T')
    )
    candidates.forEach { candidate ->
        runCatching { return LocalDateTime.parse(candidate, DateTimeFormatter.ISO_LOCAL_DATE_TIME) }
    }
    runCatching {
        val parsedDate = LocalDate.parse(normalized, DateTimeFormatter.ISO_LOCAL_DATE)
        return parsedDate.atTime(9, 0)
    }
    runCatching {
        val parsedDate = LocalDate.parse(normalized.substringBefore(" "), DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        return parsedDate.atTime(9, 0)
    }
    return LocalDateTime.now().withSecond(0).withNano(0)
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
