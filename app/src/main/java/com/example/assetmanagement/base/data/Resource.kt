package com.example.assetmanagement.base.data
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap


/**
 * @author SamuelSep on 4/20/2021.
 */
enum class ResponseStatus {
    SUCCESS,
    ERROR,
    LOADING
}

sealed class ResultState<out R> {

    data class Success<out T>(val data: T) : ResultState<T>()
    data class Error(val exception: Exception) : ResultState<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
        }
    }

    fun onSuccess(
        block: (R) -> Unit
    ): ResultState<R> {
        if (this is Success) block.invoke(this.data)
        return this
    }

    suspend fun onSuspendSuccess(
        block: suspend (R) -> Unit
    ): ResultState<R> {
        if (this is Success) block.invoke(this.data)
        return this
    }

    fun onError(
        block: (Exception) -> Unit
    ): ResultState<R> {
        if (this is Error) block.invoke(this.exception)
        return this
    }

    suspend fun onSuspendError(
        block: suspend (Exception) -> Unit
    ): ResultState<R> {
        if (this is Error) block.invoke(this.exception)
        return this
    }

    fun isSuccess(): Boolean {
        return this is Success
    }
}

inline fun <T, R> ResultState<T>.map(transform: (T) -> R): ResultState<R> {
    return when (this) {
        is ResultState.Success -> {
            ResultState.Success(transform.invoke(this.data))
        }
        is ResultState.Error -> {
            ResultState.Error(this.exception)
        }
    }
}

fun <T> ResultState<LinkedTreeMap<*, *>>.genericParse(type: Class<T>): ResultState<T> {
    return when (this) {
        is ResultState.Success -> {
            val data = (this.data).linkedTreeParse(type)
            ResultState.Success(data)
        }
        is ResultState.Error -> {
            ResultState.Error(this.exception)
        }
    }
}

private fun <T> LinkedTreeMap<*,*>.linkedTreeParse(type: Class<T>): T {
    val gson = Gson()
    val json = gson.toJsonTree(this).asJsonObject
    return gson.fromJson(json, type)
}
