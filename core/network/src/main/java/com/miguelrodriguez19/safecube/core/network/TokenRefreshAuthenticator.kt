package com.miguelrodriguez19.safecube.core.network

import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * Owner for 401 -> refresh -> retry flow.
 *
 * Phase 2 keeps this as a no-op until refresh logic is implemented.
 */
@Singleton
class TokenRefreshAuthenticator @Inject constructor() : Authenticator {
    override fun authenticate(
        route: Route?,
        response: Response,
    ): Request? = null
}
