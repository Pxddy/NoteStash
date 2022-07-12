package com.ph.notestash.common.recyclerview.swipe

interface Swipeable {
    val canSwipe: Boolean
        get() = true

    fun onSwiped(direction: Int)
}