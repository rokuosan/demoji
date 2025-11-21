package org.yaken.demoji.common

sealed interface Result<out T, out E> {
    data class Ok<out T>(val value: T) : Result<T, Nothing> {
        val isSuccess: Boolean = true
        val isError: Boolean = false
    }

    data class Err<out E>(val error: E) : Result<Nothing, E> {
        val isSuccess: Boolean = false
        val isError: Boolean = true
    }
}

data class Error<T>(
    val code: String,
    val message: T
)

typealias ErrorS = Error<String>

fun <T> ok(value: T): Result.Ok<T> = Result.Ok(value)
fun <E> err(error: E): Result.Err<E> = Result.Err(error)
