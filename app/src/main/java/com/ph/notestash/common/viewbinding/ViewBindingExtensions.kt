package com.ph.notestash.common.viewbinding

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import timber.log.Timber
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

// View binding for Activity
@Suppress("unused")
inline fun <reified ViewBindingT : ViewBinding> Activity.viewBinding(
    noinline viewBindFactory: (View) -> ViewBindingT
) = ActivityBindingProperty(viewBindFactory = viewBindFactory)

class ActivityBindingProperty<ViewBindingT : ViewBinding>(
    val viewBindFactory: (View) -> ViewBindingT
) : ViewBindingProperty<Activity, ViewBindingT>() {

    override fun createBinding(thisRef: Activity): ViewBindingT {
        val view = thisRef.findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
        return viewBindFactory(view)
    }
}


// View binding for Fragment
@Suppress("unused")
inline fun <reified T : ViewBinding> Fragment.viewBinding(noinline factory: (View) -> T) =
    FragmentBindingProperty(factory)

class FragmentBindingProperty<ViewBindingT : ViewBinding>(val factory: (View) -> ViewBindingT) :
    ViewBindingProperty<Fragment, ViewBindingT>() {

    override fun createBinding(thisRef: Fragment): ViewBindingT {
        thisRef.parentFragmentManager.registerFragmentLifecycleCallbacks(
            FragmentViewDestroyedListener(thisRef),
            false
        )
        return factory(thisRef.requireView())
    }

    private inner class FragmentViewDestroyedListener(private val fragment: Fragment) :
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

abstract class ViewBindingProperty<in Ref : Any, out ViewBindingT : ViewBinding> :
    ReadOnlyProperty<Ref, ViewBindingT> {
    private var binding: ViewBindingT? = null

    fun clearBinding() {
        binding = null
    }

    @MainThread
    override fun getValue(thisRef: Ref, property: KProperty<*>): ViewBindingT =
        binding ?: createBinding(thisRef).also {
            Timber.v("Created view binding=%s for %s", it, thisRef)
            binding = it
        }

    internal abstract fun createBinding(thisRef: Ref): ViewBindingT
}