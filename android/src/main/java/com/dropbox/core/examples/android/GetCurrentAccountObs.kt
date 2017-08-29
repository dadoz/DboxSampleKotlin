package com.dropbox.core.examples.android

import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.users.FullAccount
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.internal.operators.observable.ObservableCreate

/**
 * Created by davide-syn on 8/29/17.
 */

class GetCurrentAccountObs(private val dbxClient: DbxClientV2) {
    /**
     * create by client
     */
    fun create() : Observable<FullAccount> {
        return ObservableCreate(ObservableOnSubscribe { observableEmitter ->
            try {
                val result = makeRequest()
                observableEmitter.onNext(result)
            } catch (e: Exception) {
                observableEmitter.onError(e)
            }
        })
    }

    @Synchronized
    @Throws(Exception::class)
    private fun makeRequest(): FullAccount = dbxClient.users().currentAccount

}

