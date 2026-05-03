package com.miguelrodriguez19.safecube.core.network.di

import com.miguelrodriguez19.safecube.core.network.BuildConfig
import com.miguelrodriguez19.safecube.core.network.data.auth.AuthInterceptor
import com.miguelrodriguez19.safecube.core.network.data.auth.TokenRefreshAuthenticator
import com.miguelrodriguez19.safecube.core.network.data.client.NetworkClientFactory
import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkConfig
import com.miguelrodriguez19.safecube.core.network.generated.api.AuthControllerApi
import com.miguelrodriguez19.safecube.core.network.generated.api.VaultControllerApi
import com.miguelrodriguez19.safecube.core.network.generated.api.VaultKeyMaterialControllerApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * Dependency graph for networking infrastructure.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    /**
     * Provides app-level network configuration from build constants.
     */
    @Provides
    @Singleton
    fun provideNetworkConfig(): NetworkConfig = NetworkConfig(
        baseUrl = BuildConfig.BASE_URL,
        isDebug = BuildConfig.DEBUG,
    )

    /**
     * Provides the shared Json serializer configuration.
     */
    @Provides
    @Singleton
    fun provideJson(): Json = NetworkClientFactory.createJson()

    /**
     * Provides the primary authenticated OkHttp client.
     */
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

    /**
     * Provides the primary Retrofit instance used by generated APIs.
     */
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

    /**
     * Provides an OkHttp client that bypasses auth interceptors for refresh operations.
     */
    @Provides
    @Singleton
    @RefreshOkHttpClient
    fun provideRefreshOkHttpClient(
        config: NetworkConfig,
    ): OkHttpClient = NetworkClientFactory.createOkHttpClient(
        config = config,
    )

    /**
     * Provides a Retrofit instance dedicated to token refresh calls.
     */
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

    /**
     * Provides the generated auth API bound to the primary Retrofit stack.
     */
    @Provides
    @Singleton
    fun provideAuthControllerApi(
        retrofit: Retrofit,
    ): AuthControllerApi = retrofit.create(AuthControllerApi::class.java)

    /**
     * Provides the generated vault API bound to the primary Retrofit stack.
     */
    @Provides
    @Singleton
    fun provideVaultKeyMaterialControllerApi(
        retrofit: Retrofit,
    ): VaultKeyMaterialControllerApi = retrofit.create(VaultKeyMaterialControllerApi::class.java)

    /**
     * Provides the generated vault items API bound to the primary Retrofit stack.
     */
    @Provides
    @Singleton
    fun provideVaultControllerApi(
        retrofit: Retrofit,
    ): VaultControllerApi = retrofit.create(VaultControllerApi::class.java)

    /**
     * Provides the generated auth API bound to the refresh Retrofit stack.
     */
    @Provides
    @Singleton
    @RefreshAuthApi
    fun provideRefreshAuthControllerApi(
        @RefreshRetrofit refreshRetrofit: Retrofit,
    ): AuthControllerApi = refreshRetrofit.create(AuthControllerApi::class.java)
}
