package io.elite.livedata

import io.elite.auth.AuthException

enum class Status {
    SUCCESS, FAILURE, LOADING, NO_INTERNET
}

class Response<T> private constructor(val status: Status, val data: T?, val exception: AuthException?) {
    companion object {
        fun <T> success(data: T?): Response<T> {
            return Response(Status.SUCCESS, data, null)
        }

        fun <T> failure(exception: AuthException?): Response<T> {
            return Response(Status.FAILURE, null, exception)
        }

        fun <T> loading(): Response<T> {
            return Response(Status.LOADING, null, null)
        }

        fun <T> noInternet(): Response<T> {
            return Response(Status.NO_INTERNET, null, null)
        }
    }
}
