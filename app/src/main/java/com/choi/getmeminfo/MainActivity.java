package com.choi.getmeminfo;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.util.Log;
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

    private static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1;
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private TextView textView, textView2, textView3, textView4;
    private Button sv_on_bt, sv_off_bt;
    private boolean roof = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setUi();
        //getMemInfo();
    }

    private void setUi() {
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);
        sv_on_bt = findViewById(R.id.bt_on);
        sv_off_bt = findViewById(R.id.bt_off);
        onProccessCheck();
        sv_on_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndStartService();
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
        String name = "";
        Log.e("YYYM", "onProccessCheck: ...................." );
        for (int i=0; i<appList.size(); i++) {
            ActivityManager.RunningAppProcessInfo rapi = appList.get(i);
            name += "#"+i+"_"+rapi.processName;

            Log.e("YYYM", "onProccessCheck: "+rapi.importance);
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
}