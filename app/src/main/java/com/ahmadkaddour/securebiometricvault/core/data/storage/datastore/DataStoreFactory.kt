package com.ahmadkaddour.securebiometricvault.core.data.storage.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

const val DATA_STORE_FILE_NAME = "preferences.pb"

object DataStoreFactory {
    fun create(pathFactory: DataStorePathProvider): DataStore<Preferences> =
        PreferenceDataStoreFactory.createWithPath(
            produceFile = { pathFactory.producePath().toPath() }
        )
}

fun interface DataStorePathProvider {
    fun producePath(): String
}