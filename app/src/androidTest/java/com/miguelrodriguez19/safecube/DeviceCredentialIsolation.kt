package com.miguelrodriguez19.safecube

/**
 * Requires a dedicated managed device because changing its lock credential affects system Keystore
 * state beyond the package-data isolation provided by Android Test Orchestrator.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DeviceCredentialIsolation
