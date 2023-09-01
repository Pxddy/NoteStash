package com.ph.notestash.common.coroutines

import com.ph.notestash.common.coroutines.dispatcher.DefaultDispatcherProvider
import com.ph.notestash.common.coroutines.dispatcher.DispatcherProvider
import com.ph.notestash.common.coroutines.scope.AppScope
import com.ph.notestash.common.coroutines.scope.ApplicationCoroutineScope
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope

@Module
@InstallIn(SingletonComponent::class)
interface CoroutinesModule {

    @Binds
    fun bindDispatcherProvider(defaultDispatcherProvider: DefaultDispatcherProvider): DispatcherProvider

    @AppScope
    @Binds
    fun bindCoroutineScope(applicationCoroutineScope: ApplicationCoroutineScope): CoroutineScope
}