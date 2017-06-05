package com.ravikumar.changelogmonitor.di

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.ravikumar.changelogmonitor.BuildConfig
import com.ravikumar.data.ChangelogDataManager
import com.ravikumar.data.remote.ChangelogApiContract
import com.ravikumar.domain.datasource.ChangelogApi
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(includes = [GsonModule::class, DataModule::class])
class ApiModule {
  @Provides
  @Singleton
  fun provideOkHttpClient(): OkHttpClient {
    if (BuildConfig.DEBUG) {
      return OkHttpClient.Builder()
        .addNetworkInterceptor(StethoInterceptor())
        .addInterceptor(getLoggingInterceptor())
        .build()
    }
    return OkHttpClient.Builder()
      .build()
  }

  @Provides
  @Singleton
  fun provideChangelogApi(
    okHttpClient: OkHttpClient,
    gson: Gson
  ): ChangelogApiContract {
    val retrofit = Retrofit.Builder()
      .baseUrl(BuildConfig.BASE_URL)
      .addConverterFactory(GsonConverterFactory.create(gson))
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .client(okHttpClient)
      .build()
    return retrofit.create(ChangelogApiContract::class.java)
  }

  @Provides
  @Singleton
  fun provideDataManager(manager: ChangelogDataManager): ChangelogApi {
    return manager
  }

  private fun getLoggingInterceptor(): Interceptor {
    val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    interceptor.level = HttpLoggingInterceptor.Level.BODY
    return interceptor
  }
}
