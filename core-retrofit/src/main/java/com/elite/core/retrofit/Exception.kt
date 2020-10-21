package com.elite.core.retrofit

open class Exception : java.lang.Exception {
    /**
     * Access the code for this error.
     *
     * @return The code for this error.
     */
    var code: Error
    /**
     * Access the messsage for this error.
     *
     * @return The error-code for this error.
     */
    var messsage: String? = null
    /**
     * Access the error-code for this error.
     *
     * @return The error-code for this error.
     */
    var errorCode: String? = null
    /**
     * Access the error-code for this error.
     *
     * @return The error-code for this error.
     */
    var errorResponse: String? = null

    /**
     * Construct a new AuthException with a particular error code.
     *
     * @param theCode          The error code to identify the type of exception.
     * @param theMessage       A message describing the error in more detail.
     * @param theErrorCode     A code for checking auth error.
     * @param theErrorResponse
     */
    constructor(
        theCode: Error,
        theMessage: String?,
        theErrorCode: String?,
        theErrorResponse: String
    ) : super(theMessage) {
        code = theCode
        messsage = theMessage
        errorCode = theErrorCode
        errorResponse = theErrorResponse
    }

    /**
     * Construct a new AuthException with a particular error code.
     *
     * @param theCode The error code to identify the type of exception.
     * @param cause   The cause of the error.
     */
    constructor(theCode: Error, cause: Throwable) : super(cause) {
        code = theCode
    }


    override fun toString(): String {
        return (Exception::class.java.getName() + " "
                + code.toString() + " : " + super.getLocalizedMessage())
    }

    companion object {
        private val serialVersionUID: Long = 1
    }
}
