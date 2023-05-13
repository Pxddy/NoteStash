package com.ph.notestash.common.uuid

import java.util.*
import javax.inject.Inject

class UUIDProvider @Inject constructor() {

    val uuid: String
        get() = UUID.randomUUID().toString()
}