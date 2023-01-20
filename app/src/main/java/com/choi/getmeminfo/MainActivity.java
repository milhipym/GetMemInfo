package com.choi.getmeminfo;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.choi.getmeminfo.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static Context mContext;
    private static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1;
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private TextView textView, textView2, textView3, textView4;
    private Button sv_on_bt, sv_off_bt;
    private boolean roof = false;
    boolean operation = false;
    CheckPackageNameThread checkPackageNameThread;
    public static String folderpath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mContext = this.getApplicationContext();
        setUi();
/*        getMemInfo();
        checkPermission();
        Intent PermissionIntent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS, Uri.parse("package:" + getPackageName()));
        startActivity(PermissionIntent);
        operation = true;
        checkPackageNameThread = new CheckPackageNameThread();
        checkPackageNameThread.start();

        PackageManager pm = this.getPackageManager();
        boolean installed = false;
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo packageInfo : packages)  {
            Log.e("YYYM", "PackageManager: "+packageInfo.packageName
            + ", versionName: "+packageInfo.versionName
            + ", sourceDir:: "+packageInfo.applicationInfo.sourceDir);
            Log.d("Test", "packageName: ${info.packageName}"
                    + ", versionName: ${info.versionName}"
                    + ", lastUpdateTime: ${info.lastUpdateTime}"
                    + ", targetSdk: ${info.applicationInfo.targetSdkVersion}"
                    + ", minSdk: ${info.applicationInfo.minSdkVersion}"
                    + ", sourceDir: ${info.applicationInfo.sourceDir}"
                    + ", uid: ${info.applicationInfo.uid}"
                    + ", label: ${info.applicationInfo.loadLabel(packageManager)}")
        }*/
    }

    private void setUi() {
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);
        sv_on_bt = findViewById(R.id.bt_on);
        sv_off_bt = findViewById(R.id.bt_off);
        //MyService myService = new MyService(mContext);
        folderpath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        //onProccessCheck();
        sv_on_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(MainActivity.this, MyService.class));
                //checkPermissionAndStartService();
            }
        });
    }

    public void checkPermissionAndStartService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (!Settings.canDrawOverlays(this)){
                Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:"+ getPackageName()));
                startActivityForResult(i, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            } else {
                startService(new Intent(MainActivity.this, MyService.class));
            }
            startService(new Intent(MainActivity.this, MyService.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // TODO 동의를 얻지 못했을 경우의 처리

            } else {
                startService(new Intent(MainActivity.this, MyService.class));
            }
        }
    }

    private void onProccessCheck()
    {
        ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appList = am.getRunningAppProcesses();

        List<ActivityManager.RecentTaskInfo> recentTasks = am.getRecentTasks(5, ActivityManager.RECENT_WITH_EXCLUDED);
        for (ActivityManager.RecentTaskInfo task : recentTasks) {
            Intent taskIntent = task.baseIntent;
            Log.d("Recent Task", "Intent: " + taskIntent);
            Log.d("Recent Task", "Persistent ID: " + task.persistentId);
            Log.d("Recent Task", "ID: " + task.id);
        }
        String name = "";
        Log.e("YYYM", "onProccessCheck: ....................");
        for (int i=0; i<appList.size(); i++) {
            ActivityManager.RunningAppProcessInfo rapi = appList.get(i);
            name += "#"+i+"_"+rapi.processName;

            Log.e("YYYM", "rapi.processName: "+rapi.processName);
            Log.e("YYYM", "rapi.importance: "+rapi.importance);
        }
        textView4.setText(name);
    }

    private void getMemInfo() {
        Log.e("YYYM", "getMemInfo()");

        //Ram 사용량 구하기
        ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();

        activityManager.getMemoryInfo(memoryInfo);
        //전체 RAM용량
        double totalMem = memoryInfo.totalMem;  //API 16부터
        //사용가능한 RAM용량
        double availMem = memoryInfo.availMem;
        double availMems = memoryInfo.availMem/0x100000L;
        Log.e("YYYM", "availMems: "+availMems);
        double percentAvail = memoryInfo.availMem / (double)memoryInfo.totalMem * 100.0;
        Log.e("YYYM", "percentAvail: "+percentAvail);

        boolean lowMem =  memoryInfo.lowMemory;

        double aa = totalMem/1024;
        double bb = aa/1024;
        double cc = bb/1024;
        Log.e("YYYM", "aa:"+aa+" ,bb="+bb + " , cc="+cc);
        double dd = availMem /1024;
        double ee = dd/1024;
        double ff = ee/1024;
        Log.e("YYYM", "dd="+dd+" , ee:"+ee+ " ,ff="+ff);
        Runtime runtime = Runtime.getRuntime();
        final long usedMemInMB=(runtime.totalMemory() - runtime.freeMemory()) / 1048576L;
        final long maxHeapSizeInMB=runtime.maxMemory() / 1048576L;
        final long availHeapSizeInMB = maxHeapSizeInMB - usedMemInMB;
        Log.e("YYYM", "usedMemInMB: "+usedMemInMB+", maxHeapSizeInMB:"+maxHeapSizeInMB+ ", availHeapSizeInMB:"+availHeapSizeInMB);

        textView.setText("토탈_\n"+totalMem);
        textView2.setText("사용가능_\n"+availMem);
/*        textView2.setText("Runtime Max Memory = " + runtime.maxMemory() + "\n" +
                     "Runtime Total Memory = " + runtime.totalMemory() + "\n"+
                     "Runtime Free Memory = " + runtime.freeMemory());*/
        textView3.setText("로우메모리_\n"+lowMem);

        Log.e("YYYM", "totalMem:"+totalMem+" , availMem:"+availMem+" , lowMem:"+lowMem);

        //사용량
        DecimalFormat df = new DecimalFormat("#,###");
        double ram = 100*(totalMem-availMem)/totalMem;
        String result =  df.format(ram)+"%";
    }


    @Override
    protected void onResume() {
        super.onResume();
        //roof = false;
        //getMemInfo();
        onProccessCheck();
    }

    @Override
    protected void onStop() {
        super.onStop();
/*        roof = true;
        while (true)
        {
            Log.e("YYYM", "onStop: "+roof);
            getMemInfo();
            if (roof == false) {break;}
        }*/
    }


    // 현재 포그라운드 앱 패키지 로그로 띄우는 함수
    private class CheckPackageNameThread extends Thread{

        public void run(){
            // operation == true 일때만 실행
            while(operation){

                // 현재 포그라운드 앱 패키지 이름 가져오기
                //System.out.println(getPackageName(getApplicationContext()));
                Log.e("YYYM", "run: "+getPackageName(getApplicationContext()) );
                try {
                    // 2초마다 패키치 이름을 로그창에 출력
                    sleep(2000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 현재 포그라운드 앱 패키지를 가져오는 함수
    public static String getPackageName(@NonNull Context context) {

        // UsageStatsManager 선언
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

        // 마지막 실행 앱 타임스탬프
        long lastRunAppTimeStamp = 0L;

        // 얼마만큼의 시간동안 수집한 앱의 이름을 가져오는지 정하기 (begin ~ end 까지의 앱 이름을 수집한다)
        final long INTERVAL = 1000 * 60 * 5;
        final long end = System.currentTimeMillis();
        final long begin = end - INTERVAL; // 5분전


        LongSparseArray packageNameMap = new LongSparseArray<>();

        // 수집한 이벤트들을 담기 위한 UsageEvents
        final UsageEvents usageEvents = usageStatsManager.queryEvents(begin, end);

        // 이벤트가 여러개 있을 경우 (최소 존재는 해야 hasNextEvent가 null이 아니니까)
        while (usageEvents.hasNextEvent()) {

            // 현재 이벤트를 가져오기
            UsageEvents.Event event = new UsageEvents.Event();
            usageEvents.getNextEvent(event);

            // 현재 이벤트가 포그라운드 상태라면(현재 화면에 보이는 앱이라면)
            //if(isForeGroundEvent(event)) {

                // 해당 앱 이름을 packageNameMap에 넣는다.
                packageNameMap.put(event.getTimeStamp(), event.getPackageName());

                // 가장 최근에 실행 된 이벤트에 대한 타임스탬프를 업데이트 해준다.
                if(event.getTimeStamp() > lastRunAppTimeStamp) {
                    lastRunAppTimeStamp = event.getTimeStamp();
                }
            //}
        }
        // 가장 마지막까지 있는 앱의 이름을 리턴해준다.
        return packageNameMap.get(lastRunAppTimeStamp, "").toString();
    }

    // 앱이 포그라운드 상태인지 체크
    private static boolean isForeGroundEvent(UsageEvents.Event event) {

        // 이벤트가 없으면 false 반환
        if(event == null)
            return false;

        // 이벤트가 포그라운드 상태라면 true 반환
        if(BuildConfig.VERSION_CODE >= 29)
            return event.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED;

        return event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND;
    }

    // 권환 체크
    private boolean checkPermission(){

        boolean granted = false;

        AppOpsManager appOps = (AppOpsManager) getApplicationContext()
                .getSystemService(Context.APP_OPS_SERVICE);

        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getApplicationContext().getPackageName());

        if (mode == AppOpsManager.MODE_DEFAULT) {
            granted = (getApplicationContext().checkCallingOrSelfPermission(
                    android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
        }
        else {
            granted = (mode == AppOpsManager.MODE_ALLOWED);
        }

        return granted;
    }
}