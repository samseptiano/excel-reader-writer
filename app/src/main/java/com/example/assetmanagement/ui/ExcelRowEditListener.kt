package com.example.assetmanagement.ui

import com.example.assetmanagement.data.model.SingleRow

interface ExcelRowEditListener {
    fun onRowEdit(cell:Int, row: Int, singleRow: SingleRow)
}