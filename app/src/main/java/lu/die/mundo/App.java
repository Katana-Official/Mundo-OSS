package lu.die.mundo;

import android.app.ActivityThread;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;

//import de.robv.android.xposed.XC_MethodHook;
//import de.robv.android.xposed.XposedHelpers;
//import de.robv.android.xposed.IXposedHookLoadPackage;
import net_62v.external.IMundoProcessCallback;
import net_62v.external.MetaCore;
//import top.canyie.pine.xposed.PineXposed;

public class App extends Application implements IMundoProcessCallback {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MetaCore.setProcessLifecycleCallback(this);
    }

    @Override
    public void onInitialized() {
        IMundoProcessCallback.super.onInitialized();
        // Load your xposed modules here
        // PineXposed.loadModule("Module file");
        // Or simply load all existing modules
        final var allApplications = getBaseContext().getPackageManager().getInstalledApplications(
                PackageManager.GET_META_DATA
        );
        for(final var item : allApplications)
        {
            final var metaData = item.metaData;
            if(metaData != null && metaData.containsKey("xposedmodule"))
            {
                // This is an Xposed module, we should load it by the source directory param.
                // PineXposed.loadModule(item.sourceDir);
            }
        }
    }

    @Override
    public void onCreate(String packageName, String processName, String splitRegion) {
        try
        {
            final var elApplicacion = ActivityThread.currentApplication();
            final var cl = elApplicacion.getClassLoader();
            /**
             * You need to initialize the Xposed modules here,
             * use onPackageLoad to initialize all Xposed modules.
             */
//            PineXposed.onPackageLoad(
//                    packageName,
//                    processName,
//                    elApplicacion.getApplicationInfo(),
//                    true,
//                    cl
//            );
            // Custom hook scope
            /*XposedHelpers.findAndHookMethod(
                    android.app.Activity.class,
                    "onCreate",
                    android.os.Bundle.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                        }
                    }
            );*/
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
