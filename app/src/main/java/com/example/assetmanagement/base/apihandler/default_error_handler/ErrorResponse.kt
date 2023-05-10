package com.example.assetmanagement.base.apihandler.default_error_handler

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @Expose
    @SerializedName("status") val status: String? = null,
    @Expose
    @SerializedName("message") val message: String? = null,
    @Expose
    @SerializedName("error") val error: String? = null,
    @Expose
    @SerializedName("error_description") val errorDescription: String? = null
)
