package com.ph.notestash.common.coroutines.scope

import com.ph.notestash.common.coroutines.dispatcher.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class ApplicationCoroutineScope @Inject constructor(
    dispatcherProvider: DispatcherProvider
) : CoroutineScope {
    override val coroutineContext: CoroutineContext = SupervisorJob() + dispatcherProvider.Default
}

@Qualifier
@MustBeDocumented
@Retention
annotation class AppScope