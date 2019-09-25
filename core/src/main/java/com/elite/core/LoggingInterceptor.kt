package io.elite.core

import java.io.IOException
import java.util.Locale

import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer

/**
 * Created by loki on 05/02/16.
 */
class LoggingInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val t1 = System.nanoTime()
        val response = chain.proceed(request)
        val t2 = System.nanoTime()

        var contentType: MediaType? = null
        var bodyString: String? = null
        if (response.body() != null) {
            contentType = response.body()!!.contentType()
            bodyString = response.body()!!.string()
        }

        val time = (t2 - t1) / 1e6

        if (request.method() == "GET") {
            println(String.format(Locale.getDefault(), "GET $F_REQUEST_WITHOUT_BODY$F_RESPONSE_WITH_BODY", request.url(), time, request.headers(), response.code(), response.headers(), stringifyResponseBody(bodyString)))
        } else if (request.method() == "POST") {
            println(String.format(Locale.getDefault(), "POST $F_REQUEST_WITH_BODY$F_RESPONSE_WITH_BODY", request.url(), time, request.headers(), stringifyRequestBody(request), response.code(), response.headers(), stringifyResponseBody(bodyString)))
        } else if (request.method() == "PUT") {
            println(String.format(Locale.getDefault(), "PUT $F_REQUEST_WITH_BODY$F_RESPONSE_WITH_BODY", request.url(), time, request.headers(), stringifyRequestBody(request), response.code(), response.headers(), stringifyResponseBody(bodyString)))
        } else if (request.method() == "DELETE") {
            println(String.format(Locale.getDefault(), "DELETE $F_REQUEST_WITHOUT_BODY$F_RESPONSE_WITHOUT_BODY", request.url(), time, request.headers(), response.code(), response.headers()))
        }

        if (response.body() != null) {
            val body = ResponseBody.create(contentType, bodyString!!)
            return response.newBuilder().body(body).build()
        } else {
            return response
        }
    }

    fun stringifyResponseBody(responseBody: String?): String? {
        return responseBody
    }

    companion object {

        private val F_BREAK = " %n"
        private val F_URL = " %s"
        private val F_TIME = " in %.1fms"
        private val F_HEADERS = "%s"
        private val F_RESPONSE = F_BREAK + "Response: %d"
        private val F_BODY = "body: %s"

        private val F_BREAKER = "$F_BREAK-------------------------------------------$F_BREAK"
        private val F_REQUEST_WITHOUT_BODY = F_URL + F_TIME + F_BREAK + F_HEADERS
        private val F_RESPONSE_WITHOUT_BODY = F_RESPONSE + F_BREAK + F_HEADERS + F_BREAKER
        private val F_REQUEST_WITH_BODY = F_URL + F_TIME + F_BREAK + F_HEADERS + F_BODY + F_BREAK
        private val F_RESPONSE_WITH_BODY = F_RESPONSE + F_BREAK + F_HEADERS + F_BODY + F_BREAK + F_BREAKER


        private fun stringifyRequestBody(request: Request): String {
            try {
                val copy = request.newBuilder().build()
                val buffer = Buffer()
                copy.body()!!.writeTo(buffer)
                return buffer.readUtf8()
            } catch (e: IOException) {
                return "did not work"
            }

        }
    }
}
