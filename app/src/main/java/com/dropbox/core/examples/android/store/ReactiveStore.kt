package com.dropbox.core.examples.android.store

import io.reactivex.Flowable

/**
 * Created by davide-syn on 9/8/17.
 */
interface ReactiveStore<in Key, Value> {
    fun getSingular(key: Key): Flowable<Value>
    fun getAll(): Flowable<List<Value>>
    fun storeSingle(value: Value)
    fun storeAll(list: List<Value>)
}
