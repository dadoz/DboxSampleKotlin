package application.library.davidelmn.dboxsdkwrapperlibrary

import android.net.Uri
import com.dropbox.core.DbxException
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.FileMetadata
import com.dropbox.core.v2.files.ThumbnailFormat
import com.dropbox.core.v2.files.ThumbnailSize
import com.squareup.picasso.Picasso
import com.squareup.picasso.Request
import com.squareup.picasso.RequestHandler
import java.io.IOException

/**
 * Example Picasso request handler that gets the thumbnail url for a dropbox path
 * Only handles urls like dropbox://dropbox/[path_to_file]
 * See [FilesAdapter] for usage
 */
class FileThumbnailRequestHandler(private val mDbxClient: DbxClientV2) : RequestHandler() {

    /**
     * can handle request
     */
    override fun canHandleRequest(data: Request): Boolean = SCHEME == data.uri.scheme && HOST == data.uri.host

    @Throws(IOException::class, DbxException::class)
    override fun load(request: Request, networkPolicy: Int): RequestHandler.Result {
        val downloader = mDbxClient.files().getThumbnailBuilder(request.uri.path)
                .withFormat(ThumbnailFormat.JPEG)
                .withSize(ThumbnailSize.W1024H768)
                .start()

        return RequestHandler.Result(downloader.inputStream, Picasso.LoadedFrom.NETWORK)

    }

    companion object {

        private val SCHEME = "dropbox"
        private val HOST = "dropbox"

        /**
         * Builds a [Uri] for a Dropbox file thumbnail suitable for handling by this handler
         */
        fun buildPicassoUri(file: FileMetadata): Uri {
            return Uri.Builder()
                    .scheme(SCHEME)
                    .authority(HOST)
                    .path(file.pathLower).build()
        }
    }
}
