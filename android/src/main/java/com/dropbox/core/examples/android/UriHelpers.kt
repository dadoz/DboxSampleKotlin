package com.dropbox.core.examples.android

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import java.io.File

/**
 * Utility functions to support Uri conversion and processing.
 */
object UriHelpers {

    /**
     * Get the file path for a uri. This is a convoluted way to get the path for an Uri created using the
     * StorageAccessFramework. This in no way is the official way to do this but there does not seem to be a better
     * way to do this at this point. It is taken from https://github.com/iPaulPro/aFileChooser.
     *
     * @param context The context of the application
     * @param uri The uri of the saved file
     * @return The file with path pointing to the saved file. It can return null if we can't resolve the uri properly.
     */
    @Throws(Exception::class)
    fun getFileForUri(context: Context, uri: Uri): File? = when {
        DocumentsContract.isDocumentUri(context, uri) -> when {
            isExternalStorageDocument(uri) -> File(getExternalStorageDocument(uri))
            isDownloadsDocument(uri) -> File(getDownloadsDocument(uri, context))
            isMediaDocument(uri) -> File(getMediaDocument(uri, context))
            else -> null
        }
        ("content".equals(uri.scheme, ignoreCase = true)) -> File(getDataColumn(context, uri, null, null))
        ("file".equals(uri.scheme, ignoreCase = true)) -> File(uri.path)
        else -> null
    }

    /**
     * get media docs
     */
    @Throws(Exception::class)
    private fun getMediaDocument(uri: Uri, context: Context): String? {
        // MediaProvider
        val docId = DocumentsContract.getDocumentId(uri)
        val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val type = split[0]

        val contentUri = when(type) {
            "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            else -> null
        }
        val selection = "_id=?"
        val selectionArgs = arrayOf(split[1])
        return getDataColumn(context, contentUri, selection, selectionArgs)
    }

    /**
     * get downloads docs
     */
    @Throws(Exception::class)
    private fun getDownloadsDocument(uri: Uri, context: Context): String? {
        // DownloadsProvider
        val id = DocumentsContract.getDocumentId(uri)
        val contentUri = ContentUris
                .withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)!!)

        return getDataColumn(context, contentUri, null, null)
    }

    /**
     * get external storage
     */
    @Throws(Exception::class)
    private fun getExternalStorageDocument(uri: Uri): String? {
        val docId = DocumentsContract.getDocumentId(uri)
        val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val type = split[0]

        if ("primary".equals(type, ignoreCase = true)) {
            return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
        }
        return null
    }

    /**
     *
     */
    private fun getDataColumn(context: Context, uri: Uri?, selection: String?,
                              selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean = "com.android.externalstorage.documents" == uri.authority

    private fun isDownloadsDocument(uri: Uri): Boolean = "com.android.providers.downloads.documents" == uri.authority

    private fun isMediaDocument(uri: Uri): Boolean = "com.android.providers.media.documents" == uri.authority
}
