package com.family.app.ui.screen.auth

import androidx.compose.runtime.Composable
import com.family.app.viewmodel.FamilyViewModel

@Composable
fun RegisterScreen(
    viewModel: FamilyViewModel,
    onRegistered: () -> Unit,
    onNavigateLogin: () -> Unit
) {
    com.family.app.ui.screen.RegisterScreen(
        viewModel = viewModel,
        onRegistered = onRegistered,
        onNavigateLogin = onNavigateLogin
    )
}
