package com.example.assetmanagement.data.repository.datasource

import com.example.assetmanagement.data.model.SingleRow
import com.example.assetmanagement.data.roomModel.RoomExcelModel

/**
 * @author SamuelSep on 4/20/2021.
 */
interface RoomDataSource {
    suspend fun insertCell(fileName:String, rowSequence:Int, singleRow: SingleRow) : Boolean
    suspend fun updateCell(fileName: String, rowSequence:Int, singleRow:SingleRow): Boolean
    suspend fun deleteCellByIdInOneFile(cellId:Int, fileName: String) :  Boolean
    suspend fun deleteAllCell() :  Boolean
    suspend fun deleteAllCellByFileName(fileName: String) :  Boolean
    suspend fun getAllCellByFileName(fileName: String) : ArrayList<RoomExcelModel>
    suspend fun getAllCellByIdInOneFile(cellId:Int, fileName: String) : ArrayList<RoomExcelModel>
    suspend fun getAllCell() : ArrayList<RoomExcelModel>

}