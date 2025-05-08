package lu.die.mundo

import android.app.Activity
import android.app.ActivityThread
import android.app.Application.ActivityLifecycleCallbacks
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import net_62v.external.IMundoProcessCallback
import net_62v.external.MetaActivationManager
import net_62v.external.MetaActivityManager
import net_62v.external.MetaApplicationInstaller
import net_62v.external.MetaCore
import net_62v.external.MetaPackageManager
import net_62v.external.MetaStorageManager

class APISample {
    fun sampleActivation()
    {
        // Activate app with activation code
        MetaActivationManager.activateSdk("ABCDEFGHIJKLMN")

        // Check the activation status, true means success
        MetaActivationManager.getActivationStatus()

        // Get the latest activation message (validate period)
        MetaActivationManager.getActivationMessage()
    }
    fun sampleAction()
    {
        // Delete all installed software
        //MetaActivityManager.factoryReset()

        // Initialize circumstance
        // MetaActivityManager.initialize(context)

        // Kill all background apps
        MetaActivityManager.killAllApps()

        // Preload app automatically, not necessary
        MetaActivityManager.acquirePreloadNextProcess()

        // Query and consult an app is running
        MetaActivityManager.isAppRunning("a.c.c.c", 0)

        // Kill the specific app with username, param (username, package name)
        MetaActivityManager.killAppByPkg(0, "a.b.c.d")

        // Launch app with specific user name, param (username, package name)
        MetaActivityManager.launchApp(0, "a.b.c.d")

        // Launch a specific intent with user name, param (intent, username)
        MetaActivityManager.launchIntent(Intent().setComponent(ComponentName(
            "a.b.c.d","a.b.c.d.Activity"
        )), 0)

        // Query and consult if a package is installed
        //MetaActivityManager.isInnerPackageInstalled("a.b.c.d")

        // Set a default user name, can be alphabetic or numeric.
        //MetaActivityManager.setUserName("0")
    }
    fun samplePackageAction()
    {
        // Clone app manually, app will be cloned automatically after its first launch
        // Directly starting (or launching) will not reuse the original application information after system or application upgrades.
        // After uninstalling the local application, the cloned application data will be deleted. If you start or launch directly without cloning, the data will not be deleted
        MetaApplicationInstaller.cloneApp("a.b.c.d")

        // Installing apk inside without system installation, supports Xposed plugin, incoming path file will be copied and installed
        MetaApplicationInstaller.installAppByPath("/sdcard/app.apk")

        // Analyze the cause of installation failure. If installLocalPackage and installSync return a value greater than or equal to 0, it means success; if it is less than 0, it means failure
        // Pass in a value less than 0 to resolve the failure reason, which is used for debugging and reminding users.
        MetaApplicationInstaller.convertResultToString(-5)

        // Get the package information of the specified package name
        MetaPackageManager.getPackageInfo("a.b.c.d")

        // Get the installed app list (package info)
        MetaPackageManager.getInstalledInnerApps()

        // Create a new user. By default, a user with a default user name will be created after the installation is completed
        MetaPackageManager.createEmptyUser("a.b.c.d", 0)

        // Delete app cache
        //MetaPackageManager.deleteAppCache("a.b.c.d")

        // Get all user names of installed apps, e.g. [0, 1, 2, 3, 4]
        MetaPackageManager.getInstalledUserId("a.b.c.d")

        // Query activities
        MetaPackageManager.queryIntentActivities(Intent("Intent you want to query"), null, 0)

        // Query services
        MetaPackageManager.queryIntentServices(Intent("Intent you want to query"), null, 0)

        // Query providers
        MetaPackageManager.queryIntentProviders(Intent("Intent you want to query"), null, 0)

        // Query receivers
        MetaPackageManager.queryIntentReceivers(Intent("Intent you want to query"), null, 0)

        // Uninstall a specific app with package name
        MetaPackageManager.uninstallAppFully("a.b.c.d")

        // Delete a specific user from installed app, param (package name, user name)
        MetaPackageManager.cleanPackageDataAsUser("a.b.c.d", 0)

        // Query is an app was installed inside.
        MetaPackageManager.isInnerAppInstalled("a.b.c.d")

        // Get external storage path by package name, for obb folder copying etc.
        // Pass a package name, and obtain scoped storage sd card path then.
        MetaStorageManager.obtainAppExternalStorageDir("a.b.c.d")

        // Obtain application info of a specific app
        MetaPackageManager.getApplicationInfo("a.b.c.d")

        // Delete all app's cache
        //MetaPackageManager.deleteAllAppCache()

        // Monitoring system services, such as package and Context.CLIPBOARD_SERVICE etc.
        // It needs to be called after the application is loaded. Please refer to the next API for details
        MetaCore.addServiceInterpreter(
            "package",
            "getPackageInfo"
        ) { p0, p1, p2 -> p1.invoke(p0, p2) }

        // Add application activity callback. The premium SDK will call back all activity actions. The lite and plus SDK only calls back afterApplicationCreate
        MetaCore.setProcessLifecycleCallback(
            object : IMundoProcessCallback, ActivityLifecycleCallbacks
            {
                override fun onCreate(packageName: String, processName : String) {
                    // Monitor system services or load other hooks here. This will call back when the application is loaded. The parameters are package name and process name.

                    // Example of modifying location
                    MetaCore.addServiceInterpreter(
                        Context.LOCATION_SERVICE,
                        "getLastLocation"
                    ) { p0, p1, p2 ->
                        Location(
                            "gps"
                        ).apply {
                            latitude = 23.456
                            longitude = 123.456
                        }
                    }

                    // Register application callbacks here
                    ActivityThread.currentApplication().registerActivityLifecycleCallbacks(
                        this
                    )
                }

                override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                    // Take actions here
                    activity.finish()
                }
                override fun onActivityStarted(activity: Activity) {
                    // Take actions here
                    activity.finish()
                }
                override fun onActivityResumed(activity: Activity) {
                }
                override fun onActivityPaused(activity: Activity) {
                }
                override fun onActivityStopped(activity: Activity) {
                }
                override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                }
                override fun onActivityDestroyed(activity: Activity) {
                    // Take actions here
                    activity.setResult(200, Intent("LOGIN_DATA"))
                }

            }
        )
        // Process type callback, which needs to be called before MetaCore.attachMetaBase(base) or MetaActivityManager.initialize
        MetaCore.setProcessLifecycleCallback(
            object : IMundoProcessCallback
            {
                override fun onInitialized() {
                    // Cloned or internal app process
                    // Example of using Xposed
//                    XposedHelpers.findAndHookMethod(
//                        Activity::class.java,
//                        "onCreate",
//                        Bundle::class.java,
//                        object : XC_MethodHook()
//                        {
//                            override fun beforeHookedMethod(p0: MethodHookParam) {
//                                super.beforeHookedMethod(p0)
//                            }
//                        }
//                    )
                }
            }
        )
    }
    fun sampleCoreAction()
    {
        // Wait for the internal information to be loaded. It is recommended to process it in the Activity onCreate(Bundle), and the callback will be called in a new thread
        MetaCore.registerCoreCallback {
            // do something
        }
    }
}
