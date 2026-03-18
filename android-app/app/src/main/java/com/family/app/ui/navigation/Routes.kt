package com.family.app.ui.navigation

sealed class Routes(val route: String, val title: String) {
    data object Login : Routes("login", "Đăng nhập")
    data object Register : Routes("register", "Đăng ký")
    data object Home : Routes("home", "Trang chủ")
    data object Family : Routes("family", "Gia đình")
    data object Clan : Routes("clan", "Dòng họ")
    data object Timeline : Routes("timeline", "Bảng tin")
    data object Profile : Routes("profile", "Tài khoản")
}

val bottomRoutes = listOf(
    Routes.Home,
    Routes.Family,
    Routes.Clan,
    Routes.Timeline,
    Routes.Profile
)
