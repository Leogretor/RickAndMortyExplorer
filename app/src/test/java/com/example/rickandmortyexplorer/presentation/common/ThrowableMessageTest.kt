package com.example.rickandmortyexplorer.presentation.common

import com.example.rickandmortyexplorer.R
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ThrowableMessageTest {

    @Test
    fun `maps unknown host to no internet message`() {
        assertEquals(
            UiText.StringResource(R.string.error_no_internet),
            UnknownHostException().toUserMessage(UiText.StringResource(R.string.error_load_characters))
        )
    }

    @Test
    fun `maps socket timeout to timeout message`() {
        assertEquals(
            UiText.StringResource(R.string.error_timeout),
            SocketTimeoutException().toUserMessage(UiText.StringResource(R.string.error_load_characters))
        )
    }

    @Test
    fun `maps not found http errors to not found message`() {
        val response = Response.error<String>(
            404,
            "missing".toResponseBody("text/plain".toMediaType())
        )

        assertEquals(
            UiText.StringResource(R.string.error_character_not_found),
            HttpException(response).toUserMessage(UiText.StringResource(R.string.error_load_characters))
        )
    }

    @Test
    fun `maps generic io errors to server reachability message`() {
        assertEquals(
            UiText.StringResource(R.string.error_server_unreachable),
            IOException().toUserMessage(UiText.StringResource(R.string.error_load_characters))
        )
    }

    @Test
    fun `falls back to supplied default message for unexpected errors`() {
        assertEquals(
            UiText.StringResource(R.string.error_load_characters),
            IllegalStateException("Boom").toUserMessage(UiText.StringResource(R.string.error_load_characters))
        )
    }
}
