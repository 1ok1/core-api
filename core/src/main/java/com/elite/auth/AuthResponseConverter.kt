package io.elite.auth

import java.io.IOException
import java.lang.reflect.Type

import io.elite.core.Converter
import io.elite.core.EliteJsonException
import io.elite.core.Util

class AuthResponseConverter<T>(private val resType: Type) : Converter<T, AuthException> {

    @Throws(AuthException::class)
    override fun fromResponse(response: okhttp3.Response): T? {
        val code = response.code()
        val responseBody = Util.getJsonBody(response)
        try {
            if (code == 200) {
                return Util.parseJson<T>(AuthService.gson, code, responseBody, resType)
            } else {
                val err = Util.parseJson<AuthErrorResponse>(
                    AuthService.gson,
                    code,
                    responseBody,
                    AuthErrorResponse::class.java
                )
                err!!.response = responseBody
                val errCode: AuthError
                when (code) {
                    400 -> errCode = AuthError.BAD_REQUEST
                    401 -> errCode = AuthError.UNAUTHORIZED
                    402 -> errCode = AuthError.REQUEST_FAILED
                    403 -> errCode = AuthError.INVALID_SESSION
                    500 -> errCode = AuthError.INTERNAL_ERROR
                    else -> errCode = AuthError.UNEXPECTED_CODE
                }
                throw AuthException(errCode, err.message, err.errorCode, responseBody)
            }
        } catch (e: EliteJsonException) {
            throw AuthException(AuthError.INTERNAL_ERROR, e)
        }

    }

    override fun fromIOException(e: IOException): AuthException {
        return AuthException(AuthError.CONNECTION_ERROR, e)
    }

    override fun castException(e: Exception): AuthException {
        try {
            return e as AuthException
        } catch (exp: Exception) {
            return AuthException(AuthError.REQUEST_FAILED, e)
        }

    }

    internal class AuthErrorResponse {
        var errorCode: String? = null
        var message: String? = null
        var response: String? = null
    }
}
