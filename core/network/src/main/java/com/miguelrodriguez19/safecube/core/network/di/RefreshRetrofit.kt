package com.miguelrodriguez19.safecube.core.network.di

import javax.inject.Qualifier

/**
 * Qualifier for Retrofit instances used exclusively for token refresh requests.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RefreshRetrofit
