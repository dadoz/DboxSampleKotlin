package com.dropbox.core.examples.android.services

import io.reactivex.Single
import java.util.*


/**
 * Created by davide-syn on 9/8/17.
 */
class ApiService {
    fun getSingleObsDataRequest() : Single<List<String>> = Single.just(Arrays.asList("remote data 1", "remote data 2", "remote data 3", "remote data 4", "remote data 5"))

    fun pushDataRequest() {
        //TODO make request to be
    }
    fun deleteDataRequest() {
        //TODO make request to be
    }
}