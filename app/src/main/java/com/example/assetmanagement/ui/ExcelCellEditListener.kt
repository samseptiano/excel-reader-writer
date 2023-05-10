package com.example.assetmanagement.ui

import com.example.assetmanagement.data.model.SingleRow

interface ExcelCellEditListener {
    fun onCellEdit(cell:Int, singleRow: SingleRow)
}