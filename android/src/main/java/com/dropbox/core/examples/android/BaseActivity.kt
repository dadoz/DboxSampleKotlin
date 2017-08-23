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
        val prefs = getSharedPreferences("dropbox-sample", Context.MODE_PRIVATE)
        var accessToken = prefs.getString("access-token", null)
        if (accessToken == null) {
            accessToken = Auth.getOAuth2Token()
            if (accessToken != null) {
                prefs.edit().putString("access-token", accessToken).apply()
                initAndLoadData(accessToken)
            }
        } else {
            initAndLoadData(accessToken)
        }

        val uid = Auth.getUid()
        val storedUid = prefs.getString("user-id", null)
        if (uid != null && uid != storedUid) {
            prefs.edit().putString("user-id", uid).apply()
        }
    }

    /**

     * @param accessToken
     */
    private fun initAndLoadData(accessToken: String) {
        DropboxClientFactory.init(accessToken)
        PicassoClient.init(applicationContext, DropboxClientFactory.getClient())
        loadData()
    }

    /**

     */
    protected abstract fun loadData()
}
