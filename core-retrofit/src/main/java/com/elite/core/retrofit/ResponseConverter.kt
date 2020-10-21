package com.elite.core.retrofit

import okhttp3.Response
import java.io.IOException
import java.lang.reflect.Type


object ResponseConverter {

    internal class ErrorResponse {
        var errorCode: String? = null
        var message: String? = null
        var response: String? = null
    }
}
