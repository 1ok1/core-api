package com.elite.core.retrofit

import java.io.IOException

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by loki on 18/08/16.
 */

class TokenInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response: Response
        val builder = request.newBuilder()
        builder.addHeader("App-Name", Service.INSTANCE.appName)
        if (Service.INSTANCE.requestType) {
            if (Service.INSTANCE.userToken != "") {
                builder.addHeader(
                    "Authorization", Service.INSTANCE.authIdentifier + Service.INSTANCE.userToken!!
                )
            }
            if (Service.INSTANCE.userRole != "") {
                builder.addHeader(
                    "x-hasura-role", Service.INSTANCE.userRole
                )
            }
            if (Service.INSTANCE.sessionId != "") {
                builder.addHeader(
                    "x-session-id", Service.INSTANCE.sessionId!!
                )
            }
            if (Service.INSTANCE.version != "") {
                builder.addHeader(
                    "version", Service.INSTANCE.version!!
                )
            }
            builder.addHeader(
                "platform", "Android"
            )
        }

        val newRequest = builder.build()
        response = chain.proceed(newRequest)
        return response
    }
}
