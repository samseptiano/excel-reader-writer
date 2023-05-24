package com.example.assetmanagement.ui

import com.example.assetmanagement.data.model.SingleRow

interface ExcelCellImageListener {
    fun onCellImageEdit(row:Int, cell:Int, singleRow: SingleRow)
}