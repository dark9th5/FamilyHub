package com.family.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.family.app.data.reminder.EventReminderScheduler
import com.family.app.di.AppContainer
import com.family.app.ui.FamilyApp
import com.family.app.ui.theme.FamilyTheme
import com.family.app.viewmodel.FamilyViewModel
import com.family.app.viewmodel.FamilyViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppContainer.initialize(applicationContext)
        setContent {
            FamilyTheme {
                val scheduler = EventReminderScheduler(applicationContext)
                val vm: FamilyViewModel = viewModel(
                    factory = FamilyViewModelFactory(
                        AppContainer.repository,
                        AppContainer.sessionStore,
                        AppContainer.chatRealtimeClient,
                        scheduler
                    )
                )
                FamilyApp(vm)
            }
        }
    }
}
