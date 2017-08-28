package com.dropbox.core.examples.android

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.FileMetadata
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.internal.operators.observable.ObservableCreate
import java.io.File
import java.io.FileOutputStream

/**
 * Task to download a file from Dropbox and put it in the Downloads folder
 */
internal class DownloadFileObs(private val mContext: Context, private val mDbxClient: DbxClientV2) {
    val path: File = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS)

//    interface Callback {
//        fun onDownloadComplete(result: File)
//        fun onError(e: Exception)
//    }

    /**
     * cerate obs from params
     */
    fun createFromParams(params: FileMetadata?): Observable<File> {
        return ObservableCreate(ObservableOnSubscribe { observableEmitter ->
            try {
                val result = makeRequest(params)
                result?.let{ observableEmitter.onError(Exception("emtpty file")); return@ObservableOnSubscribe}
                observableEmitter.onNext(result!!)
            } catch (e: Exception) {
                e.printStackTrace()
                observableEmitter.onError(e)
            }
        })
    }

    @Synchronized
    @Throws(Exception::class)
    private fun makeRequest(vararg params: FileMetadata?): File? {
        val metadata = params[0]
        metadata?.let {
            val file = File(path, metadata.name)

            // Make sure the Downloads directory exists.
            if (!path.exists() && !path.mkdirs()) {
                throw RuntimeException("Unable to create directory: " + path)
            }

            if (path.exists() && !path.isDirectory) {
                throw IllegalStateException("Download path is not a directory: " + path)
            }

            // Download the file.
            FileOutputStream(file).use { outputStream ->
                mDbxClient.files().download(metadata.pathLower, metadata.rev)
                        .download(outputStream)
            }

            // Tell android about the file
            val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            intent.data = Uri.fromFile(file)
            mContext.sendBroadcast(intent)
            return file
        }
        return null
    }
}
