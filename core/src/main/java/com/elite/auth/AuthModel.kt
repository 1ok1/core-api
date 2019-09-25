package io.elite.auth

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ChangeEmailRequest(
    var email: String,
    var info: JsonObject? = null
) : Serializable


data class ForgotPasswordRequest(
    var email: String,
    var info: JsonObject? = null
) : Serializable

data class ResendEmailRequest(
    var email: String,
    var info: JsonObject? = null
) : Serializable

data class ChangeMobileRequest(
    var mobile: String,
    var info: JsonObject? = null
) : Serializable

data class ResendOTPRequest(
    var mobile: String,
    var info: JsonObject? = null
) : Serializable

data class ChangeEmailResponse(
    var message: String
) : Serializable

data class ResendOTPResponse(
    var message: String
) : Serializable

data class ResendEmailResponse(
    var message: String
) : Serializable

data class ForgotPasswordResponse(
    var message: String
) : Serializable

data class ChangeMobileResponse(
    var message: String
) : Serializable

data class ChangeUserNameResponse(
    var message: String
) : Serializable

data class CheckPasswordResponse(
    var message: String
) : Serializable

data class LogoutResponse(
    var message: String,
    var info: JsonObject
) : Serializable

data class ConfirmMobileResponse(
    var message: String
) : Serializable

data class DeleteAccountResponse(
    var message: String
) : Serializable

data class ChangePasswordRequest(
    var password: String,
    var new_password: String,
    var info: JsonObject? = null
) : Serializable

data class LoginRequest(
    var username: String,
    var password: String,
    var email: String? = null,
    var mobile: String? = null,
    var info: JsonObject? = null
) : Serializable

data class RegisterRequest(
    var mobile: String,
    var email: String,
    var username: String,
    var password: String? = null,
    var info: JsonObject? = null
) : Serializable

data class CheckPasswordRequest(
    var password: String,
    var info: JsonObject? = null
) : Serializable

data class ResetPasswordRequest(
    var token: String,
    var password: String,
    var info: JsonObject? = null
) : Serializable

data class DeleteAccountRequest(
    var password: String,
    var info: JsonObject? = null
) : Serializable

data class ChangePasswordResponse(
    var message: String,
    var auth_token: String
) : Serializable

data class ChangeUserNameRequest(
    var username: String,
    var info: JsonObject? = null
) : Serializable

data class ConfirmEmailRequest(
    var token: String,
    var info: JsonObject? = null
) : Serializable

data class ConfirmEmailResponse(
    var hasura_id: Int,
    var user_email: String,
    var message: String
) : Serializable

data class GetCredentialsResponse(
    var hasura_id: Int,
    var hasura_roles: ArrayList<String>,
    var auth_token: String,
    var mobile: String,
    var email: String,
    var username: String,
    var info: JsonObject
) : Serializable

data class LoginResponse(
    var hasura_id: Int,
    var hasura_roles: ArrayList<String>,
    var auth_token: String,
    var info: JsonObject
) : Serializable

data class RegisterResponse(
    var hasura_id: Int,
    var hasura_roles: ArrayList<String>,
    var auth_token: String,
    var info: JsonObject
) : Serializable

data class ConfirmMobileRequest(
    var mobile: String,
    var otp: Int,
    var info: JsonObject? = null
) : Serializable

data class LoginRequestOtp(
    var mobile: String,
    var otp: String,
    var info: JsonObject? = null
) : Serializable


data class ResetPasswordResponse(
    var message: String
) : Serializable