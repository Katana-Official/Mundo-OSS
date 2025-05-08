package lu.die.mundo;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net_62v.external.MetaActivationManager;
import net_62v.external.MetaActivityManager;
import net_62v.external.MetaApplicationInstaller;
import net_62v.external.MetaPackageManager;

public class AppTestCloneActivity extends AppCompatActivity {

    public static  Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        //canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void bindViewInner()
    {
        try{
            List<String> installedApps = MetaPackageManager.getInstalledInnerApps();
            if(installedApps == null)
            {
                return;
            }
            final ArrayList<String> stringArrayList = new ArrayList<>();
            final List<PackageInfo> pkgInfos2 = new ArrayList<>();
            for(String pkg : installedApps)
            {
                PackageInfo info = null;
                if(MetaPackageManager.isAppInstalledAsInternal(pkg))
                {
                    info = MetaPackageManager.getPackageInfo(pkg);
                }
                else info = getPackageManager().getPackageInfo(
                        pkg,
                        0
                );
                if(info == null) continue;
                try {
                    pkgInfos2.add(info);
                    String strAppName = info.applicationInfo.loadLabel(getPackageManager())
                            + info.packageName;
                    stringArrayList.add(strAppName);
                }catch (Exception e)
                {
                    e.printStackTrace();
                    stringArrayList.add(
                            info.packageName != null ?
                                    info.packageName : "Unknown"
                    );
                }
            }
            runOnUiThread(()->{
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.app_item);
                arrayAdapter.addAll(stringArrayList);
                ((ListView)findViewById(R.id.listCloneApp)).setAdapter(arrayAdapter);
                ((ListView)findViewById(R.id.listCloneApp))
                        .setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                if(position < 0 || position >= stringArrayList.size())
                                {
                                    return false;
                                }
                                new Thread()
                                {
                                    @Override
                                    public void run() {
                                        super.run();
                                        final String pkg = pkgInfos2.get(position)
                                                .packageName;
                                        try {
                                            MetaPackageManager.uninstallAppFully(pkg);
                                        } catch (Exception e) {
                                        }
                                        runOnUiThread(() -> Toast.makeText(AppTestCloneActivity.this
                                                , "Cleared "+pkg,Toast.LENGTH_SHORT)
                                                .show());
                                    }
                                }.start();
                                return true;
                            }
                        });
                ((ListView)findViewById(R.id.listCloneApp))
                        .setOnItemClickListener((parent, view, position, id) -> {
                            if(position < 0 || position >= stringArrayList.size())
                            {
                                return;
                            }
                            AlertDialog.Builder b = new AlertDialog.Builder(AppTestCloneActivity.this);
                            b.setTitle("Launch internal app");
                            PackageInfo info = pkgInfos2.get(position);
                            ApplicationInfo appInfo = info.applicationInfo;
                            if(appInfo == null) appInfo = new ApplicationInfo();
                            b.setMessage(
                                    info.toString()
                                            + appInfo.nativeLibraryDir
                                            + appInfo.toString()
                            );
                            b.setPositiveButton("Launch internal app", (dialog, which) -> {
                                Intent vxIntent = null;
                                try {
                                    vxIntent = MetaActivityManager.obtainSplashLaunchIntent(
                                            pkgInfos2.get(position).packageName,
                                            this
                                    );
                                } catch (Exception e) {
                                }
                                if(vxIntent != null)
                                {
                                    startActivity(vxIntent);
                                }
                            });
                            b.setNegativeButton("Cancel", (dialog, which) -> {
                                try{
                                    dialog.dismiss();
                                }catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            });
                            b.setNeutralButton("Delete",(dialog, which) -> {
                                new Thread()
                                {
                                    @Override
                                    public void run() {
                                        super.run();
                                        try{
                                            MetaPackageManager.uninstallAppFully(pkgInfos2.get(position)
                                                            .packageName);
                                            runOnUiThread(()->
                                            {
                                                Toast.makeText(
                                                        AppTestCloneActivity.this,
                                                        "Okay",
                                                        Toast.LENGTH_SHORT
                                                ).show();
                                            });
                                            bindViewInner();
                                        }catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                            });
                            b.show();
                        });
            });
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void bindView(String filter)
    {
        try{
            final boolean is64Bit;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                is64Bit = Process.is64Bit();
            }
            else is64Bit = true;
            final ArrayList<String> stringArrayList = new ArrayList<>();
            final List<PackageInfo> pkgInfos = getPackageManager().getInstalledPackages
                    (0);
            List<PackageInfo> pkgInfos2 = new ArrayList<>();
            for(PackageInfo info : pkgInfos)
            {
                try {
                    if(info.applicationInfo != null
                    && (info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
                        if(!info.packageName.startsWith("com.google") && !info.packageName.contains("vending")) continue;
                    if(info.applicationInfo != null
                            && info.applicationInfo.nativeLibraryDir != null)
                    {
                        if(info.applicationInfo.nativeLibraryDir.endsWith("64"))
                        {
                            if(!is64Bit)
                            {
                                continue;
                            }
                        }
                        else if(is64Bit) continue;
                    }
                    String strAppName = info.applicationInfo.loadLabel(getPackageManager())
                            + info.packageName;
                    if(!TextUtils.isEmpty(filter))
                    {
                        if(!strAppName
                                .toLowerCase(Locale.ROOT)
                                .contains(filter
                                        .toLowerCase(Locale.ROOT)))
                        {
                            continue;
                        }
                    }
                    pkgInfos2.add(info);
                    stringArrayList.add(strAppName);
                }catch (Exception e)
                {
                    e.printStackTrace();
                    stringArrayList.add(
                            info.packageName != null ?
                                    info.packageName : "Unknown"
                    );
                }
            }
            pkgInfos.clear();
            pkgInfos.addAll(pkgInfos2);
            runOnUiThread(()->{
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.app_item);
                arrayAdapter.addAll(stringArrayList);
                ((ListView)findViewById(R.id.listCloneApp)).setAdapter(arrayAdapter);
                ((ListView)findViewById(R.id.listCloneApp))
                        .setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                if(position < 0 || position >= stringArrayList.size())
                                {
                                    return false;
                                }
                                new Thread()
                                {
                                    @Override
                                    public void run() {
                                        super.run();
                                        final String pkg = pkgInfos.get(position)
                                                .packageName;
                                        try {
                                            MetaPackageManager.uninstallAppFully(pkg);
                                        } catch (Exception e) {
                                        }
                                        runOnUiThread(() -> Toast.makeText(AppTestCloneActivity.this
                                                , "Cleaned "+pkg,Toast.LENGTH_SHORT)
                                                .show());
                                    }
                                }.start();
                                return true;
                            }
                        });
                ((ListView)findViewById(R.id.listCloneApp))
                        .setOnItemClickListener((parent, view, position, id) -> {
                            if(position < 0 || position >= stringArrayList.size())
                            {
                                return;
                            }
                            AlertDialog.Builder b = new AlertDialog.Builder(AppTestCloneActivity.this);
                            b.setTitle("Select to run");
                            PackageInfo info = pkgInfos.get(position);
                            ApplicationInfo appInfo = info.applicationInfo;
                            if(appInfo == null) appInfo = new ApplicationInfo();
                            b.setMessage(
                                    info.toString()
                                    + appInfo.nativeLibraryDir
                                    + appInfo.toString()
                            );
                            b.setPositiveButton("Launch", (dialog, which) -> {
                                try {
                                    MetaActivityManager.launchApp(
                                            pkgInfos.get(position)
                                                    .packageName);
                                } catch (Exception e) {
                                }
                            });
                            b.setNegativeButton("Cancel", (dialog, which) -> {
                                try{
                                    dialog.dismiss();
                                }catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            });
                            b.show();
                        });
            });
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private boolean sw = false;

    private String currentActivationCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_test_clone);
        new Thread()
        {
            @Override
            public void run() {
                super.run();
                bindView("");
            }
        }.start();
        findViewById(R.id.btnActivation)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder b = new AlertDialog.Builder(AppTestCloneActivity.this);
                                final EditText editText = new EditText(AppTestCloneActivity.this);
                                editText.setText(currentActivationCode);
                                b.setView(editText);
                                try {
                                    b.setMessage(MetaActivationManager.getActivationMessage());
                                } catch (Exception e) {
                                }
                                String currentStatus = "Activate (";
                                try {
                                    if(MetaActivationManager.getActivationStatus())
                                        currentStatus += "Activated";
                                    else currentStatus += "Not Activated";
                                } catch (Exception e) {
                                }
                                currentStatus += ")";
                                b.setPositiveButton(currentStatus, (dialogInterface, i) ->{
                                    String currentTxt = editText.getText().toString();
                                    try {
                                        MetaActivationManager.activateSdk(currentTxt);
                                    } catch (Exception e) {
                                    }
                                    currentActivationCode = currentTxt;
                                });
                                b.show();
                            }
                        }
                );
        findViewById(R.id.btnKillApp)
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new Thread()
                                {
                                    @Override
                                    public void run() {
                                        super.run();
                                        try {
                                            MetaActivityManager.killAllApps();
                                        } catch (Exception e) {
                                        }
                                    }
                                }.start();
                                Toast.makeText(AppTestCloneActivity.this
                                        , "Done",Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                );
        findViewById(R.id.btnInstallInternal)
                .setOnClickListener(v -> {
                    installXapk = false;
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent,10010);
                });
        findViewById(R.id.btnInstallXapkInternal)
                .setOnClickListener(v -> {
                    installXapk = true;
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent,10010);
                });
        findViewById(R.id.btnInstallInternal)
                .setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        new Thread()
                        {
                            @Override
                            public void run() {
                                super.run();
                                if(sw)
                                {
                                    bindView("");
                                }
                                else
                                {
                                    bindViewInner();
                                }
                                sw = !sw;
                            }
                        }.start();
                        return true;
                    }
                });
        ((SearchView)findViewById(R.id.searchClone))
                .setOnQueryTextListener(
                        new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {
                                new Thread()
                                {
                                    @Override
                                    public void run() {
                                        super.run();
                                        bindView(newText);
                                    }
                                }.start();
                                return false;
                            }
                        }
                );
        try{
            Intent serviceIntent = new Intent(
                    this,
                    MyService.class
            );
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                startForegroundService(serviceIntent);
            else startService(serviceIntent);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private boolean installXapk = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10010 && resultCode == RESULT_OK && data != null)
        {
            Toast.makeText(
                    AppTestCloneActivity.this,
                    "Trying to install...",
                    Toast.LENGTH_SHORT
            ).show();
            new Thread()
            {
                @Override
                public void run() {
                    super.run();
                    try{
                        Uri uri = data.getData();
                        final File fmTargetFile = new File(
                                getDir("temp_apk", MODE_PRIVATE),
                                "temp.apk"
                        );
                        if(fmTargetFile.exists())fmTargetFile.delete();
                        FileOutputStream fos = new FileOutputStream(fmTargetFile);
                        InputStream is = getContentResolver().openInputStream(uri);
                        byte[] b = new byte[1024];
                        int length;
                        long total = 0;
                        while((length = is.read(b)) > 0){
                            fos.write(b,0,length);
                            total += length;
                        }
                        is.close();
                        fos.close();
                        long finalTotal = total;
                        runOnUiThread(
                                () -> Toast.makeText(
                                        AppTestCloneActivity.this,
                                        "Apk file(s) copied to storage, size of apk file(s) -> "+ finalTotal,
                                        Toast.LENGTH_SHORT
                                ).show()
                        );
                        int nResult = -1;
                        if(installXapk)
                        {
                            nResult = MetaApplicationInstaller.installAppsByPath(
                                    fmTargetFile.getAbsolutePath()
                            );
                        }
                        else
                        {
                            nResult = MetaApplicationInstaller.installAppByPath(
                                    fmTargetFile.getAbsolutePath()
                            );
                        }
                        final int result = nResult;
                        runOnUiThread(
                                () -> Toast.makeText(
                                        AppTestCloneActivity.this,
                                        "Result -> "+result,
                                        Toast.LENGTH_SHORT
                                ).show()
                        );
                        fmTargetFile.delete();
                        bindViewInner();
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }
}