package com.ph.notestash.common.viewbinding

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import timber.log.Timber

// View binding for Activity
fun <ViewBindingT : ViewBinding> Activity.viewBinding(
    viewBindFactory: (View) -> ViewBindingT
): Lazy<ViewBindingT> = ActivityBindingProperty(
    activity = this,
    viewBindFactory = viewBindFactory
)


// View binding for Fragment
fun <ViewBindingT : ViewBinding> Fragment.viewBinding(
    viewBindFactory: (View) -> ViewBindingT
): Lazy<ViewBindingT> = FragmentBindingProperty(
    fragment = this,
    viewBindFactory = viewBindFactory
)

private class ActivityBindingProperty<ViewBindingT : ViewBinding>(
    private val activity: Activity,
    private val viewBindFactory: (View) -> ViewBindingT
) : ViewBindingProperty<ViewBindingT>() {

    override fun createBinding(): ViewBindingT {
        val view = activity.findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
        return viewBindFactory(view)
    }
}

private class FragmentBindingProperty<ViewBindingT : ViewBinding>(
    private val fragment: Fragment,
    private val viewBindFactory: (View) -> ViewBindingT
) : ViewBindingProperty<ViewBindingT>() {

    override fun createBinding(): ViewBindingT {
        fragment.parentFragmentManager.registerFragmentLifecycleCallbacks(
            FragmentViewDestroyedListener(),
            false
        )
        return viewBindFactory(fragment.requireView())
    }

    private inner class FragmentViewDestroyedListener :
        FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
            super.onFragmentViewDestroyed(fm, f)
            if (fragment === f) {
                Timber.v("Fragment %s view destroyed! Clearing view binding", fragment)
                clearBinding()
                fm.unregisterFragmentLifecycleCallbacks(this)
            }
        }
    }
}

private abstract class ViewBindingProperty<out ViewBindingT : ViewBinding> : Lazy<ViewBindingT> {
    private var binding: ViewBindingT? = null

    override val value: ViewBindingT
        get() = getBinding()

    override fun isInitialized(): Boolean = binding != null

    fun clearBinding() {
        binding = null
    }

    @MainThread
    private fun getBinding(): ViewBindingT = binding ?: createBinding().also {
        Timber.v("Created view binding=%s", it)
        binding = it
    }

    abstract fun createBinding(): ViewBindingT
}