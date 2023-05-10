package com.example.assetmanagement.base.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.assetmanagement.base.coroutine.AppDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

open class BaseViewModel : ViewModel(), CoroutineScope {
    @Inject
    lateinit var appDispatchers: AppDispatchers

    var job = Job()
    private val _loading by lazy { MutableLiveData<Boolean>() }
    val loading: LiveData<Boolean> = _loading
    protected val coroutineScope by lazy { appDispatchers.getScope() }

    fun handleError(resCode: Int, message: String) {}

    override fun onCleared() {
        super.onCleared()
        coroutineScope.cancel()
        job.cancel()
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
}