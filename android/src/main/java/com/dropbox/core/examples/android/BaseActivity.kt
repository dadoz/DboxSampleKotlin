package com.dropbox.core.examples.android

import android.content.Context
import android.support.v7.app.AppCompatActivity
import com.dropbox.core.android.Auth


/**
 * Base class for Activities that require auth tokens
 * Will redirect to auth flow if needed
 */
abstract class BaseActivity : AppCompatActivity() {
    private val prefs by lazy {
        getSharedPreferences("dropbox-sample", Context.MODE_PRIVATE)
    }

    private val storedUid by lazy {
        prefs.getString("user-id", null)
    }

    protected val dbxClient by lazy {
        DropboxClientFactory(accessToken).client
    }

    protected val picassoClient by lazy {
        PicassoClient(applicationContext, dbxClient).client
    }

    private lateinit var accessToken: String
    private val uid = Auth.getUid()

    override fun onResume() {
        super.onResume()
        onResumeHandler()
    }

    /**
     */
    private fun onResumeHandler() {
        accessToken.apply {
            prefs.getString("access-token", null)?: Auth.getOAuth2Token()
        }

        accessToken.let {
            prefs.edit().putString("access-token", accessToken).apply()
            loadData()
        }

        uid?.let {
            if (uid != storedUid)
                prefs.edit().putString("user-id", uid).apply()
        }
    }


    /**
     */
    protected abstract fun loadData()
}
