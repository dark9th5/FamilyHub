package com.family.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Cottage
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.family.app.ui.navigation.Routes
import com.family.app.ui.navigation.bottomRoutes
import com.family.app.ui.screen.ClanHubScreen
import com.family.app.ui.screen.FamilyHubScreen
import com.family.app.ui.screen.HomeScreen
import com.family.app.ui.screen.LoginScreen
import com.family.app.ui.screen.ProfileScreen
import com.family.app.ui.screen.RegisterScreen
import com.family.app.ui.screen.TimelineScreen
import com.family.app.ui.theme.FamilyElevation
import com.family.app.ui.theme.FamilyRadius
import com.family.app.ui.theme.FamilySpacing
import com.family.app.viewmodel.FamilyViewModel

private val routeIcons: Map<String, ImageVector> = mapOf(
    Routes.Home.route to Icons.Filled.Home,
    Routes.Family.route to Icons.Filled.Cottage,
    Routes.Clan.route to Icons.Filled.Diversity3,
    Routes.Timeline.route to Icons.Filled.Timeline,
    Routes.Profile.route to Icons.Filled.AccountCircle
)

@Composable
fun FamilyApp(viewModel: FamilyViewModel) {
    val navController = rememberNavController()
    val state by viewModel.state.collectAsState()
    val showClan = (state.ageYears ?: 16) >= 16
    val visibleBottomRoutes = if (showClan) bottomRoutes else bottomRoutes.filter { it != Routes.Clan }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (state.isAuthenticated) {
                val navPadding = WindowInsets.navigationBars.asPaddingValues()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = FamilySpacing.sm)
                        .padding(bottom = FamilySpacing.xs + navPadding.calculateBottomPadding())
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        tonalElevation = FamilyElevation.low,
                        shadowElevation = FamilyElevation.high,
                        shape = RoundedCornerShape(FamilyRadius.lg),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                    ) {
                        Row(
                            modifier = Modifier
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            androidx.compose.ui.graphics.Color(0xFFD4EBF8),
                                            androidx.compose.ui.graphics.Color(0xFFF5FAFE),
                                            androidx.compose.ui.graphics.Color(0xFFEDD7E3)
                                        )
                                    )
                                )
                                .padding(horizontal = FamilySpacing.xs, vertical = FamilySpacing.xs)
                        ) {
                            val backStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = backStackEntry?.destination
                            visibleBottomRoutes.forEach { route ->
                                val selected = currentDestination?.hierarchy?.any { it.route == route.route } == true
                                val showFamilyBirthdayBadge =
                                    route.route == Routes.Family.route && state.birthdayAlerts.isNotEmpty()
                                val container = if (selected) {
                                    Brush.linearGradient(
                                        colors = listOf(
                                            androidx.compose.ui.graphics.Color(0xFFD4EBF8),
                                            androidx.compose.ui.graphics.Color(0xFFF4FAFD),
                                            androidx.compose.ui.graphics.Color(0xFFEDD7E3)
                                        ),
                                    )
                                } else {
                                    Brush.linearGradient(
                                        colors = listOf(
                                            androidx.compose.ui.graphics.Color(0xFFE8F5FB),
                                            androidx.compose.ui.graphics.Color(0xFFFAFCFE),
                                            androidx.compose.ui.graphics.Color(0xFFF5E9EF)
                                        ),
                                    )
                                }
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 3.dp)
                                        .clickable {
                                            if (!selected) {
                                                navController.navigate(route.route) {
                                                    popUpTo(navController.graph.findStartDestination().id) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        },
                                    shape = RoundedCornerShape(FamilyRadius.md),
                                    tonalElevation = if (selected) 2.dp else 0.dp,
                                    color = androidx.compose.ui.graphics.Color.Transparent
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(container, RoundedCornerShape(FamilyRadius.md))
                                            .padding(horizontal = FamilySpacing.xxs, vertical = FamilySpacing.xs),
                                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                                    ) {
                                        Box(modifier = Modifier.size(22.dp), contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = routeIcons[route.route] ?: Icons.Filled.Home,
                                                contentDescription = route.title,
                                                tint = if (selected) {
                                                    androidx.compose.ui.graphics.Color(0xFF2F4A5A)
                                                } else {
                                                    androidx.compose.ui.graphics.Color(0xFF506879)
                                                },
                                                modifier = Modifier.size(20.dp)
                                            )
                                            if (showFamilyBirthdayBadge) {
                                                Box(
                                                    modifier = Modifier
                                                        .align(Alignment.TopEnd)
                                                        .size(8.dp)
                                                        .background(MaterialTheme.colorScheme.error, CircleShape)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = if (state.isAuthenticated) Routes.Home.route else Routes.Login.route,
                modifier = Modifier.padding(if (state.isAuthenticated) padding else PaddingValues(0.dp))
            ) {
                composable(Routes.Login.route) {
                    LoginScreen(
                        viewModel = viewModel,
                        onLoggedIn = {
                            navController.navigate(Routes.Home.route) {
                                popUpTo(Routes.Login.route) { inclusive = true }
                            }
                        },
                        onNavigateRegister = {
                            navController.navigate(Routes.Register.route)
                        }
                    )
                }
                composable(Routes.Register.route) {
                    RegisterScreen(
                        viewModel = viewModel,
                        onRegistered = {
                            navController.navigate(Routes.Home.route) {
                                popUpTo(Routes.Login.route) { inclusive = true }
                            }
                        },
                        onNavigateLogin = {
                            navController.popBackStack()
                        }
                    )
                }
                composable(Routes.Home.route) { HomeScreen(viewModel) }
                composable(Routes.Family.route) { FamilyHubScreen(viewModel) }
                if (showClan) {
                    composable(Routes.Clan.route) { ClanHubScreen(viewModel) }
                }
                composable(Routes.Timeline.route) { TimelineScreen(viewModel) }
                composable(Routes.Profile.route) { ProfileScreen(viewModel) }
            }
        }
    }
}
