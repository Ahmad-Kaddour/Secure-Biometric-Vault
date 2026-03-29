package com.ahmadkaddour.securebiometricvault.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.GppBad
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ahmadkaddour.securebiometricvault.R
import com.ahmadkaddour.securebiometricvault.core.presentation.theme.AppTheme
import com.ahmadkaddour.securebiometricvault.core.security.root.RootDetector
import com.ahmadkaddour.securebiometricvault.feature.auth.presentation.biometric.BiometricScreen
import com.ahmadkaddour.securebiometricvault.feature.auth.presentation.login.LoginScreen
import org.koin.android.ext.android.inject


class MainActivity : FragmentActivity() {

    private val rootDetector: RootDetector by inject()
    private var isRooted by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        checkRoot()

        setContent {
            AppTheme {
                if (isRooted) {
                    RootedDeviceScreen()
                } else {
                    MainNavHost(activity = this)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkRoot()
    }

    private fun checkRoot() {
        isRooted = rootDetector.isRooted()
    }
}


private object Routes {
    const val LOGIN = "login"
    const val BIOMETRIC = "biometric"
    const val HOME = "home"
}

@Composable
private fun MainNavHost(activity: FragmentActivity) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN,
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToBiometric = {
                    navController.navigate(Routes.BIOMETRIC) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToForgotPassword = {
                    // Not Implemented
                },
            )
        }

        composable(Routes.BIOMETRIC) {
            BiometricScreen(
                activity = activity,
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.BIOMETRIC) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
            )
        }
    }
}

@Composable
private fun RootedDeviceScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.errorContainer,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.GppBad,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(72.dp),
                )

                Text(
                    text = stringResource(R.string.root_detection_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = stringResource(R.string.root_detection_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
                    ),
                ) {
                    Text(
                        text = stringResource(R.string.root_detection_error_code),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(onLogout: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name_short)) },
                actions = {
                    TextButton(onClick = onLogout) {
                        Text(stringResource(R.string.home_sign_out))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    Icons.Outlined.AccountBalance,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    stringResource(R.string.home_welcome),
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                    stringResource(R.string.home_auth_success),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
