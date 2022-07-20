package com.ph.core.common.coroutines

import com.ph.core.common.coroutines.dispatcher.TestDispatcherProvider
import com.ph.notestash.common.coroutines.CoroutinesModule
import com.ph.notestash.common.coroutines.dispatcher.DispatcherProvider
import com.ph.notestash.common.coroutines.scope.AppScope
import com.ph.notestash.common.coroutines.scope.ApplicationCoroutineScope
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineScope

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [CoroutinesModule::class],
)
interface CoroutinesTestModule {

    @get:Binds
    val TestDispatcherProvider.bindDispatcherProvider: DispatcherProvider

    @AppScope
    @Binds
    fun ApplicationCoroutineScope.bindCoroutineScope(): CoroutineScope
}