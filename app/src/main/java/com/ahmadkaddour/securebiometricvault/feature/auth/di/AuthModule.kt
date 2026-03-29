package com.ahmadkaddour.securebiometricvault.feature.auth.di

import com.ahmadkaddour.securebiometricvault.core.di.InstanceNames
import com.ahmadkaddour.securebiometricvault.core.security.biometric.BiometricAvailabilityChecker
import com.ahmadkaddour.securebiometricvault.core.security.biometric.android.AndroidBiometricAvailabilityChecker
import com.ahmadkaddour.securebiometricvault.feature.auth.data.repository.DefaultAuthRepository
import com.ahmadkaddour.securebiometricvault.feature.auth.data.source.local.AuthLocalDataSource
import com.ahmadkaddour.securebiometricvault.feature.auth.data.source.local.DefaultAuthLocalDataSource
import com.ahmadkaddour.securebiometricvault.feature.auth.data.source.remote.AuthRemoteDataSource
import com.ahmadkaddour.securebiometricvault.feature.auth.data.source.remote.MockAuthRemoteDataSource
import com.ahmadkaddour.securebiometricvault.feature.auth.domain.repository.AuthRepository
import com.ahmadkaddour.securebiometricvault.feature.auth.domain.usecase.CheckBiometricAvailabilityUseCase
import com.ahmadkaddour.securebiometricvault.feature.auth.domain.usecase.GetStoredTokenUseCase
import com.ahmadkaddour.securebiometricvault.feature.auth.domain.usecase.HasValidSessionUseCase
import com.ahmadkaddour.securebiometricvault.feature.auth.domain.usecase.LoginUseCase
import com.ahmadkaddour.securebiometricvault.feature.auth.presentation.biometric.BiometricViewModel
import com.ahmadkaddour.securebiometricvault.feature.auth.presentation.login.LoginViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val authModule = module {
    single<AuthRemoteDataSource> { MockAuthRemoteDataSource(get()) }
    single<AuthLocalDataSource> { DefaultAuthLocalDataSource(get(named(InstanceNames.SECURE_APP_CACHE_INSTANCE_NAME))) }

    single<AuthRepository> { DefaultAuthRepository(get(), get()) }

    factory<BiometricAvailabilityChecker> { AndroidBiometricAvailabilityChecker(androidContext()) }

    factory { LoginUseCase(get(), Dispatchers.IO) }
    factory { HasValidSessionUseCase(get()) }
    factory { GetStoredTokenUseCase(get()) }
    factory { CheckBiometricAvailabilityUseCase(get()) }

    viewModel { LoginViewModel(get(), get(), get(), get()) }
    viewModel { BiometricViewModel(get(), get(), get()) }
}