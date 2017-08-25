package com.dropbox.core.examples.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.dropbox.core.android.Auth
import com.dropbox.core.examples.android.internal.OpenWithActivity
import com.dropbox.core.v2.users.FullAccount
import kotlinx.android.synthetic.main.activity_user.*
import kotlinx.android.synthetic.main.app_bar.*


/**
 * Activity that shows information about the currently logged in user
 */
class UserActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        onInitView()
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    /**
     * init view
     */
    private fun onInitView() {
        //set actionbar
        setSupportActionBar(app_bar)

        //set button action
        login_button.setOnClickListener { Auth.startOAuth2Authentication(this@UserActivity, getString(R.string.app_key)) }
        files_button.setOnClickListener { startActivity(FilesActivity.getIntent(this@UserActivity, "")) }
        open_with.setOnClickListener { startActivity(Intent(this@UserActivity, OpenWithActivity::class.java)) }
    }

    /**
     *
     */
    private fun updateUI() {
        val hasToken = Utils.hasToken(this)
        login_button.visibility = if (hasToken) View.GONE else View.VISIBLE
        email_text.visibility = if (hasToken) View.VISIBLE else View.GONE
        name_text.visibility = if (hasToken) View.VISIBLE else View.GONE
        type_text.visibility = if (hasToken) View.VISIBLE else View.GONE
        files_button.isEnabled = hasToken
        open_with.isEnabled = hasToken
    }

    override fun loadData() {
        GetCurrentAccountTask(DropboxClientFactory.getClient(), object : GetCurrentAccountTask.Callback {
            override fun onComplete(result: FullAccount) {
                email_text.text = result.email
                name_text.text = result.name.displayName
                type_text.text = result.accountType.name
            }

            override fun onError(e: Exception) {
                Log.e(javaClass.name, "Failed to get account details.", e)
            }
        }).execute()
    }

}
