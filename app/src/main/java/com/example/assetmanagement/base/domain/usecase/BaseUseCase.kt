package com.example.assetmanagement.base.domain.usecase


import com.example.assetmanagement.base.coroutine.AppDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

abstract class BaseUseCase<out Type, in Params> where Type : Any {

    abstract suspend fun run(params: Params): Type

    operator fun invoke(
        params: Params,
        scope: CoroutineScope,
        appDispatchers: AppDispatchers,
        onResult: (Type) -> Unit = {}
    ) {
        scope.launch {
            val deferred = async(appDispatchers.io) {
                run(params)
            }
            onResult(deferred.await())
        }
    }

    class None
}