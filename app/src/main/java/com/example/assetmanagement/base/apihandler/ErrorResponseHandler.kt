package com.example.assetmanagement.base.apihandler

import com.google.gson.Gson
import com.example.assetmanagement.base.apihandler.exception.BadRequestException
import com.example.assetmanagement.base.apihandler.exception.NotFoundException
import com.example.assetmanagement.base.apihandler.exception.ServerErrorException
import com.example.assetmanagement.base.apihandler.exception.UnauthorizedException
import com.mkt.arch.functional.api_handler.ResponseCode
import okhttp3.ResponseBody
import java.lang.reflect.Type

abstract class ErrorResponseHandler {

    fun handle(errorBody: ResponseBody?, responseCode: Int): Exception {
        return fetchError(errorBody, responseCode)
    }

    protected open fun fetchError(errorBody: ResponseBody?, responseCode: Int): Exception {
        return try {
            val exception = when (responseCode) {
                ResponseCode.UNAUTHORIZED -> UnauthorizedException()
                ResponseCode.BAD_REQUEST -> {
                    errorBody?.let { error ->
                        handleBadRequestError(error)
                    } ?: Exception()
                }
                ResponseCode.NOT_FOUND -> NotFoundException()
                in 500..599 -> ServerErrorException()
                else -> Exception()
            }
            exception
        } catch (exception: Exception) {
            exception
        }
    }

    protected abstract fun handleBadRequestError(errorBody: ResponseBody): BadRequestException

    protected fun <T> ResponseBody.errorBodyParser(type: Type): T? {
        return this.string().let {
            Gson().fromJson(it, type)
        }
    }
}