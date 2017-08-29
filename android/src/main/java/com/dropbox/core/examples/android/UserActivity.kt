package com.dropbox.core.examples.android

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.dropbox.core.android.Auth
import com.dropbox.core.examples.android.internal.OpenWithActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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
        val hasToken = DropboxClientFactory.hasToken(this)
        login_button.visibility = if (hasToken) View.GONE else View.VISIBLE
        email_text.visibility = if (hasToken) View.VISIBLE else View.GONE
        name_text.visibility = if (hasToken) View.VISIBLE else View.GONE
        type_text.visibility = if (hasToken) View.VISIBLE else View.GONE
        files_button.isEnabled = hasToken
        open_with.isEnabled = hasToken
    }

    override fun loadData() {
        GetCurrentAccountObs(dbxClient)
                .create()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({fullAccount -> with(fullAccount, {
                                email_text.text = email
                                name_text.text = name.displayName
                                type_text.text = accountType.name
                            })},
                        { e -> e.printStackTrace()})
    }

}
