package application.library.davidelmn.dboxsdkwrapperlibrary

import android.content.Context

import com.dropbox.core.v2.DbxClientV2
import com.squareup.picasso.OkHttpDownloader
import com.squareup.picasso.Picasso

/**
 * Singleton instance of Picasso pre-configured
 */
class PicassoClient(val context: Context, val dbxClient: DbxClientV2) {
    val client: Picasso by lazy {
        Picasso.Builder(context)
            .downloader(OkHttpDownloader(context))
            .addRequestHandler(FileThumbnailRequestHandler(dbxClient))
            .build()
    }
}
