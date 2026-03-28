package com.ahmadkaddour.securebiometricvault.core.data.mapper

import kotlin.reflect.KClass

/**
 * Provides JSON mapping functionality, converting objects to JSON strings and vice versa.
 */
interface JsonMapper {
    /**
     * Converting objects to JSON formatted string.
     *
     * @param value the object to be converted.
     * @param klass the class info of the value object.
     * @return JSON representation of the value object.
     */
    fun <T : Any> toJson(value: T, klass: KClass<T>): String

    /**
     * Converting JSON formatted string into object with specific type.
     *
     * @param json the JSON object to be converted.
     * @param klass the class info of the resulting object.
     * @return an object with type klass based on the json values.
     */
    fun <T: Any> fromJson(json: String, klass: KClass<T>): T?
}

inline fun <reified T : Any> JsonMapper.toJson(value: T): String {
    return toJson(value, T::class)
}

inline fun <reified T : Any> JsonMapper.fromJson(json: String): T? {
    return fromJson(json, T::class)
}