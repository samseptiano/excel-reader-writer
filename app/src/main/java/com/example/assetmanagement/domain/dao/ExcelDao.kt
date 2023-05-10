package com.example.assetmanagement.domain.dao

import androidx.room.*
import com.example.assetmanagement.data.roomModel.*

@Dao
interface ExcelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCell(roomExcelModel: RoomExcelModel)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCell(roomExcelModel: RoomExcelModel)

    @Query("DELETE FROM m_excel WHERE id = :id and fileName = :fileName")
    fun deleteCellByIdInOneFile(id: String, fileName: String)

    @Query("DELETE FROM m_excel WHERE fileName= :fileName")
    fun deleteAllCellByFileName(fileName: String)

    @Query("DELETE FROM m_excel")
    fun deleteAllCell()

    @Query("SELECT * FROM m_excel WHERE fileName= :fileName")
    fun getAllCellByFileName(fileName: String): List<RoomExcelModel>

    @Query("SELECT * FROM m_excel where id = :id and fileName = :fileName")
    fun getAllCellByIdInOneFile(id: Int, fileName: String): List<RoomExcelModel>

    @Query("SELECT * FROM m_excel")
    fun getAllCell(): List<RoomExcelModel>

}
