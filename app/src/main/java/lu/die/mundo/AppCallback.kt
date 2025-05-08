package lu.die.mundo

import android.app.Activity
import android.app.ActivityThread
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import net_62v.external.IMundoProcessCallback

object AppCallback : IMundoProcessCallback, ActivityLifecycleCallbacks {
    private val TAG = AppCallback.javaClass.canonicalName
    override fun onCreate(p0: String?, p1: String?) {
        // Detect app package name by the first param.
        ActivityThread.currentApplication().registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        // ......
        android.util.Log.d(TAG, "Activity created -> ${p0.javaClass}")
    }

    override fun onActivityStarted(p0: Activity) {
    }

    override fun onActivityResumed(p0: Activity) {
        // ......
        android.util.Log.d(TAG, "Activity resumed -> ${p0.javaClass}")
    }

    override fun onActivityPaused(p0: Activity) {
    }

    override fun onActivityStopped(p0: Activity) {
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(p0: Activity) {
        // ......
        android.util.Log.d(TAG, "Activity destroyed -> ${p0.javaClass}")
    }
}