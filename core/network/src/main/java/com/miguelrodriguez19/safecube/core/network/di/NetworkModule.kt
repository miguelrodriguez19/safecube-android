package com.miguelrodriguez19.safecube.core.network.di

import com.miguelrodriguez19.safecube.core.network.ApiService
import com.miguelrodriguez19.safecube.core.network.AuthInterceptor
import com.miguelrodriguez19.safecube.core.network.BuildConfig
import com.miguelrodriguez19.safecube.core.network.NetworkClientFactory
import com.miguelrodriguez19.safecube.core.network.NetworkConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideNetworkConfig(): NetworkConfig = NetworkConfig(
        baseUrl = BuildConfig.BASE_URL,
    )

    @Provides
    @Singleton
    fun provideJson(): Json = NetworkClientFactory.createJson()

    @Provides
    @Singleton
    fun provideOkHttpClient(
        config: NetworkConfig,
        authInterceptor: AuthInterceptor,
    ): OkHttpClient = NetworkClientFactory.createOkHttpClient(
        config = config,
        authInterceptor = authInterceptor,
    )

    @Provides
    @Singleton
    fun provideRetrofit(
        config: NetworkConfig,
        json: Json,
        okHttpClient: OkHttpClient,
    ): Retrofit = NetworkClientFactory.createRetrofit(
        config = config,
        json = json,
        okHttpClient = okHttpClient,
    )

    @Provides
    @Singleton
    fun provideApiService(
        retrofit: Retrofit,
    ): ApiService = retrofit.create(ApiService::class.java)

}
