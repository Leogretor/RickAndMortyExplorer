package com.example.rickandmortyexplorer.presentation.common

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
            "No internet connection. Please try again.",
            UnknownHostException().toUserMessage(DEFAULT_MESSAGE)
        )
    }

    @Test
    fun `maps socket timeout to timeout message`() {
        assertEquals(
            "The request timed out. Please try again.",
            SocketTimeoutException().toUserMessage(DEFAULT_MESSAGE)
        )
    }

    @Test
    fun `maps not found http errors to not found message`() {
        val response = Response.error<String>(
            404,
            "missing".toResponseBody("text/plain".toMediaType())
        )

        assertEquals(
            "We couldn't find that character.",
            HttpException(response).toUserMessage(DEFAULT_MESSAGE)
        )
    }

    @Test
    fun `maps generic io errors to server reachability message`() {
        assertEquals(
            "We couldn't reach the server. Please try again.",
            IOException().toUserMessage(DEFAULT_MESSAGE)
        )
    }

    @Test
    fun `falls back to supplied default message for unexpected errors`() {
        assertEquals(
            DEFAULT_MESSAGE,
            IllegalStateException("Boom").toUserMessage(DEFAULT_MESSAGE)
        )
    }

    private companion object {
        const val DEFAULT_MESSAGE = "Unable to load characters right now."
    }
}

