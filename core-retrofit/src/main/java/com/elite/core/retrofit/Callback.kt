package com.elite.core.retrofit

import retrofit2.Call
import retrofit2.Callback
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException


fun<T> Call<T>.enqueue(callback: com.elite.core.retrofit.Callback<T>.() -> Unit) {
    val callbackBk = Callback<T>()
    callback.invoke(callbackBk)
    this.enqueue(callbackBk)
    callbackBk.loading?.invoke()
}

class Callback<T> : Callback<T> {
    var loading : (() -> Unit?)? = null
    var onSuccess : ((r: T?) -> Unit)? = null
    var onFailure :  ((e: Exception) -> Unit)? = null
    var noInternetConnection: (() -> Unit?)? = null

    override fun onFailure(call: retrofit2.Call<T>, t: Throwable) {
        if (t is IOException || t is SocketTimeoutException || t is ConnectException) {
            if (t is SocketTimeoutException || t is TimeoutException) {
                onFailure?.invoke(
                    Exception(
                        Error.CONNECTION_ERROR,
                        "Something went wrong please try after sometime",
                        "",
                        ""
                    )
                )
                //avsInterface.onError(call, new AvsException("Oops something went wrong, please try again later..."));
            } else {
                noInternetConnection?.invoke()
            }
        } else {
            onFailure?.invoke(
                Exception(
                    Error.CONNECTION_ERROR,
                    "Something went wrong please try after sometime",
                    "",
                    ""
                )
            )
        }
    }

    override fun onResponse(call: retrofit2.Call<T>, response: retrofit2.Response<T>) {
        val code = response.code()
        val responseBody = Util.getJsonBody(response)
        try {
            if (code == 200) {
                onSuccess?.invoke(response.body())
            } else {
                val err = Util.parseJson<ResponseConverter.ErrorResponse>(
                    Service.gson,
                    code,
                    responseBody,
                    ResponseConverter.ErrorResponse::class.java
                )
                err!!.response = responseBody
                val errCode = when (code) {
                    400 -> Error.BAD_REQUEST
                    401 -> Error.UNAUTHORIZED
                    402 -> Error.REQUEST_FAILED
                    403 -> Error.INVALID_SESSION
                    500 -> Error.INTERNAL_ERROR
                    else -> Error.UNEXPECTED_CODE
                }
                onFailure?.invoke(Exception(errCode, err.message, err.errorCode, responseBody))
            }
        } catch (e: JsonException) {
            onFailure?.invoke(Exception(Error.INTERNAL_ERROR, e))
        }
    }
}