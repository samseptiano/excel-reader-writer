package com.example.assetmanagement.di.modules

import com.example.assetmanagement.base.coroutine.AppDispatchers
import com.example.assetmanagement.base.coroutine.AppDispatchersImpl
import com.example.assetmanagement.di.qualifier.GameOkHttpClientQualifier
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


/**
 * @author SamuelSep on 4/20/2021.
 */

@Module
@InstallIn(SingletonComponent::class)
class NetworkApiModule {

//    @Singleton
//    @Provides
//    fun buildSampleServicesClient(
//        @GameOkHttpClientQualifier
//        okHttpClient: OkHttpClient,
//        coroutineCallAdapterFactory: CoroutineCallAdapterFactory,
//        gsonConverterFactory: GsonConverterFactory
//    ): SampleServices {
//        return Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .client(okHttpClient)
//            .addConverterFactory(gsonConverterFactory)
//            .addCallAdapterFactory(coroutineCallAdapterFactory)
//            .build()
//            .create(SampleServices::class.java)
//    }

    @Singleton
    @Provides
    @GameOkHttpClientQualifier
    fun buildOkHttpClient(): OkHttpClient {
        return OkHttpClient().newBuilder().also { item ->
            val log = HttpLoggingInterceptor()
            log.level = HttpLoggingInterceptor.Level.BODY
            item.addInterceptor(log)
            item.retryOnConnectionFailure(true)
        }.build()
    }

    @Provides
    @Singleton
    fun provideAppDispatchers(): AppDispatchers {
        return AppDispatchersImpl()
    }

    @Provides
    @Singleton
    fun getGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @Provides
    @Singleton
    fun getCoroutineCallAdapter(): CoroutineCallAdapterFactory {
        return CoroutineCallAdapterFactory.invoke()
    }

}