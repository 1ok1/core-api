package io.elite.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.elite.auth.AuthException
import io.elite.core.Call
import io.elite.core.Callback

/**
 * Created by hipbar on 9/8/17.
 */

open class ApiBaseLiveData<T> : LiveData<T>() {
    private lateinit var apiCall: Call<*, *>

    protected fun <T> makeApiCall(
        apiCall: Call<T, AuthException>,
        data: MutableLiveData<Response<T>>
    ): LiveData<Response<T>> {
        data.value = Response.loading()
        apiCall.enqueueOnUIThread({
            data.value = Response.loading()
        }, {
            data.value = Response.success(it)
        }, {
            data.value = Response.failure(it)
        }, {
            data.value = Response.noInternet()
        })
        return data
    }
}
