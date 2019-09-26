package io.elite.core

import android.content.Context
import android.content.SharedPreferences

import java.util.concurrent.TimeUnit

import io.elite.auth.AuthService
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import java.net.CookieManager
import java.net.CookiePolicy

/**
 * Created by loki on 18/08/16.
 */

class Elite {

    private val eliteSharedPref = "io.elite.shared.pref"
    private val eliteSharedPrefUserId = "io.elite.shared.pref.userId"
    private val eliteSharedPrefUserRole = "io.elite.shared.pref.eliteSharedPrefUserRole"
    private val eliteSharedPrefUserToken = "io.elite.shared.pref.eliteSharedPrefUserToken"
    private val eliteSharedPrefRequestType = "io.elite.shared.pref.eliteSharedPrefRequestType"
    private val eliteSharedPrefLoginCheck = "io.elite.shared.pref.eliteSharedPrefLoginCheck"
    private val eliteSharedPrefauthIdentifier =
        "io.elite.shared.pref.eliteSharedPrefauthIdentifier"

    companion object {
        val instance: Elite = Elite()
    }


    private lateinit var okHttpClient: OkHttpClient
    private lateinit var okHttpBuilder: OkHttpClient.Builder
    private lateinit var cookiePrefs: SharedPreferences
    private lateinit var mEnvironment: Environment

    private var auth: AuthService? = null
    private lateinit var context: Context
    private lateinit var sAuthUrl: String
    private var timeOut: Long = 15
    var appName = ""
    var authIdentifier = ""
    var userRole = ""

    /*var authIdentifier: String?
        get() = cookiePrefs.getString(eliteSharedPrefauthIdentifier, "")
        set(preData) {
            val prefsWriter = cookiePrefs.edit()
            prefsWriter.putString(eliteSharedPrefauthIdentifier, preData)
            prefsWriter.apply()
        }*/

    var userId: Int?
        get() = cookiePrefs.getInt(eliteSharedPrefUserId, -1)
        set(userId) {
            val prefsWriter = cookiePrefs.edit()
            prefsWriter.putInt(eliteSharedPrefUserId, userId!!)
            prefsWriter.apply()
        }

    val isLoggedIn: Boolean
        get() = cookiePrefs.getBoolean(eliteSharedPrefLoginCheck, false)

    var requestType: Boolean
        get() = cookiePrefs.getBoolean(eliteSharedPrefRequestType, false)
        set(requestType) {
            val prefsWriter = cookiePrefs.edit()
            prefsWriter.putBoolean(eliteSharedPrefRequestType, requestType)
            prefsWriter.apply()
        }

    var userToken: String?
        get() = cookiePrefs.getString(eliteSharedPrefUserToken, "")
        set(userToken) {
            val prefsWriter = cookiePrefs.edit()
            prefsWriter.putString(eliteSharedPrefUserToken, userToken)
            prefsWriter.apply()
        }

    /*var userRole: String?
        get() = cookiePrefs.getString(eliteSharedPrefUserRole, "")
        set(userRole) {
            val prefsWriter = cookiePrefs.edit()
            prefsWriter.putString(eliteSharedPrefUserRole, userRole)
            prefsWriter.apply()
        }*/

    fun setLogin() {
        val prefsWriter = cookiePrefs.edit()
        prefsWriter.putBoolean(eliteSharedPrefLoginCheck, true)
        prefsWriter.apply()
    }

    fun clearLogin() {
        val prefsWriter = cookiePrefs.edit()
        prefsWriter.putBoolean(eliteSharedPrefLoginCheck, false)
        prefsWriter.apply()
    }

    fun buildOkHttpClientBuilder(): OkHttpClient.Builder {
        val okHttpClientBuilder = OkHttpClient.Builder()
        okHttpClientBuilder.addInterceptor(EliteTokenInterceptor())
        if (Environment.DEV == mEnvironment) {
            okHttpClientBuilder.addInterceptor(LoggingInterceptor())
        } else if (Environment.PROD == mEnvironment) {

        }
        return okHttpClientBuilder
    }

    fun clearSession() {
        userToken = ""
        clearLogin()
//        PersistentCookieStore(context!!).removeAll()
    }

    fun removeAuthService() {
        auth = null
    }

    fun getAuth(): AuthService {
        if (auth == null) {
            auth = AuthService(sAuthUrl, okHttpClient)
        }
        return auth as AuthService
    }

    fun getAuthAsRole(userRole: String): AuthService {
        okHttpClient = okHttpBuilder.addInterceptor(EliteTokenInterceptor())
            .build()
        auth = AuthService(sAuthUrl, okHttpClient)
        return auth as AuthService
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
        fun build(): Elite
    }


    private class Builder(context: Context) : IEnvironment, IUserRole, IAuthUrl,
        IAuthIdentifier, IAppName, ItimeOut, IBuild {

        init {
            instance.context = context
        }

        fun context(context: Context): Builder {
            instance.context = context
            return this
        }

        override fun environment(environment: Environment): IAuthUrl {
            instance.mEnvironment = environment
            return this
        }

        override fun authUrl(authUrl: String): IAuthIdentifier {
            instance.sAuthUrl = authUrl
            return this
        }

        override fun authIdentifier(authIdentifier: String): IUserRole {
            instance.authIdentifier = authIdentifier
            return this
        }

        override fun useRole(userRole: String): IAppName {
            instance.userRole = userRole
            return this
        }

        override fun appName(appName: String): ItimeOut {
            instance.appName = appName
            return this
        }

        override fun timeOut(timeOut: Long): IBuild {
            instance.timeOut = timeOut
            return this
        }

        override fun build(): Elite {
            instance.okHttpBuilder = instance.buildOkHttpClientBuilder()

            val cookieManager = CookieManager()
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)

            instance.okHttpClient = instance.okHttpBuilder
                .connectTimeout(instance.timeOut, TimeUnit.SECONDS)
                .writeTimeout(instance.timeOut, TimeUnit.SECONDS)
                .readTimeout(instance.timeOut, TimeUnit.SECONDS)
                .cookieJar(JavaNetCookieJar(cookieManager))
                .build()
            instance.cookiePrefs =
                instance.context.getSharedPreferences(
                    instance.eliteSharedPref,
                    Context.MODE_PRIVATE
                )
            instance.auth = instance.getAuth()
            return instance
        }

    }

}
