package com.miguelrodriguez19.safecube.core.network.di

import javax.inject.Qualifier

/**
 * Qualifier for auth API instances configured for token refresh operations.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RefreshAuthApi
