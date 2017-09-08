package com.dropbox.core.examples.android.interactors

import com.dropbox.core.examples.android.repository.DboxRepository
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Created by davide-syn on 9/8/17.
 */
class DboxRetrieveInteractor<Params, Result>(): InteractorsInterface.RetrieveInteractor<Params, Result> {
    override fun getBehaviourStream(params: Params): Flowable<List<Result>> {
        return dboxRepository.get()
                // fetch if emitted value is none
                .flatMapSingle { list -> when {
                    list.isEmpty() -> dboxRepository.fetch().toSingleDefault(list)
                    else -> Single.just(list)
                }}
    }

    val dboxRepository: DboxRepository<Params, Result> = DboxRepository()

}