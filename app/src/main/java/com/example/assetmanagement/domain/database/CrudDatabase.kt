package com.example.assetmanagement.domain.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.assetmanagement.data.roomModel.*
import com.example.assetmanagement.domain.dao.ExcelDao


@Database(entities = [RoomExcelModel::class], version = 1,exportSchema = false)
abstract class CrudDatabase : RoomDatabase() {
    abstract fun getExcelDao(): ExcelDao
}