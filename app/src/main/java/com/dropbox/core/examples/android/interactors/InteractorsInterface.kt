package com.dropbox.core.examples.android.interactors

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Created by davide-syn on 9/8/17.
 * all type interactors needed in the app
 */
interface InteractorsInterface<in Params, Result> {
    interface SendInteractor<in Params, Result> {
        fun getResult(params: Params): Single<Result>
    }

    interface DeleteInteractor<in Params, Result> {
        fun getResult(params: Params): Single<Result>
    }

    interface RequestInteractor<in Params, Result> {
        fun getSingle(params: Params): Single<Result>
    }

    interface RetrieveInteractor<in Params, Result> {
        fun getBehaviourStream(params: Params): Flowable<List<Result>>
    }

    interface RefreshInteractor<in Params> {
        fun getSingle(params: Params): Completable
    }
}