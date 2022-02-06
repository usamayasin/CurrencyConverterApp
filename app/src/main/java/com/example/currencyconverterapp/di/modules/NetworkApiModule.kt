package com.example.currencyconverterapp.di.modules

import android.content.Context
import com.example.currencyconverterapp.BuildConfig
import com.example.currencyconverterapp.data.local.AppDatabase
import com.example.currencyconverterapp.data.local.dao.CurrenciesDao
import com.example.currencyconverterapp.data.local.repository.LocalRepository
import com.example.currencyconverterapp.data.remote.ApiResponseCallAdapterFactory
import com.example.currencyconverterapp.data.remote.ApiService
import com.example.currencyconverterapp.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * The Dagger Module to provide the instances of [OkHttpClient], [Retrofit], and [ApiService] classes.
 */
@Module
@InstallIn(SingletonComponent::class)
class NetworkApiModule {

    @Singleton
    @Provides
    fun provideOKHttpClient(): OkHttpClient {
        val interceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .readTimeout(Constants.TIME_OUT, TimeUnit.SECONDS)
            .connectTimeout(Constants.TIME_OUT, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()
    }

    @Singleton
    @Provides
    fun providesRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ApiResponseCallAdapterFactory())
            .build()
    }

    @Singleton
    @Provides
    fun providesApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun providesLocalRepository(currenciesDao: CurrenciesDao): LocalRepository {
        return LocalRepository(currenciesDao)
    }

    @Singleton
    @Provides
    fun provideCurrenciesDao(appDatabase: AppDatabase): CurrenciesDao {
        return appDatabase.currenciesDao()
    }

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

}
