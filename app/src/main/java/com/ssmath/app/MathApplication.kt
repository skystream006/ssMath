package com.ssmath.app

import android.app.Activity
import android.app.Application
import android.os.Bundle

class MathApplication : Application() {
    val uiActivity = UiActivityGate()

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityResumed(activity: Activity) = uiActivity.activityResumed(activity)
            override fun onActivityPaused(activity: Activity) = uiActivity.activityPaused(activity)
            override fun onActivityDestroyed(activity: Activity) = uiActivity.activityPaused(activity)
            override fun onActivityCreated(activity: Activity, state: Bundle?) = Unit
            override fun onActivityStarted(activity: Activity) = Unit
            override fun onActivityStopped(activity: Activity) = Unit
            override fun onActivitySaveInstanceState(activity: Activity, state: Bundle) = Unit
        })
        DebugLog.initialize(this)
        DebugLog.event(DebugEvent.APP_STARTED)
    }
}
