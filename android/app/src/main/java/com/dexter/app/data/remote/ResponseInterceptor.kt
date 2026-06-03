package com.dexter.app.data.remote

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException
import javax.inject.Inject

class ResponseInterceptor @Inject constructor() : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        val responseBody = originalResponse.body

        if (responseBody != null) {
            val responseBodyString = responseBody.string()
            Log.d("ResponseInterceptor", "Raw Response: $responseBodyString")

            return originalResponse.newBuilder()
                .body(responseBodyString.toResponseBody(responseBody.contentType()))
                .build()
        }

        return originalResponse
    }
}
