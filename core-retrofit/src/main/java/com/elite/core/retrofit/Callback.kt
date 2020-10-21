package com.elite.core.retrofit

import retrofit2.Callback
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

interface Callback<T> : Callback<T> {

    fun onSuccess(response: T?)

    fun onFailure(e: Exception)

    fun noInternetConnection()

    override fun onFailure(call: retrofit2.Call<T>, t: Throwable) {
        if (t is IOException || t is SocketTimeoutException || t is ConnectException) {
            if (t is SocketTimeoutException || t is TimeoutException) {
                onFailure(
                    Exception(
                        Error.CONNECTION_ERROR,
                        "Something went wrong please try after sometime",
                        "",
                        ""
                    )
                )
                //avsInterface.onError(call, new AvsException("Oops something went wrong, please try again later..."));
            } else {
                noInternetConnection()
            }
        } else {
            onFailure(
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
                onSuccess(response.body())
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
                onFailure(Exception(errCode, err.message, err.errorCode, responseBody))
            }
        } catch (e: JsonException) {
            onFailure(Exception(Error.INTERNAL_ERROR, e))
        }
    }
}