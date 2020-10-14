package io.elite.core

import android.os.Handler

import java.io.IOException

import io.elite.auth.AuthError
import io.elite.auth.AuthException
import io.elite.auth.LoginResponse
import okhttp3.*

/**
 * Created by loki on 19/08/16.
 */

class LoginCall<T, E : Exception>(/* Underlying okhttp call */
        private val rawCall: okhttp3.Call, private val converter: Converter<T, E>) {


    val isExecuted: Boolean
        get() = rawCall.isExecuted()

    val isCancelled: Boolean
        get() = rawCall.isCanceled()

    fun request(): Request {
        return rawCall.request()
    }

    fun enqueue(
        loading: loading,
        onSuccess: onSuccess<T>,
        onFailure: onFailure<E>,
        noInternetConnection: noInternetConnection
    ) {
        loading()
        rawCall.enqueue(object : okhttp3.Callback {
            @Throws(IOException::class)
            override fun onResponse(call: okhttp3.Call, rawResponse: okhttp3.Response) {
                val response: T?
                try {
                    response = converter.fromResponse(rawResponse)
                } catch (e: Exception) {
                    callFailure(converter.castException(e))
                    return
                }

                callSuccess(response)
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                try {
                    callFailure(converter.fromIOException(e))
                } catch (t: Throwable) {
                    t.printStackTrace()
                }

            }

            private fun callFailure(he: E) {
                try {
                    val authException = he as AuthException
                    if (authException.code == AuthError.CONNECTION_ERROR) {
                        noInternetConnection()
                    } else {
                        onFailure(he)
                    }
                } catch (t: Throwable) {
                    t.printStackTrace()
                }

            }

            private fun callSuccess(response: T?) {
                try {
                    setUserDetails(response)
                    onSuccess(response)
                } catch (t: Throwable) {
                    t.printStackTrace()
                }

            }
        })
    }

    fun enqueueOnUIThread(
        loading: loading,
        onSuccess: onSuccess<T>,
        onFailure: onFailure<E>,
        noInternetConnection: noInternetConnection
    ) {
        val handler = Handler()
        loading()
        rawCall.enqueue(object : okhttp3.Callback {
            @Throws(IOException::class)
            override fun onResponse(call: okhttp3.Call, rawResponse: okhttp3.Response) {
                val response: T?
                try {
                    response = converter.fromResponse(rawResponse)
                } catch (e: Exception) {
                    callFailure(converter.castException(e))
                    return
                }

                callSuccess(response)
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                try {
                    callFailure(converter.fromIOException(e))
                } catch (t: Throwable) {
                    t.printStackTrace()
                }

            }

            private fun callFailure(he: E) {
                handler.post {
                    try {
                        val authException = he as AuthException
                        if (authException.code == AuthError.CONNECTION_ERROR) {
                            noInternetConnection()
                        } else {
                            onFailure(he)
                        }
                    } catch (t: Throwable) {
                        t.printStackTrace()
                    }
                }
            }

            private fun callSuccess(response: T?) {
                handler.post {
                    try {
                        setUserDetails(response)
                        onSuccess(response)
                    } catch (t: Throwable) {
                        t.printStackTrace()
                    }
                }
            }
        })
    }

    fun setUserDetails(response: T?) {
        val loginResponse = response as LoginResponse
        Elite.instance.userToken = loginResponse.auth_token
        if (loginResponse.auth_token != null) {
            Elite.instance.setLogin()
        }
        Elite.instance.userId = loginResponse.hasura_id
    }

//    @Throws(E::class)
    fun execute(): T? {
        try {
            return converter.fromResponse(rawCall.execute())
        } catch (e: IOException) {
            throw converter.fromIOException(e)
        }

    }

    fun cancel() {
        rawCall.cancel()
    }
}
