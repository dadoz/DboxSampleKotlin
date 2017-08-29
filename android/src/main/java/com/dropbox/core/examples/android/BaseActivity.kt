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

    private val accessToken: String by lazy {
        prefs.getString("access-token", null)?: Auth.getOAuth2Token()
    }
    private val uid = Auth.getUid()

    override fun onResume() {
        super.onResume()
        onResumeHandler()
    }

    /**
     */
    private fun onResumeHandler() {
        prefs.getString("access-token", null)?: prefs.edit().putString("access-token", accessToken).apply()
        loadData()

        uid?.let {
            if (uid != storedUid)
                prefs.edit().putString("user-id", uid).apply()
        }
    }


    /**
     */
    protected abstract fun loadData()
}
