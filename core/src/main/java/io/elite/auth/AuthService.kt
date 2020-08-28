package io.elite.auth

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

import java.lang.reflect.Type
import java.net.CookieManager
import java.net.CookiePolicy

import io.elite.core.Call
import io.elite.core.Elite
import io.elite.core.LoginCall
import io.elite.core.LogoutCall
import io.elite.core.RegisterCall
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType


class AuthService {
    var client: OkHttpClient? = null
        private set
    var url: String? = null
        private set

    /**
     * Returns credentials of the logged in user
     *
     *
     * This method can be used to retrieve Elite credentials for the current logged in user.
     * Elite credentials include "Elite Id", "Hausura Role" and "Session Id". This method can
     * also be used to check if the user has an existing session (or logged in basically). If
     * not logged in, it will throw an [AuthException].
     *
     *
     * @return [GetCredentialsResponse]
     * @throws AuthException
     */
    val credentials: Call<GetCredentialsResponse, AuthException>
        get() {
            val respType = object : TypeToken<GetCredentialsResponse>() {

            }.type
            return mkGetCall("/user/account/info", respType)
        }

    constructor(authUrl: String, httpClient: OkHttpClient) {
        this.url = authUrl
        this.client = httpClient
    }

    constructor(authUrl: String) {
        this.url = authUrl
        val cookieManager = CookieManager()
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        this.client = OkHttpClient.Builder()
                .cookieJar(JavaNetCookieJar(cookieManager))
                .build()
    }

    private fun <T> mkPostCall(url: String, jsonBody: String, bodyType: Type): Call<T, AuthException> {


        val reqBody = jsonBody.toRequestBody(JSON)
        Elite.instance.requestType = true
        val request = Request.Builder()
                .url(this.url!! + url)
                .post(reqBody)
                .build()
        return Call(
                client!!.newCall(request), AuthResponseConverter(bodyType))
    }

    private fun <T> mkRegisterCall(url: String, jsonBody: String, bodyType: Type): RegisterCall<T, AuthException> {
        val reqBody = jsonBody.toRequestBody(JSON)
        Elite.instance.requestType = false
        val request = Request.Builder()
                .url(this.url!! + url)
                .post(reqBody)
                .build()
        return RegisterCall(
                client!!.newCall(request), AuthResponseConverter(bodyType))
    }

    private fun <T> mkLoginCall(url: String, jsonBody: String, bodyType: Type): LoginCall<T, AuthException> {
        val reqBody = jsonBody.toRequestBody(JSON)
        Elite.instance.requestType = false
        val request = Request.Builder()
                .url(this.url!! + url)
                .post(reqBody)
                .build()
        return LoginCall(
                client!!.newCall(request), AuthResponseConverter(bodyType))
    }

    private fun <T> mkLogoutCall(url: String, bodyType: Type): LogoutCall<T, AuthException> {
        val request = Request.Builder()
                .url(this.url!! + url)
                .build()
        Elite.instance.requestType = true
        return LogoutCall(
                client!!.newCall(request), AuthResponseConverter(bodyType))
    }

    private fun <T> mkGetCall(url: String, bodyType: Type): Call<T, AuthException> {
        val request = Request.Builder()
                .url(this.url!! + url)
                .build()
        Elite.instance.requestType = true
        return Call(
                client!!.newCall(request), AuthResponseConverter(bodyType))
    }

    /**
     * Signup or register a new user
     *
     * @param r a [RegisterRequest] type
     * @return  the [RegisterResponse]
     * @throws AuthException
     */
    fun register(r: RegisterRequest): RegisterCall<RegisterResponse, AuthException> {
        val jsonBody = gson.toJson(r)
        val respType = object : TypeToken<RegisterResponse>() {

        }.type
        return mkRegisterCall("/signup", jsonBody, respType)
    }

    /**
     * OTP Signup or register a new user
     *
     * @param r a [RegisterRequest] type
     * @return  the [RegisterResponse]
     * @throws AuthException
     */
    fun otpSignUp(r: RegisterRequest): RegisterCall<RegisterResponse, AuthException> {
        val jsonBody = gson.toJson(r)
        val respType = object : TypeToken<RegisterResponse>() {

        }.type
        return mkRegisterCall("/otp-signup", jsonBody, respType)
    }

    /**
     * Login an existing user
     *
     * Login an existing user by creating a [LoginRequest] class
     *
     * @param r [LoginRequest] type
     * @return  the [LoginResponse]
     * @throws AuthException
     */
    fun login(r: LoginRequest): LoginCall<LoginResponse, AuthException> {
        val jsonBody = gson.toJson(r)
        val respType = object : TypeToken<LoginResponse>() {

        }.type
        return mkLoginCall("/login", jsonBody, respType)
    }

    /**
     * OTP Login an existing user
     *
     * Login an existing user by creating a [LoginRequestOtp] class
     *
     * @param r [LoginRequestOtp] type
     * @return  the [LoginResponse]
     * @throws AuthException
     */
    fun otpLogin(r: LoginRequestOtp): LoginCall<LoginResponse, AuthException> {
        val jsonBody = gson.toJson(r)
        val respType = object : TypeToken<LoginResponse>() {

        }.type
        return mkLoginCall("/otp-login", jsonBody, respType)
    }

    /**
     * Login an existing user
     *
     * Login an existing user by passing username and password. This is a shortcut for the above
     * method when only username and password is used for login.
     *
     * @param userName the user name of the user
     * @param password password of the user (unencrypted)
     * @return the [LoginResponse]
     * @throws AuthException
     */
    fun login(
            userName: String, password: String): LoginCall<LoginResponse, AuthException> {
        return this.login(LoginRequest(userName, password))
    }

    /**
     * Logout a logged-in user.
     *
     * @return a [LogoutResponse] type
     * @throws AuthException
     */
    fun logout(): LogoutCall<LogoutResponse, AuthException> {
        val respType = object : TypeToken<LogoutResponse>() {

        }.type
        return mkLogoutCall("/user/logout", respType)
    }

    /**
     * Confirm the email of an user - given an existing token.
     *
     *
     * Once the user retrieves the token that is sent to the user's email, this method can be
     * used to confirm the email of the user with Elite Auth.
     *
     *
     * @param r [ConfirmEmailRequest]
     * @return  [ConfirmEmailResponse]
     * @throws AuthException
     */
    fun confirmEmail(r: ConfirmEmailRequest): Call<ConfirmEmailResponse, AuthException> {
        val token = r.token
        val respType = object : TypeToken<ConfirmEmailResponse>() {

        }.type
        return mkGetCall("/email/confirm?token=$token", respType)
    }

    /**
     * Resend the verification email of an user.
     *
     *
     * Initialize the [ResendEmailRequest] class with the email of the user, and pass the
     * object to this method.
     *
     *
     * @param r [ResendEmailRequest]
     * @return  [ResendEmailResponse]
     * @throws AuthException
     */
    fun resendVerifyEmail(r: ResendEmailRequest): Call<ResendEmailResponse, AuthException> {
        val jsonBody = gson.toJson(r)
        val respType = object : TypeToken<ResendEmailResponse>() {

        }.type
        return mkPostCall("/email/resend-verify", jsonBody, respType)
    }

    /**
     * Change user's email address.
     *
     *
     * Initialize [ChangeEmailRequest] with the new email address of the user. This method
     * will send a verification email to the new email address of the user.
     *
     *
     * @param r [ChangeEmailRequest]
     * @return  [ChangeEmailResponse]
     * @throws AuthException
     */
    fun changeEmail(r: ChangeEmailRequest): Call<ChangeEmailResponse, AuthException> {
        val jsonBody = gson.toJson(r)
        val respType = object : TypeToken<ChangeEmailResponse>() {

        }.type
        return mkPostCall("/user/email/change", jsonBody, respType)
    }

    /**
     * Change user's password
     *
     *
     * This method takes a [ChangePasswordRequest] object, which should contain the
     * current password and the new password.
     *
     *
     * @param r [ChangePasswordRequest]
     * @return  [ChangePasswordResponse]
     * @throws AuthException
     */
    fun changePassword(r: ChangePasswordRequest): Call<ChangePasswordResponse, AuthException> {
        val jsonBody = gson.toJson(r)
        val respType = object : TypeToken<ChangePasswordResponse>() {

        }.type
        return mkPostCall("/user/password/change", jsonBody, respType)
    }

    /**
     * Send an email to the user, containing the forgot password link.
     *
     * @param r [ForgotPasswordRequest]
     * @return  [ForgotPasswordResponse]
     * @throws AuthException
     */
    fun forgotPassword(r: ForgotPasswordRequest): Call<ForgotPasswordResponse, AuthException> {
        val jsonBody = gson.toJson(r)
        val respType = object : TypeToken<ForgotPasswordResponse>() {

        }.type
        return mkPostCall("/password/forgot", jsonBody, respType)
    }

    /**
     * Reset the password of the user, given the password reset token and the new password.
     *
     *
     * Initialize the [ResetPasswordRequest] object with the password reset token (which
     * the user retrieves from the forgot password email, and the new password of the user.
     *
     *
     * @param r [ResetPasswordRequest]
     * @return  [ResetPasswordResponse]
     * @throws AuthException
     */
    fun resetPassword(r: ResetPasswordRequest): Call<ResetPasswordResponse, AuthException> {
        val jsonBody = gson.toJson(r)
        val respType = object : TypeToken<ResetPasswordResponse>() {

        }.type
        return mkPostCall("/password/reset", jsonBody, respType)
    }

    /**
     * Change user's username.
     *
     * @param r [ChangeUserNameRequest]
     * @return  [ChangeUserNameResponse]
     * @throws AuthException
     */
    fun changeUserName(r: ChangeUserNameRequest): Call<ChangeUserNameResponse, AuthException> {
        val jsonBody = gson.toJson(r)
        val respType = object : TypeToken<ChangeUserNameResponse>() {

        }.type
        return mkPostCall("/user/account/change-username", jsonBody, respType)
    }

    /**
     *
     * @param r
     * @return
     */
    fun checkPassword(r: CheckPasswordRequest): Call<CheckPasswordResponse, AuthException> {
        val jsonBody = gson.toJson(r)
        val respType = object : TypeToken<CheckPasswordResponse>() {

        }.type
        return mkPostCall("/user/password/verify", jsonBody, respType)
    }

    /**
     * Confirm the mobile number of the user, by passing the OTP and the mobile number of the user.
     *
     * @param r [ConfirmMobileRequest]
     * @return  [ConfirmMobileResponse]
     * @throws AuthException
     */
    fun confirmMobile(r: ConfirmMobileRequest): Call<ConfirmMobileResponse, AuthException> {
        val jsonBody = gson.toJson(r)
        val respType = object : TypeToken<ConfirmMobileResponse>() {

        }.type
        return mkPostCall("/mobile/confirm", jsonBody, respType)
    }

    /**
     * Change user's mobile number. This method will send an OTP to the new number of the user.
     *
     * @param r [ChangeMobileRequest]
     * @return  [ChangeMobileResponse]
     * @throws AuthException
     */
    fun changeMobile(r: ChangeMobileRequest): Call<ChangeMobileResponse, AuthException> {
        val jsonBody = gson.toJson(r)
        val respType = object : TypeToken<ChangeMobileResponse>() {

        }.type
        return mkPostCall("/user/mobile/change", jsonBody, respType)
    }

    /**
     * Resend the OTP to a user's mobile number.
     *
     * @param r [ResendOTPRequest]
     * @return  [ResendOTPResponse]
     * @throws AuthException
     */
    fun resendOTP(r: ResendOTPRequest): Call<ResendOTPResponse, AuthException> {
        val jsonBody = gson.toJson(r)
        val respType = object : TypeToken<ResendOTPResponse>() {

        }.type
        return mkPostCall("/mobile/resend-otp", jsonBody, respType)
    }

    /**
     * Resend the OTP to a user's mobile number.
     *
     * @param r [ResendOTPRequest]
     * @return  [ResendOTPResponse]
     * @throws AuthException
     */
    fun createOTP(r: ResendOTPRequest): Call<ResendOTPResponse, AuthException> {
        val jsonBody = gson.toJson(r)
        val respType = object : TypeToken<ResendOTPResponse>() {

        }.type
        return mkPostCall("/mobile/create-otp", jsonBody, respType)
    }

    /**
     * Delete account of the current user
     *
     * @param r [DeleteAccountRequest]
     * @return  [DeleteAccountResponse]
     * @throws AuthException
     */
    fun deleteAccount(r: DeleteAccountRequest): Call<DeleteAccountResponse, AuthException> {
        val jsonBody = gson.toJson(r)
        val respType = object : TypeToken<DeleteAccountResponse>() {

        }.type
        return mkPostCall("/user/account/delete", jsonBody, respType)
    }

    companion object {

        val JSON = "application/json; charset=utf-8".toMediaType()
        val gson = GsonBuilder().create()
    }


}
