package com.dropbox.core.examples.android

import android.content.Context
import android.net.Uri
import com.dropbox.core.DbxException
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.FileMetadata
import com.dropbox.core.v2.files.WriteMode
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.internal.operators.observable.ObservableCreate
import java.io.FileInputStream
import java.io.IOException

/**
 * Async task to upload a file to a directory
 */
class UploadFileObs(private val mContext: Context, private val mDbxClient: DbxClientV2) {

    /**
     * get observable
     * @return
     */
    fun createFromUrl(requestUrl: String, mPath: String): Observable<FileMetadata> {
        return ObservableCreate(ObservableOnSubscribe { observableEmitter ->
            try {
                val result = makeRequest(requestUrl)
                result?.let { observableEmitter.onError(Throwable("empty")); return@ObservableOnSubscribe }
                observableEmitter.onNext(result!!)
            } catch (e: Exception) {
                e.printStackTrace()
                observableEmitter.onError(e)
            }
        })
    }

    /**
     * make request --> take only fileUrl
     * @param localUri
     * @return
     * @throws DbxException
     * @throws IOException
     */
    @Synchronized
    @Throws(DbxException::class, IOException::class)
    private fun makeRequest(localUri: String): FileMetadata? {
        val localFile = UriHelpers.getFileForUri(mContext, Uri.parse(localUri))
        if (localFile != null) {
            // Note - this is not ensuring the name is a valid dropbox file name
            val remoteFileName = localFile.name

            val inputStream = FileInputStream(localFile)
            return mDbxClient.files().uploadBuilder(localUri + "/" + remoteFileName)
                    .withMode(WriteMode.OVERWRITE)
                    .uploadAndFinish(inputStream)
        }
        return null
    }
}
