package com.example.assetmanagement.di.modules


import android.content.Context
import androidx.room.Room
import com.example.assetmanagement.domain.dao.ExcelDao
import com.example.assetmanagement.domain.database.CrudDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {


    @Provides
    @Singleton
    fun providesCrudGameDatabase(@ApplicationContext context: Context): CrudDatabase =
        Room.databaseBuilder(context, CrudDatabase::class.java, "crud")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun providesExcelDao(crudDatabase: CrudDatabase): ExcelDao =
        crudDatabase.getExcelDao()

}