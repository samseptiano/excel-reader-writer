package com.example.assetmanagement.data.model

data class ListItems(
    var singleRowList: ArrayList<SingleRow> = arrayListOf(),
    var filterItem: String? = ""
)

data class SingleRow(
    var id:Int?,
    var name: String,
    var value: String? = ""
)

data class ExcellFile(
    var name: String,
    var path: String
)