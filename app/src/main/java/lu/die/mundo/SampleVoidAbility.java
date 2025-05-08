package lu.die.mundo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import net_62v.external.MetaActivationManager;
import net_62v.external.MetaActivityManager;
import net_62v.external.MetaCore;

public class SampleVoidAbility extends Activity {
    private void scheduleEnqueueNewIntent(final Intent intent)
    {
        try {
            getPackageManager().getInstallerPackageName("com.whatsapp");
            final var nowadays = System.currentTimeMillis();
            while(!MetaActivationManager.getActivationStatus() && ((System.currentTimeMillis() - nowadays) < 5000L))
            {
                Thread.yield();
            }
            if(!MetaActivationManager.getActivationStatus())
            {
                runOnUiThread(()-> Toast.makeText(this,"Initialization failed", Toast.LENGTH_SHORT).show());
                return;
            }
            MetaActivityManager.launchApp("com.whatsapp");
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        new Thread(()-> scheduleEnqueueNewIntent(intent)).start();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final var intent = getIntent();
        MetaCore.registerCoreCallback(()->new Thread(()-> scheduleEnqueueNewIntent(intent)).start());
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
