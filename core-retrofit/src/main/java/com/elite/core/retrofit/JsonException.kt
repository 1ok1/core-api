package com.elite.core.retrofit

import java.lang.Exception

class JsonException : Exception {
    /**
     * Access the code for this error.
     *
     * @return The numerical code for this error.
     */
    var code: Int = 0
        private set

    /**
     * Construct a new EliteJsonException with a particular error code.
     *
     * @param theCode    The error code to identify the type of exception.
     * @param theMessage A message describing the error in more detail.
     */
    constructor(theCode: Int, theMessage: String) : super(theMessage) {
        code = theCode
    }

    /**
     * Construct a new EliteJsonException with an external cause.
     *
     * @param message A message describing the error in more detail.
     * @param cause   The cause of the error.
     */
    constructor(theCode: Int, message: String, cause: Throwable) : super(message, cause) {
        code = theCode
    }

    /**
     * Construct a new EliteJsonException with an external cause.
     *
     * @param cause The cause of the error.
     */
    constructor(cause: Throwable) : super(cause) {
        code = 0
    }

    companion object {
        private val serialVersionUID: Long = 1
    }
}
