package com.example.rickandmortyexplorer.presentation.common

import com.example.rickandmortyexplorer.R
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import retrofit2.HttpException

internal fun Throwable.toUserMessage(defaultMessage: UiText): UiText = when (this) {
    is UnknownHostException -> UiText.StringResource(R.string.error_no_internet)
    is SocketTimeoutException -> UiText.StringResource(R.string.error_timeout)
    is HttpException -> when (code()) {
        404 -> UiText.StringResource(R.string.error_character_not_found)
        in 500..599 -> UiText.StringResource(R.string.error_server_unavailable)
        else -> defaultMessage
    }

    is IOException -> UiText.StringResource(R.string.error_server_unreachable)
    else -> defaultMessage
}
