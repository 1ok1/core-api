package com.elite.core.retrofit

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.GsonBuilder

import java.util.concurrent.TimeUnit

import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import java.net.CookieManager
import java.net.CookiePolicy

/**
 * Created by loki on 18/08/16.
 */

class Service {

    private val serviceSharedPref = "io.elite.shared.pref"
    private val serviceSharedPrefUserId = "io.elite.shared.pref.userId"
    private val serviceSharedPrefUserToken = "io.elite.shared.pref.serviceSharedPrefUserToken"
    private val serviceSharedPrefSessionId = "io.elite.shared.pref.serviceSharedPrefSessionId"
    private val serviceSharedPrefVersion = "io.elite.shared.pref.serviceSharedPrefVersion"
    private val serviceSharedPrefRequestType = "io.elite.shared.pref.eliteSharedPrefRequestType"
    private val serviceSharedPrefLoginCheck = "io.elite.shared.pref.eliteSharedPrefLoginCheck"

    companion object {
        val INSTANCE: Service = Service()
        val gson = GsonBuilder().create()
    }


    private lateinit var okHttpClient: OkHttpClient
    private lateinit var okHttpBuilder: OkHttpClient.Builder
    private lateinit var cookiePrefs: SharedPreferences
    private lateinit var mEnvironment: Environment

    private lateinit var context: Context
    private lateinit var sAuthUrl: String
    private var timeOut: Long = 15
    var appName = ""
    var authIdentifier = ""
    var userRole = ""



    var userId: Int?
        get() = cookiePrefs.getInt(serviceSharedPrefUserId, -1)
        set(userId) {
            val prefsWriter = cookiePrefs.edit()
            prefsWriter.putInt(serviceSharedPrefUserId, userId!!)
            prefsWriter.apply()
        }

    val isLoggedIn: Boolean
        get() = cookiePrefs.getBoolean(serviceSharedPrefLoginCheck, false)

    var requestType: Boolean
        get() = cookiePrefs.getBoolean(serviceSharedPrefRequestType, false)
        set(requestType) {
            val prefsWriter = cookiePrefs.edit()
            prefsWriter.putBoolean(serviceSharedPrefRequestType, requestType)
            prefsWriter.apply()
        }

    var userToken: String?
        get() = cookiePrefs.getString(serviceSharedPrefUserToken, "")
        set(userToken) {
            val prefsWriter = cookiePrefs.edit()
            prefsWriter.putString(serviceSharedPrefUserToken, userToken)
            prefsWriter.apply()
        }

    var sessionId: String?
        get() = cookiePrefs.getString(serviceSharedPrefSessionId, "")
        set(userToken) {
            val prefsWriter = cookiePrefs.edit()
            prefsWriter.putString(serviceSharedPrefSessionId, userToken)
            prefsWriter.apply()
        }

    var version: String?
        get() = cookiePrefs.getString(serviceSharedPrefVersion, "")
        set(userToken) {
            val prefsWriter = cookiePrefs.edit()
            prefsWriter.putString(serviceSharedPrefVersion, userToken)
            prefsWriter.apply()
        }

    fun setLogin() {
        val prefsWriter = cookiePrefs.edit()
        prefsWriter.putBoolean(serviceSharedPrefLoginCheck, true)
        prefsWriter.apply()
    }

    fun clearLogin() {
        val prefsWriter = cookiePrefs.edit()
        prefsWriter.putBoolean(serviceSharedPrefLoginCheck, false)
        prefsWriter.apply()
    }

    fun buildOkHttpClientBuilder(): OkHttpClient.Builder {
        val okHttpClientBuilder = OkHttpClient.Builder()
        okHttpClientBuilder.addInterceptor(TokenInterceptor())
        if (Environment.DEV == mEnvironment) {
            okHttpClientBuilder.addInterceptor(LoggingInterceptor())
        } else if (Environment.PROD == mEnvironment) {

        }
        return okHttpClientBuilder
    }

    fun clearSession() {
        userToken = ""
        clearLogin()
    }

    fun context(context: Context): IEnvironment {
        return Builder(context)
    }

    interface IEnvironment {
        fun environment(environment: Environment): IAuthUrl
    }

    interface IAuthUrl {
        fun authUrl(authUrl: String): IAuthIdentifier
    }

    interface IAuthIdentifier {
        fun authIdentifier(authIdentifier: String): IUserRole
    }

    interface IUserRole {
        fun useRole(dbUrl: String): IAppName
    }

    interface IAppName {
        fun appName(appName: String): ItimeOut
    }

    interface ItimeOut {
        fun timeOut(timeOut: Long): IBuild
    }

    interface IBuild {
        fun build(): Service
    }


    private class Builder(context: Context) : IEnvironment, IUserRole, IAuthUrl,
        IAuthIdentifier, IAppName, ItimeOut, IBuild {

        init {
            INSTANCE.context = context
        }

        fun context(context: Context): Builder {
            INSTANCE.context = context
            return this
        }

        override fun environment(environment: Environment): IAuthUrl {
            INSTANCE.mEnvironment = environment
            return this
        }

        override fun authUrl(authUrl: String): IAuthIdentifier {
            INSTANCE.sAuthUrl = authUrl
            return this
        }

        override fun authIdentifier(authIdentifier: String): IUserRole {
            INSTANCE.authIdentifier = authIdentifier
            return this
        }

        override fun useRole(userRole: String): IAppName {
            INSTANCE.userRole = userRole
            return this
        }

        override fun appName(appName: String): ItimeOut {
            INSTANCE.appName = appName
            return this
        }

        override fun timeOut(timeOut: Long): IBuild {
            INSTANCE.timeOut = timeOut
            return this
        }

        override fun build(): Service {
            INSTANCE.okHttpBuilder = INSTANCE.buildOkHttpClientBuilder()

            val cookieManager = CookieManager()
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)

            INSTANCE.okHttpClient = INSTANCE.okHttpBuilder
                .connectTimeout(INSTANCE.timeOut, TimeUnit.SECONDS)
                .writeTimeout(INSTANCE.timeOut, TimeUnit.SECONDS)
                .readTimeout(INSTANCE.timeOut, TimeUnit.SECONDS)
                .cookieJar(JavaNetCookieJar(cookieManager))
                .build()
            INSTANCE.cookiePrefs =
                INSTANCE.context.getSharedPreferences(
                    INSTANCE.serviceSharedPref,
                    Context.MODE_PRIVATE
                )
            return INSTANCE
        }

    }

}
