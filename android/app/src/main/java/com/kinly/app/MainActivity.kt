package com.kinly.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kinly.app.auth.AppAuthRepository
import com.kinly.app.auth.AuthConfig
import com.kinly.app.presentation.AuthViewModel
import com.kinly.app.data.HttpBackendStatusRepository
import com.kinly.app.presentation.BackendStatusViewModel
import com.kinly.app.ui.KinlyApp
import com.kinly.app.ui.theme.KinlyTheme

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModel.factory(AppAuthRepository(applicationContext, AuthConfig.fromBuild()))
    }
    private val authLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        authViewModel.complete(result.data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = HttpBackendStatusRepository(BuildConfig.BACKEND_READINESS_URL)
        val factory = BackendStatusViewModel.factory(repository)

        setContent {
            KinlyTheme {
                val statusViewModel: BackendStatusViewModel = viewModel(factory = factory)
                KinlyApp(
                    viewModel = statusViewModel,
                    authViewModel = authViewModel,
                    onSignIn = {
                        authViewModel.beginSignIn()?.let(authLauncher::launch)
                    },
                )
            }
        }
    }
}
