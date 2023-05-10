package com.mkt.arch.functional.api_handler.default_error_handler

import com.example.assetmanagement.base.apihandler.default_error_handler.ErrorResponse
import com.google.gson.reflect.TypeToken
import com.example.assetmanagement.base.apihandler.exception.BadRequestException
import com.example.assetmanagement.base.apihandler.ErrorResponseHandler
import okhttp3.ResponseBody

class DefaultErrorResponseHandler: ErrorResponseHandler() {
    override fun handleBadRequestError(errorBody: ResponseBody): BadRequestException {
        val type = object: TypeToken<ErrorResponse>(){}.type
        val errorWrapper = errorBody.errorBodyParser<ErrorResponse>(type)
        val errorMessage = errorWrapper?.message.orEmpty()
        return BadRequestException(errorMessage)
    }
}