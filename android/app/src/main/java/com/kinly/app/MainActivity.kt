package com.kinly.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kinly.app.data.HttpBackendStatusRepository
import com.kinly.app.presentation.BackendStatusViewModel
import com.kinly.app.ui.KinlyApp
import com.kinly.app.ui.theme.KinlyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = HttpBackendStatusRepository(BuildConfig.BACKEND_READINESS_URL)
        val factory = BackendStatusViewModel.factory(repository)

        setContent {
            KinlyTheme {
                val statusViewModel: BackendStatusViewModel = viewModel(factory = factory)
                KinlyApp(viewModel = statusViewModel)
            }
        }
    }
}
