package com.example.assetmanagement.base.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.assetmanagement.data.repository.RoomDataSourceImpl
import com.example.assetmanagement.domain.database.CrudDatabase
import com.example.assetmanagement.domain.usecase.excelUseCase.DeleteItemUseCase
import com.example.assetmanagement.domain.usecase.excelUseCase.GetAllItemUseCase
import com.example.assetmanagement.domain.usecase.excelUseCase.InsertItemUseCase
import com.example.assetmanagement.domain.usecase.excelUseCase.UpdateItemUseCase
import com.example.assetmanagement.ui.viewmodel.ExcellReaderViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author SamuelSep on 4/20/2021.
 */
@Singleton
@Suppress("UNCHECKED_CAST")
class ViewModelFactory @Inject constructor(private val crudDatabase: CrudDatabase, private val repository: RoomDataSourceImpl, @ApplicationContext private val context: Context) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ExcellReaderViewModel::class.java) -> ExcellReaderViewModel(
                InsertItemUseCase(crudDatabase, repository),
                UpdateItemUseCase(repository),
                DeleteItemUseCase(repository),
                GetAllItemUseCase(repository)
            ) as T
            else -> throw IllegalArgumentException("Unknown viewModel class $modelClass")
        }
    }

}