package com.ahmadkaddour.securebiometricvault.feature.auth.presentation.login

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.biometric.BiometricManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.ahmadkaddour.securebiometricvault.R
import com.ahmadkaddour.securebiometricvault.core.presentation.state.UiState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToBiometric: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    viewModel: LoginViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is LoginEffect.NavigateToHome -> onNavigateToHome()
                is LoginEffect.NavigateToBiometric -> onNavigateToBiometric()
                is LoginEffect.NavigateToForgotPassword -> onNavigateToForgotPassword()
                is LoginEffect.ShowError -> errorMessage = effect.error.message
                is LoginEffect.OpenBiometricSettings -> {
                    val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                            putExtra(
                                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                BiometricManager.Authenticators.BIOMETRIC_STRONG,
                            )
                        }
                    } else {
                        Intent(Settings.ACTION_SECURITY_SETTINGS)
                    }
                    context.startActivity(intent)
                }
            }
        }
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.onResume()
        }
    }

    // Error dialog
    errorMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = {
                errorMessage = null
                viewModel.dispatch(LoginIntent.ErrorDismissed)
            },
            title = { Text(stringResource(R.string.login_failed)) },
            text = { Text(msg) },
            confirmButton = {
                TextButton(onClick = {
                    errorMessage = null
                    viewModel.dispatch(LoginIntent.ErrorDismissed)
                }) { Text(stringResource(R.string.common_ok)) }
            },
        )
    }

    if (state.showBiometricDisabledBlock) {
        AlertDialog(
            onDismissRequest = { },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Fingerprint,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                )
            },
            title = { Text(stringResource(R.string.login_biometric_required_title)) },
            text = { Text(stringResource(R.string.login_biometric_required_message)) },
            confirmButton = {
                Button(onClick = { viewModel.dispatch(LoginIntent.BiometricDisabledAcknowledged) }) {
                    Text(stringResource(R.string.login_biometric_open_settings))
                }
            },
        )
    }

    LoginContent(
        state = state,
        onIntent = viewModel::dispatch,
    )
}

@Composable
private fun LoginContent(
    state: LoginUiModel,
    onIntent: (LoginIntent) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val isLoading = state.loginState is UiState.Loading

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(Unit) {
                detectTapGestures { focusManager.clearFocus() }
            },
    ) {
        // Decorative gradient header band
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer,
                        ),
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(60.dp))

            // Logo / wordmark
            BrandHeader()

            Spacer(Modifier.height(48.dp))

            // Card containing form fields
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(
                        WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal)
                    ),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {

                    Text(
                        text = stringResource(R.string.login_sign_in),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Text(
                        text = stringResource(R.string.login_welcome_back),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(Modifier.height(4.dp))

                    // Username field
                    OutlinedTextField(
                        value = state.username,
                        onValueChange = { onIntent(LoginIntent.UsernameChanged(it)) },
                        label = { Text(stringResource(R.string.login_username_label)) },
                        leadingIcon = {
                            Icon(Icons.Outlined.Person, contentDescription = null)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next,
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) },
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        shape = MaterialTheme.shapes.medium,
                    )

                    // Password field
                    OutlinedTextField(
                        value = state.password,
                        onValueChange = { onIntent(LoginIntent.PasswordChanged(it)) },
                        label = { Text(stringResource(R.string.login_password_label)) },
                        leadingIcon = {
                            Icon(Icons.Outlined.Lock, contentDescription = null)
                        },
                        trailingIcon = {
                            IconButton(onClick = { onIntent(LoginIntent.TogglePasswordVisibility) }) {
                                Icon(
                                    imageVector = if (state.passwordVisible)
                                        Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                    contentDescription = if (state.passwordVisible)
                                        stringResource(R.string.login_hide_password) else stringResource(
                                        R.string.login_show_password
                                    ),
                                )
                            }
                        },
                        visualTransformation = if (state.passwordVisible)
                            VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                onIntent(LoginIntent.LoginClicked)
                            },
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        shape = MaterialTheme.shapes.medium,
                    )

                    // Forgot password (visible but inactive per requirements)
                    TextButton(
                        onClick = { onIntent(LoginIntent.ForgotPasswordClicked) },
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Text(stringResource(R.string.login_forgot_password))
                    }

                    // Sign in button
                    Button(
                        onClick = { onIntent(LoginIntent.LoginClicked) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        enabled = !isLoading && state.username.isNotBlank() && state.password.isNotBlank(),
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        } else {
                            Text(
                                stringResource(R.string.login_sign_in),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }

                    // Biometric divider (shown only if biometric is available and has a session)
                    AnimatedVisibility(
                        visible = state.biometricAvailable && state.hasExistingSession,
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut(),
                    ) {
                        BiometricLoginHint(modifier = Modifier.fillMaxWidth())
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // Fine-print
            Text(
                text = stringResource(R.string.login_security_footer),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun BrandHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Placeholder logo — replace with a real vector drawable
        Surface(
            modifier = Modifier.size(72.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.AccountBalance,
                    contentDescription = stringResource(R.string.login_logo_description),
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.app_name_short),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            letterSpacing = 6.sp,
        )

        Text(
            text = stringResource(R.string.login_brand_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f),
        )
    }
}

@Composable
private fun BiometricLoginHint(modifier: Modifier = Modifier) {
    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.Fingerprint,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.login_biometric_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}