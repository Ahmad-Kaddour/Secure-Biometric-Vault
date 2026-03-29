package com.ahmadkaddour.securebiometricvault.app.di

import android.security.keystore.KeyProperties
import com.ahmadkaddour.securebiometricvault.core.data.mapper.JsonMapper
import com.ahmadkaddour.securebiometricvault.core.data.mapper.kotlinx.KotlinxJsonMapper
import com.ahmadkaddour.securebiometricvault.core.data.network.ApiClient
import com.ahmadkaddour.securebiometricvault.core.data.network.ktor.HttpClientFactory
import com.ahmadkaddour.securebiometricvault.core.data.network.ktor.KtorApiClient
import com.ahmadkaddour.securebiometricvault.core.data.storage.AppCache
import com.ahmadkaddour.securebiometricvault.core.data.storage.datastore.DATA_STORE_FILE_NAME
import com.ahmadkaddour.securebiometricvault.core.data.storage.datastore.DataStoreCache
import com.ahmadkaddour.securebiometricvault.core.data.storage.datastore.DataStoreFactory
import com.ahmadkaddour.securebiometricvault.core.data.storage.datastore.DefaultPreferenceDataStoreManager
import com.ahmadkaddour.securebiometricvault.core.data.storage.datastore.PreferenceDataStoreManager
import com.ahmadkaddour.securebiometricvault.core.data.storage.secure.SecureAppCache
import com.ahmadkaddour.securebiometricvault.core.di.InstanceNames
import com.ahmadkaddour.securebiometricvault.core.exception.ExceptionHandler
import com.ahmadkaddour.securebiometricvault.core.exception.MockExceptionHandler
import com.ahmadkaddour.securebiometricvault.core.security.cipher.StringCipher
import com.ahmadkaddour.securebiometricvault.core.security.cipher.aes.AesGcmKeyStoreStringCipher
import com.ahmadkaddour.securebiometricvault.core.security.root.RootDetector
import com.ahmadkaddour.securebiometricvault.core.security.root.rootbeer.RootBeerRootDetector
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.security.KeyStore
import javax.crypto.KeyGenerator

val appModule = module {
    single<JsonMapper> { KotlinxJsonMapper() }

    single<ApiClient> {
        KtorApiClient(HttpClientFactory.create(OkHttp.create()))
    }

    factory<StringCipher> {
        AesGcmKeyStoreStringCipher(
            keyAlias = "secure_app_cache_key_v1",
            keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) },
            keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                "AndroidKeyStore"
            ),
        )
    }

    factory<RootDetector> { RootBeerRootDetector(androidContext()) }

    single<PreferenceDataStoreManager> {
        DefaultPreferenceDataStoreManager(
            DataStoreFactory.create {
                androidContext().filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath
            }
        )
    }
    single<AppCache> { DataStoreCache(get(), get()) }
    single<AppCache>(named(InstanceNames.SECURE_APP_CACHE_INSTANCE_NAME)) {
        SecureAppCache(get(), get(), get())
    }

    single<ExceptionHandler> { MockExceptionHandler() }
}