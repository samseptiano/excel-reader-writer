package com.example.assetmanagement.domain.usecase.excelUseCase

import com.example.assetmanagement.data.repository.datasource.RoomDataSource
import com.example.assetmanagement.base.domain.usecase.BaseUseCase
import com.example.assetmanagement.data.model.SingleRow
import javax.inject.Inject

/**
 * @author SamuelSep on 4/20/2021.
 */
class UpdateItemUseCase @Inject constructor(private val repository: RoomDataSource) :
    BaseUseCase<Boolean, UpdateItemUseCase.Params>() {

    class Params (val fileName:String, val rowSequence:Int, val singleRow: SingleRow, val updDate:String)

    override suspend fun run(params: Params): Boolean {
       return repository.updateCell(params.fileName, params.rowSequence, params.singleRow)
    }

}