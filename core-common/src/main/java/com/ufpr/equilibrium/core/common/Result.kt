package com.ufpr.equilibrium.core.common

sealed class Result<out T> {
    data class Success<T>(val value: T) : Result<T>()
    data class Error(val cause: Throwable) : Result<Nothing>()
}

inline fun <T> Result<T>.onSuccess(block: (T) -> Unit): Result<T> {
    if (this is Result.Success) block(value)
    return this
}

inline fun <T> Result<T>.onError(block: (Throwable) -> Unit): Result<T> {
    if (this is Result.Error) block(cause)
    return this
}


