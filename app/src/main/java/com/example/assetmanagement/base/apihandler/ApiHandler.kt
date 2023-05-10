package com.example.assetmanagement.base.apihandler

import android.content.res.Resources
import com.mkt.arch.functional.api_handler.default_error_handler.DefaultErrorResponseHandler
import retrofit2.Response

object ApiHandler {

    suspend fun <T: Any> handleApi(
        errorHandler: ErrorResponseHandler = DefaultErrorResponseHandler(),
        block: suspend () -> Response<T>
    ): T? {
        try {
            val response = block.invoke()

            if (response.isSuccessful) {
                return response.body()
            }

            // When Error
            throw errorHandler.handle(response.errorBody(), response.code())
        } catch (e: Exception) {
            return if (e is Resources.NotFoundException) null
            else throw e
        }
    }
}