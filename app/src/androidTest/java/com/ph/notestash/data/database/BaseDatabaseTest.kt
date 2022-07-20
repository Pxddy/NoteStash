package com.ph.notestash.data.database

import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import javax.inject.Inject

internal abstract class BaseDatabaseTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var db: AppDatabase

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @After
    fun teardown() {
        db.close()
    }
}