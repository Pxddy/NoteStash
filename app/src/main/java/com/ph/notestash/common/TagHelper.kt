package com.ph.notestash.common

inline fun <reified T> fragmentTag(): String = T::class.java.name