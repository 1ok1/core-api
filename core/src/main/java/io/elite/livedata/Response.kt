package io.elite.livedata

import io.elite.auth.AuthException

enum class Status {
    SUCCESS, FAILURE, LOADING, NO_INTERNET
}

class Resource<T> private constructor(val status: Status, val data: T?, val exception: AuthException?) {
    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> failure(exception: AuthException?): Resource<T> {
            return Resource(Status.FAILURE, null, exception)
        }

        fun <T> loading(): Resource<T> {
            return Resource(Status.LOADING, null, null)
        }

        fun <T> noInternet(): Resource<T> {
            return Resource(Status.NO_INTERNET, null, null)
        }
    }
}
