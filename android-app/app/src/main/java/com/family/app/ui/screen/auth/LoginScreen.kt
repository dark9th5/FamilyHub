package com.family.app.ui.screen.auth

import androidx.compose.runtime.Composable
import com.family.app.viewmodel.FamilyViewModel

@Composable
fun LoginScreen(
    viewModel: FamilyViewModel,
    onLoggedIn: () -> Unit,
    onNavigateRegister: () -> Unit
) {
    com.family.app.ui.screen.LoginScreen(
        viewModel = viewModel,
        onLoggedIn = onLoggedIn,
        onNavigateRegister = onNavigateRegister
    )
}
