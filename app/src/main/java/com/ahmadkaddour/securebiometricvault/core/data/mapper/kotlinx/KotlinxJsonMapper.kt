package com.ahmadkaddour.securebiometricvault.core.data.mapper.kotlinx

import com.ahmadkaddour.securebiometricvault.core.data.mapper.JsonMapper
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

/**
 * `JsonMapper` implementation based on KotlinX serialization framework.
 */
@OptIn(InternalSerializationApi::class)
class KotlinxJsonMapper : JsonMapper {
    override fun <T : Any> toJson(value: T, klass: KClass<T>): String {
        return Json.Default.encodeToString(klass.serializer(),value)
    }

    override fun <T : Any> fromJson(json: String, klass: KClass<T>): T {
        return Json.Default.decodeFromString(klass.serializer(), json)
    }
}