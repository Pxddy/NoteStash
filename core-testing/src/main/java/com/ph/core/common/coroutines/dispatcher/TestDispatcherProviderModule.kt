package com.ph.core.common.coroutines.dispatcher

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object TestDispatcherProviderModule {

    @Provides
    fun provideTestDispatcherProvider(): TestDispatcherProvider = TestDispatcherProvider()
}