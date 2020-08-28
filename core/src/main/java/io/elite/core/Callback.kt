package io.elite.core

interface Callback<T, E> {
    fun onSuccess(response: T?)

    fun onFailure(e: E)

    fun noInternetConnection()
}
