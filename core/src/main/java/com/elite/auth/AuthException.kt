package io.elite.auth

class AuthException : Exception {
    /**
     * Access the code for this error.
     *
     * @return The code for this error.
     */
    var code: AuthError
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
        theCode: AuthError,
        theMessage: String,
        theErrorCode: String,
        theErrorResponse: String
    ) : super(theMessage) {
        code = theCode
        errorCode = theErrorCode
        errorResponse = theErrorResponse
    }

    /**
     * Construct a new AuthException with a particular error code.
     *
     * @param theCode The error code to identify the type of exception.
     * @param cause   The cause of the error.
     */
    constructor(theCode: AuthError, cause: Throwable) : super(cause) {
        code = theCode
    }


    override fun toString(): String {
        return (AuthException::class.java.getName() + " "
                + code.toString() + " : " + super.getLocalizedMessage())
    }

    companion object {
        private val serialVersionUID: Long = 1
    }
}
