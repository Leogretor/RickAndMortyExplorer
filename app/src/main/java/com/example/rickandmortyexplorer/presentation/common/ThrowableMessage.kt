package com.example.rickandmortyexplorer.presentation.common

import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import retrofit2.HttpException

internal fun Throwable.toUserMessage(defaultMessage: String): String = when (this) {
    is UnknownHostException -> "No internet connection. Please try again."
    is SocketTimeoutException -> "The request timed out. Please try again."
    is HttpException -> when (code()) {
        404 -> "We couldn't find that character."
        in 500..599 -> "The server is unavailable right now. Please try again later."
        else -> defaultMessage
    }

    is IOException -> "We couldn't reach the server. Please try again."
    else -> defaultMessage
}

