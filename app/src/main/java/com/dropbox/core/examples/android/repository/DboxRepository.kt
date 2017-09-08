package com.dropbox.core.examples.android.repository

import com.dropbox.core.examples.android.services.ApiService
import com.dropbox.core.examples.android.store.DboxStore
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * layer to be interfaced with
 * db or network
 * Created by davide-syn on 9/8/17.
 */
class DboxRepository<Params, Result> {
    //init store - get all data or whatever u need
    val store: DboxStore<Params, Result> = DboxStore()
    val apiService: ApiService = ApiService()

    /**
     * get data from local store or db (no network interaction)
     */
    fun get(): Flowable<List<Result>> = store.getAll()

    /**
     * fetch data from network - so api or interaction with third part services
     */
    fun fetch(): Completable = apiService.getSingleObsDataRequest().doOnSuccess{ store::storeAll }.toCompletable()

    /**
     * fetch data from network - so api or interaction with third part services but dont push on flowable
     * rx processor :)
     */
    fun request(): Single<List<String>> = apiService.getSingleObsDataRequest()

    fun push() = apiService.pushDataRequest()

    fun delete() = apiService.deleteDataRequest()
}

