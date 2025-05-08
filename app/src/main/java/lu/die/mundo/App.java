package lu.die.mundo;

import android.app.ActivityThread;
import android.app.Application;
import android.content.Context;

//import de.robv.android.xposed.XC_MethodHook;
//import de.robv.android.xposed.XposedHelpers;
import net_62v.external.IMundoProcessCallback;
import net_62v.external.MetaCore;

public class App extends Application implements IMundoProcessCallback {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MetaCore.setProcessLifecycleCallback(this);
    }

    @Override
    public void onCreate(String packageName, String processName, String splitRegion) {
        try
        {
            final var elApplicacion = ActivityThread.currentApplication();
            final var cl = elApplicacion.getClassLoader();
            // Hook scope
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
