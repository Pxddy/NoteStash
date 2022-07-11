package com.ph.notestash.common

inline fun <reified T> tag(): String = T::class.java.simpleName