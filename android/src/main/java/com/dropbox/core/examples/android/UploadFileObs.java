package com.dropbox.core.examples.android;

import android.content.Context;
import android.net.Uri;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.internal.operators.observable.ObservableCreate;

/**
 * Async task to upload a file to a directory
 */
class UploadFileObs {
    private final Context mContext;
    private final DbxClientV2 mDbxClient;
    private Exception mException;

    UploadFileObs(Context context, DbxClientV2 dbxClient) {
        mContext = context;
        mDbxClient = dbxClient;
    }

    /**
     * get observable
     * @return
     */
    public Observable<FileMetadata> createFromUrl(final String requestUrl, String mPath) {
        return new ObservableCreate<>(new ObservableOnSubscribe<FileMetadata>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<FileMetadata> observableEmitter) throws Exception {
                try {
                    FileMetadata result = makeRequest(requestUrl);
                    observableEmitter.onNext(result);
                } catch (Exception e) {
                    e.printStackTrace();
                    observableEmitter.onError(e);
                }
            }
        });
    }

    /**
     * make request --> take only fileUrl
     * @param localUri
     * @return
     * @throws DbxException
     * @throws IOException
     */
    protected synchronized FileMetadata makeRequest(String localUri) throws DbxException, IOException {
        File localFile = UriHelpers.getFileForUri(mContext, Uri.parse(localUri));
        if (localFile != null) {
            // Note - this is not ensuring the name is a valid dropbox file name
            String remoteFileName = localFile.getName();

            InputStream inputStream = new FileInputStream(localFile);
            return mDbxClient.files().uploadBuilder(localUri + "/" + remoteFileName)
                    .withMode(WriteMode.OVERWRITE)
                    .uploadAndFinish(inputStream);
        }
        return null;
    }
}
