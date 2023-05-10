package com.example.assetmanagement.domain.usecase.excelUseCase

import com.example.assetmanagement.data.repository.datasource.RoomDataSource
import com.example.assetmanagement.base.domain.usecase.BaseUseCase
import com.example.assetmanagement.data.roomModel.RoomExcelModel
import javax.inject.Inject

/**
 * @author SamuelSep on 4/20/2021.
 */
class GetAllItemUseCase @Inject constructor(private val repository: RoomDataSource) :
    BaseUseCase<ArrayList<RoomExcelModel>, GetAllItemUseCase.Params>() {

    class Params(val cellId: Int?, val fileName: String?)

    override suspend fun run(params: Params): ArrayList<RoomExcelModel> {
        return if (params.cellId == null && params.fileName != null) {
            repository.getAllCellByFileName(params.fileName)
        } else if (params.cellId != null && params.fileName != null) {
            repository.getAllCellByIdInOneFile(params.cellId, params.fileName)
        } else {
            repository.getAllCell()
        }
    }


}