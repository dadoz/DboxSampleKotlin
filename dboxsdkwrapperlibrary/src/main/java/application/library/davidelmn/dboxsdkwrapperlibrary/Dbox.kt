package application.library.davidelmn.dboxsdkwrapperlibrary

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import application.library.davidelmn.dboxsdkwrapperlibrary.observable.DownloadFileObs
import application.library.davidelmn.dboxsdkwrapperlibrary.observable.ListFolderObs
import application.library.davidelmn.dboxsdkwrapperlibrary.observable.UploadFileObs
import com.dropbox.core.android.Auth
import application.library.davidelmn.dboxsdkwrapperlibrary.FilesAdapter.Callback
/**
 * Created by davide-syn on 9/6/17.
 */
class Dbox(val context: Context, val callbacks: Callback) {

    protected val dbxClient by lazy {
        DropboxClientFactory(accessToken).client
    }

    protected val picassoClient by lazy {
        PicassoClient(context, dbxClient).client
    }

    private val prefs by lazy {
        context.getSharedPreferences("dropbox-sample", Context.MODE_PRIVATE)
    }

    private val storedUid by lazy {
        prefs.getString("user-id", null)
    }

    private val accessToken: String by lazy {
        prefs.getString("access-token", null)?: Auth.getOAuth2Token()
    }

    private val uid = Auth.getUid()

    /**
     * build adapter
     */
    fun buildAdapter() : RecyclerView.Adapter<FilesAdapter.MetadataViewHolder>  = FilesAdapter(picasso, mCallback = callbacks)

    /**
     * load data
     */
    fun loadData() {
        val disposable = ListFolderObs(dbxClient)
                .createByPath(mPath)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe ({result -> onDataLoaded(result)}, { error -> error(Exception(error.message))})
        compositeDisposable.add(disposable)
    }

    /**
     * download file
     */
    private fun downloadFile() {
        val disposable = DownloadFileObs(applicationContext, dbxClient)
                .createFromParams(mSelectedFile)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ file -> onDownloadComplete(file) }, {error -> onError(Exception(error.message)) })
        compositeDisposable.add(disposable)
    }

    /**
     * upload file
     */
    private fun uploadFile(fileUri: String) {
        val disposable = UploadFileObs(this, dbxClient)
                .createFromUrl(fileUri, mPath)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({ metadata -> onUploadComplete(metadata) }, { error -> onError(Exception(error.message)) })
        compositeDisposable.add(disposable)
    }

    fun dispose() {
        //TODO dispose all stuff
    }

}