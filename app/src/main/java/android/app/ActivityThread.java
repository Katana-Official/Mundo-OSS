package android.app;

import android.app.assist.AssistStructure;
import android.content.res.Configuration;
import android.os.IBinder;
import android.os.Looper;
import android.util.ArrayMap;

import androidx.annotation.Keep;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Keep
public class ActivityThread {
    //private static volatile IPermissionManager sPermissionManager;
    //final ApplicationThread mAppThread = new ApplicationThread();
    Looper mLooper = Looper.myLooper();
    //final H mH = new H();
    //final Executor mExecutor = new HandlerExecutor(mH);
    //final ArrayMap<IBinder, ActivityClientRecord> mActivities = new ArrayMap<>();
    // List of new activities (via ActivityRecord.nextIdle) that should
    // be reported when next we idle.
    //ActivityClientRecord mNewActivities = null;
    // Number of activities that are currently visible on-screen.
    int mNumVisibleActivities = 0;
    private final AtomicInteger mNumLaunchingActivities = new AtomicInteger();
    //private int mLastProcessState = PROCESS_STATE_UNKNOWN;
    //private int mPendingProcessState = PROCESS_STATE_UNKNOWN;
    ArrayList<WeakReference<AssistStructure>> mLastAssistStructures = new ArrayList<WeakReference<AssistStructure>>();
    private int mLastSessionId;
    ArrayMap<IBinder, Service> mServices = new ArrayMap<IBinder, Service>();
    //Profiler mProfiler;
    int mCurDefaultDisplayDpi;
    boolean mDensityCompatMode;
    Configuration mConfiguration;
    Configuration mCompatConfiguration;
    Application mInitialApplication;
    final ArrayList<Application> mAllApplications
            = new ArrayList<Application>();
    public static ActivityThread currentActivityThread() {
        return null;
    }
    public static String currentProcessName() {
        return null;
    }
    public static Application currentApplication() {
        ActivityThread am = currentActivityThread();
        return am != null ? am.mInitialApplication : null;
    }
    public Instrumentation getInstrumentation()
    {
        return null;
    }
    public Looper getLooper() {
        return mLooper;
    }

    
    void sendMessage(int what, Object obj) {

    }

    
    public Activity getActivity(IBinder token) {
        return null;
    }

    
    public void updatePendingConfiguration(Configuration config) {

    }

    
    public void updateProcessState(int processState, boolean fromIpc) {

    }

    
    public boolean isHandleSplashScreenExit(IBinder token) {
        return false;
    }
    public void handleConfigurationChanged(Configuration config) {

    }

    
    public void removeLaunchingActivity(IBinder token) {

    }

    public static String currentPackageName() {
        return null;
    }
}
