package com.elite.core.retrofit

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import retrofit2.Response

import java.lang.reflect.Type

object Util {
    @Throws(JsonException::class)
    fun <R> parseJson(
        gson: Gson, code: Int,
        response: String, bodyType: Type
    ): R? {
        try {
            return gson.fromJson<R>(response, bodyType)
        } catch (e: JsonSyntaxException) {
//            val msg = "FATAL : JSON structure not as expected. Schema changed maybe? : " + e.message
            val msg = "Something went wrong please try after sometime"
            throw JsonException(code, msg, e)
        } catch (e: JsonParseException) {
//            val msg = "FATAL : Server didn't return valid JSON : " + e.message
            val msg = "Something went wrong please try after sometime"
            throw JsonException(code, msg, e)
        }
    }

    fun <T> getJsonBody(response: Response<T>): String {
        return try {
            response.body()!!.toString()
        } catch (e: Exception) {
            ""
        }

    }
}
