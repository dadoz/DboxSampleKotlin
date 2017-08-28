package com.dropbox.core.examples.android

import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.ListFolderResult
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.internal.operators.observable.ObservableCreate

/**
 * Created by davide-syn on 8/28/17.
 */
class ListFolderObs(private val client: DbxClientV2) {


    fun createByPath(path: String) : Observable<ListFolderResult> {
        return ObservableCreate(ObservableOnSubscribe { observableEmitter ->
            try {
                val result = makeRequest(path)
                observableEmitter.onNext(result)
            } catch (e: Exception) {
                observableEmitter.onError(e)
            }
        })
    }

    @Synchronized
    @Throws(Exception::class)
    fun makeRequest(path: String): ListFolderResult = client.files().listFolder(path)

}
