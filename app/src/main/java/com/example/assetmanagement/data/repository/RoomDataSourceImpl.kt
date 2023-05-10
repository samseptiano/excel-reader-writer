package com.example.assetmanagement.data.repository

import android.util.Log
import com.example.assetmanagement.data.model.SingleRow
import com.example.assetmanagement.data.repository.datasource.RoomDataSource
import com.example.assetmanagement.data.roomModel.RoomExcelModel
import com.example.assetmanagement.domain.dao.ExcelDao
import javax.inject.Inject

/**
 * @author SamuelSep on 4/20/2021.
 */
class RoomDataSourceImpl @Inject constructor(
    private val excelDao: ExcelDao,
) : RoomDataSource {

    override suspend fun insertCell(fileName: String, rowSequence:Int, singleRow: SingleRow): Boolean {
        excelDao.insertCell(RoomExcelModel.toRoomExcelModel(fileName,rowSequence, singleRow))
        Log.d("data inserted to room","Inserted ${singleRow.toString()} at row: $rowSequence ")

        return true
    }

    override suspend fun updateCell(
        fileName: String,
        rowSequence:Int,
        singleRow: SingleRow
    ): Boolean {
        excelDao.updateCell(RoomExcelModel.toRoomExcelModel(fileName, rowSequence, singleRow))
        return true
    }

    override suspend fun deleteCellByIdInOneFile(cellId: Int, fileName: String): Boolean {
        excelDao.deleteCellByIdInOneFile(cellId.toString(), fileName)
        return true
    }

    override suspend fun deleteAllCellByFileName(fileName: String): Boolean {
        excelDao.deleteAllCellByFileName(fileName)
        return true
    }

    override suspend fun deleteAllCell(): Boolean {
        excelDao.deleteAllCell()
        return true
    }
    override suspend fun getAllCellByFileName(fileName: String): ArrayList<RoomExcelModel> {
        return excelDao.getAllCellByFileName(fileName) as ArrayList<RoomExcelModel>
    }

    override suspend fun getAllCellByIdInOneFile(
        cellId: Int,
        fileName: String
    ): ArrayList<RoomExcelModel> {
        return excelDao.getAllCellByIdInOneFile(cellId, fileName) as ArrayList<RoomExcelModel>
    }

    override suspend fun getAllCell(): ArrayList<RoomExcelModel> {
        val aa = excelDao.getAllCell() as ArrayList<RoomExcelModel>
        Log.d("data get all from room","$aa")

        return aa
    }

}