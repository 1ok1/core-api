package io.elite.core

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException

import java.lang.reflect.Type

object Util {
    @Throws(EliteJsonException::class)
    fun <R> parseJson(
        gson: Gson, code: Int,
        response: String, bodyType: Type
    ): R? {
        try {
            return gson.fromJson<R>(response, bodyType)
        } catch (e: JsonSyntaxException) {
//            val msg = "FATAL : JSON structure not as expected. Schema changed maybe? : " + e.message
            val msg = "Something went wrong please try after sometime"
            throw EliteJsonException(code, msg, e)
        } catch (e: JsonParseException) {
//            val msg = "FATAL : Server didn't return valid JSON : " + e.message
            val msg = "Something went wrong please try after sometime"
            throw EliteJsonException(code, msg, e)
        }
    }

    fun getJsonBody(response: okhttp3.Response): String {
        try {
            return response.body()!!.string()
        } catch (e: Exception) {
            return ""
        }

    }
}
