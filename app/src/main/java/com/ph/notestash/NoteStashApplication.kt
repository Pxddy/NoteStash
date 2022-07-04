package com.ph.notestash

import android.app.Application
import com.ph.notestash.common.coroutines.dispatcher.DispatcherProvider
import com.ph.notestash.common.coroutines.scope.AppScope
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class NoteStashApplication : Application() {

    @AppScope
    @Inject
    lateinit var appScope: CoroutineScope

    @Inject
    lateinit var dispatcherProvider: DispatcherProvider

    override fun onCreate() {
        super.onCreate()
        setupTimber()
        test()
    }

    private fun setupTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.d("Timber setup")
        }
    }

    private fun test() = appScope.launch(dispatcherProvider.IO) {
        Timber.e("!!!!!!!!!!!!!!!!!!")
    }
}