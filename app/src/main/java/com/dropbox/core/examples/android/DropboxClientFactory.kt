package com.dropbox.core.examples.android

import android.content.Context
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.http.OkHttp3Requestor
import com.dropbox.core.v2.DbxClientV2

/**
 * Singleton instance of [DbxClientV2] and friends
 */
class DropboxClientFactory(private val accessToken: String) {
    private val requestConfig = DbxRequestConfig.newBuilder("examples-v2-demo")
            .withHttpRequestor(OkHttp3Requestor(OkHttp3Requestor.defaultOkHttpClient()))
            .build()

    val client: DbxClientV2 by lazy {
        DbxClientV2(requestConfig, accessToken)
    }

    companion object {
        fun hasToken(context: Context): Boolean = context
                .getSharedPreferences("dropbox-sample", Context.MODE_PRIVATE)
                .getString("access-token", null) != null
    }

}
