package com.ph.notestash.common.fragment

import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController

/**
 * Navigate to specified [NavDirections]
 */
fun Fragment.navigateTo(navDirections: NavDirections) = findNavController().navigate(navDirections)

/**
 * Attempts to remove the fragment from the back stack
 */
fun Fragment.popBackStack() = findNavController().popBackStack()