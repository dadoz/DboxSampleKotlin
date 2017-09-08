package com.dropbox.core.examples.android

import android.content.Context
import android.support.v7.app.AppCompatActivity
import com.dropbox.core.android.Auth


/**
 * Base class for Activities that require auth tokens
 * Will redirect to auth flow if needed
 */
abstract class BaseActivity : AppCompatActivity() {

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
