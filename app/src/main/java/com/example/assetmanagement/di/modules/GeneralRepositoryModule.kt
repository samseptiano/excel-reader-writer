package com.example.assetmanagement.di.modules

import com.example.assetmanagement.data.repository.datasource.RoomDataSource
import com.example.assetmanagement.data.repository.RoomDataSourceImpl
import com.example.assetmanagement.domain.dao.ExcelDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class GeneralRepositoryModule {
    @Singleton
    @Provides
    fun provideRoomsDataSource(excelDao: ExcelDao): RoomDataSource {
        return RoomDataSourceImpl(excelDao)
    }

}