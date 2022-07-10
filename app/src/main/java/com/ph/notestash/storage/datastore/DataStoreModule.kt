package com.ph.notestash.storage.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.ph.notestash.common.coroutines.dispatcher.DispatcherProvider
import com.ph.notestash.common.coroutines.scope.AppScope
import com.ph.notestash.storage.datastore.serializer.NoteSortingPreferencesSerializer
import com.ph.notestash.storage.model.sort.NoteSortingPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.plus
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Singleton
    @Provides
    fun provideDataStoreNoteSortingPreferences(
        @ApplicationContext context: Context,
        @AppScope appScope: CoroutineScope,
        dispatcherProvider: DispatcherProvider,
        serializer: NoteSortingPreferencesSerializer
    ): DataStore<NoteSortingPreferences> = DataStoreFactory.create(
        serializer = serializer,
        scope = appScope + dispatcherProvider.IO,
        produceFile = { context.dataStoreFile("note_sorting_preferences.json") }
    )
}