package com.elite.core.retrofit

import java.io.IOException

interface Converter<T, E : Exception> {
//    @Throws(E::class)
    fun fromResponse(r: okhttp3.Response): T?

    fun fromIOException(e: IOException): E

    fun castException(e: Exception): E
}
