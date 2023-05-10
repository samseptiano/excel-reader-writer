package com.example.assetmanagement.domain.usecase.excelUseCase

import com.example.assetmanagement.data.repository.datasource.RoomDataSource
import com.example.assetmanagement.base.domain.usecase.BaseUseCase
import javax.inject.Inject

/**
 * @author SamuelSep on 4/20/2021.
 */
class DeleteItemUseCase @Inject constructor(private val repository: RoomDataSource) :
    BaseUseCase<Boolean, DeleteItemUseCase.Params>() {

    class Params (val cellId:Int?, val fileName:String?)

    override suspend fun run(params: Params): Boolean {

        return if (params.cellId == null && params.fileName != null) {
            repository.deleteAllCellByFileName(params.fileName)
        } else if (params.cellId != null && params.fileName != null) {
            repository.deleteCellByIdInOneFile(params.cellId, params.fileName)
        } else {
            repository.deleteAllCell()
        }

    }

}