package com.example.assetmanagement.base.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

interface CoroutineScopeProvider {
    val io: CoroutineDispatcher
    val main: CoroutineDispatcher
    val async: CoroutineScope

    fun createIoScope(): CoroutineScope
}