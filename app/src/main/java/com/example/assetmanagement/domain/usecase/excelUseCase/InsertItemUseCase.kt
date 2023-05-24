package com.example.assetmanagement.domain.usecase.excelUseCase

import android.util.Log
import com.example.assetmanagement.base.domain.usecase.BaseUseCase
import com.example.assetmanagement.data.model.ListItems
import com.example.assetmanagement.data.repository.datasource.RoomDataSource
import com.example.assetmanagement.domain.database.CrudDatabase
import javax.inject.Inject

/**
 * @author SamuelSep on 4/20/2021.
 */
class InsertItemUseCase @Inject constructor(
    private val crudDatabase: CrudDatabase,
    private val repository: RoomDataSource
) :
    BaseUseCase<Boolean, InsertItemUseCase.Params>() {

    class Params(val filename: String, val listItem: ArrayList<ListItems>)

    override suspend fun run(params: Params): Boolean {
        var id = 0
        params.listItem.mapIndexed { index, listItems ->
            listItems.singleRowList.map { itSingle ->
                itSingle.id = id + 1
                try {
                    repository.insertCell(params.filename, index, itSingle)
                } catch (e: Exception) {
                    Log.e("error: insert to room", e.toString())
                }
                id++
            }
        }
        return true
    }

}