package com.dropbox.core.examples.android.store

import io.reactivex.Flowable
import io.reactivex.processors.FlowableProcessor
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers

/**
 * Created by davide-syn on 9/8/17.
 */
class DboxStore<Params, Result>(): ReactiveStore<Params, Result> {
    private val flowableProcessor: FlowableProcessor<List<Result>> = PublishProcessor.create()
    private val singleFlowableProcessor: FlowableProcessor<Result> = PublishProcessor.create()
    private lateinit var items: MutableList<Result> //Arrays.asList("test1", "test2", "test3", "test4")

    override fun getSingular(key: Params): Flowable<Result> {
        return singleFlowableProcessor.startWith(items.get(key as Int))
                .subscribeOn(Schedulers.computation())
    }

    override fun getAll(): Flowable<List<Result>> {
        return flowableProcessor.startWith(items)
                .subscribeOn(Schedulers.computation())
    }

    override fun storeAll(list: List<Result>) {
        items.addAll(list)
        flowableProcessor.onNext(list)
    }

    override fun storeSingle(item: Result) {
        items.add(item)
        singleFlowableProcessor.onNext(item)
        flowableProcessor.onNext(items)
    }

}
