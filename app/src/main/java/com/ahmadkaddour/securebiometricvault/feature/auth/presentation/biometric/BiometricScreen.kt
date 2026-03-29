package com.ahmadkaddour.securebiometricvault.feature.auth.presentation.biometric

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ahmadkaddour.securebiometricvault.R
import com.ahmadkaddour.securebiometricvault.core.presentation.state.UiState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BiometricScreen(
    activity: FragmentActivity,
    onNavigateToHome: () -> Unit,
    viewModel: BiometricViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.dispatch(BiometricIntent.AuthenticateClicked(activity))
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                BiometricEffect.NavigateToHome -> onNavigateToHome()
            }
        }
    }

    val authState = state.authState
    LaunchedEffect(authState) {
        if (authState is UiState.Failure) {
            errorMessage = authState.error.message
        }
    }

    errorMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = {
                errorMessage = null
                viewModel.dispatch(BiometricIntent.ErrorDismissed)
            },
            icon = { Icon(Icons.Outlined.Warning, contentDescription = null) },
            title = { Text(stringResource(R.string.biometric_auth_failed)) },
            text = { Text(msg) },
            confirmButton = {
                Button(onClick = {
                    errorMessage = null
                    viewModel.dispatch(BiometricIntent.AuthenticateClicked(activity))
                }) { Text(stringResource(R.string.biometric_try_again)) }
            },
        )
    }

    BiometricContent(
        state = state,
        onRetry = {
            viewModel.dispatch(
                BiometricIntent.AuthenticateClicked(activity)
            )
        },
    )
}

@Composable
private fun BiometricContent(
    state: BiometricUiModel,
    onRetry: () -> Unit,
) {
    val isLoading = state.authState is UiState.Loading

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(360.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                            MaterialTheme.colorScheme.background,
                        ),
                    ),
                    shape = CircleShape,
                ),
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .windowInsetsPadding(WindowInsets.systemBars),
        ) {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(56.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Outlined.AccountBalance,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(28.dp),
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.biometric_verify_identity),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.biometric_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(56.dp))

            PulsingFingerprintButton(
                isLoading = isLoading,
                onClick = onRetry,
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = if (isLoading)
                    stringResource(R.string.biometric_waiting)
                else
                    stringResource(R.string.biometric_tap_to_authenticate),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(56.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(
                    Icons.Outlined.Shield,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp),
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.biometric_secured_by),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun PulsingFingerprintButton(
    isLoading: Boolean,
    onClick: () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isLoading) 1.08f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "scale",
    )

    Box(contentAlignment = Alignment.Center) {
        // Outer pulse ring (only visible while loading)
        if (isLoading) {
            Surface(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                content = {},
            )
        }

        // Icon button
        FilledIconButton(
            onClick = onClick,
            modifier = Modifier.size(88.dp),
            shape = CircleShape,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
            enabled = !isLoading,
        ) {
            Icon(
                imageVector = Icons.Outlined.Fingerprint,
                contentDescription = stringResource(R.string.biometric_accessibility_fingerprint),
                modifier = Modifier.size(44.dp),
            )
        }
    }
}
