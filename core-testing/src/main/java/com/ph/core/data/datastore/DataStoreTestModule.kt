package com.ph.core.data.datastore

import androidx.datastore.core.DataStore
import com.ph.notestash.data.datastore.DataStoreModule
import com.ph.notestash.data.model.sort.NoteSortingPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataStoreModule::class],
)
object DataStoreTestModule {

    @Singleton
    @Provides
    fun provideDataStoreNoteSortingPreferences(): DataStore<NoteSortingPreferences> {
        return InMemoryDataStore(defaultValue = NoteSortingPreferences())
    }
}