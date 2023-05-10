package com.example.assetmanagement.base.data

import com.google.gson.annotations.SerializedName

class ResponseWrapper<T>(
    @field:SerializedName("count")
    var count: Int,
    @field:SerializedName("next")
    var next: String? = null,
    @field:SerializedName("previous")
    var previous: String? = null,
    @field:SerializedName("results")
    var results: T,
)