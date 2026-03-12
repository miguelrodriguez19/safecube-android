package com.miguelrodriguez19.safecube.core.network.di

import javax.inject.Qualifier

/**
 * Qualifier for OkHttp clients that must not trigger authenticated interceptors.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RefreshOkHttpClient
