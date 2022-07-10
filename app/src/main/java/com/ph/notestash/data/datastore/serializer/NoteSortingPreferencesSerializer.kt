package com.ph.notestash.data.datastore.serializer

import com.ph.notestash.data.model.sort.NoteSortingPreferences
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import javax.inject.Inject

class NoteSortingPreferencesSerializer @Inject constructor(
    moshi: Moshi
) : BaseJsonSerializer<NoteSortingPreferences>(adapter = moshi.adapter()) {

    override val defaultValue: NoteSortingPreferences
        get() = NoteSortingPreferences()
}