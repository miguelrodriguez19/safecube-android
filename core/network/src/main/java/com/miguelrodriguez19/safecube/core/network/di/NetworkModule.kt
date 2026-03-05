package com.miguelrodriguez19.safecube.core.network.di

import com.miguelrodriguez19.safecube.core.network.AuthInterceptor
import com.miguelrodriguez19.safecube.core.network.BuildConfig
import com.miguelrodriguez19.safecube.core.network.NetworkClientFactory
import com.miguelrodriguez19.safecube.core.network.NetworkConfig
import com.miguelrodriguez19.safecube.core.network.TokenRefreshAuthenticator
import com.miguelrodriguez19.safecube.core.network.generated.api.AuthControllerApi
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
        tokenRefreshAuthenticator: TokenRefreshAuthenticator,
    ): OkHttpClient = NetworkClientFactory.createOkHttpClient(
        config = config,
        authInterceptor = authInterceptor,
        authenticator = tokenRefreshAuthenticator,
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
    @RefreshOkHttpClient
    fun provideRefreshOkHttpClient(
        config: NetworkConfig,
    ): OkHttpClient = NetworkClientFactory.createOkHttpClient(
        config = config,
    )

    @Provides
    @Singleton
    @RefreshRetrofit
    fun provideRefreshRetrofit(
        config: NetworkConfig,
        json: Json,
        @RefreshOkHttpClient refreshOkHttpClient: OkHttpClient,
    ): Retrofit = NetworkClientFactory.createRetrofit(
        config = config,
        json = json,
        okHttpClient = refreshOkHttpClient,
    )

    @Provides
    @Singleton
    // Contract-only usage: generated API runs on app-owned Retrofit/OkHttp stack.
    fun provideAuthControllerApi(
        retrofit: Retrofit,
    ): AuthControllerApi = retrofit.create(AuthControllerApi::class.java)

    @Provides
    @Singleton
    @RefreshAuthApi
    fun provideRefreshAuthControllerApi(
        @RefreshRetrofit refreshRetrofit: Retrofit,
    ): AuthControllerApi = refreshRetrofit.create(AuthControllerApi::class.java)

}
