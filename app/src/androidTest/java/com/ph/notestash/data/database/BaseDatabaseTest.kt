package com.ph.notestash.data.database

import androidx.annotation.CallSuper
import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import javax.inject.Inject

internal abstract class BaseDatabaseTest {

    @get:Rule(order = 0)
    abstract val hiltRule: HiltAndroidRule

    @Inject
    lateinit var db: AppDatabase

    @CallSuper
    @Before
    fun setup() {
        hiltRule.inject()
    }

    @CallSuper
    @After
    fun teardown() {
        db.close()
    }
}