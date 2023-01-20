package com.choi.getmeminfo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MyService extends Service implements View.OnTouchListener{
    private String TAG = "MyService";

    private View onTopView;
    private WindowManager manager;
    public Context mContext;

    private boolean stopThread = false;

    private String foldername = "TESTLOG";
    private String filename = "logfile.txt";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("YYYM", "onCreate: jkashfjklhasjkfajdhfkjlshdf");
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        onTopView = inflater.inflate(R.layout.always_on_top_layout, null);
        onTopView.setOnTouchListener(this);

        int LAYOUT_FLAG;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else{
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.LEFT | Gravity.TOP;

        manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        manager.addView(onTopView, params);

        Button closeBtn = onTopView.findViewById(R.id.close_this_window);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.removeView(onTopView);
                onTopView = null;
                stopThread = true;
                stopSelf();
            }
        });
        //Log.e("YYYM", "getLogcatLog: "+getLogcatLog());
        BackLogThread thread = new BackLogThread();
        thread.setDaemon(true);
        thread.start();
        //getLogcatLog();
    }

    float xpos = 0;
    float ypos = 0;

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int action = motionEvent.getAction();
        int pointerCount = motionEvent.getPointerCount();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (pointerCount == 1) {
                    xpos = motionEvent.getRawX();
                    ypos = motionEvent.getRawY();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (pointerCount == 1) {
                    WindowManager.LayoutParams lp = (WindowManager.LayoutParams) view.getLayoutParams();
                    float dx = xpos - motionEvent.getRawX();
                    float dy = ypos - motionEvent.getRawY();
                    xpos = motionEvent.getRawX();
                    ypos = motionEvent.getRawY();

                    Log.d(TAG, "lp.x : " + lp.x + ", dx : " + dx + "lp.y : " + lp.y + ", dy : " + dy);

                    lp.x = (int) (lp.x - dx);
                    lp.y = (int) (lp.y - dy);

                    manager.updateViewLayout(view,lp);
                    return true;
                }
                break;

        }
        return false;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (onTopView != null) {
            manager.removeView(onTopView);
            onTopView = null;
        }
    }

    private void getLogcatLog() {
        StringBuilder log = new StringBuilder();
        try {
            //Process process = Runtime.getRuntime().exec("logcat -d -v time");
            Process process = Runtime.getRuntime().exec("logcat -v time");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String folderPath = MainActivity.folderpath;
            String filePath = folderPath + File.separator + filename;
            //Log.e("YYYM", "filePath: "+filePath);
            File logFile = new File(filePath);
            if(!new File(folderPath).exists()) {
                new File(folderPath).mkdir();
            }
            logFile.createNewFile();
            String line = "";
            //파일 output stream 생성
            FileOutputStream fos = new FileOutputStream(logFile, true);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            while ((line = bufferedReader.readLine()) != null) {
                writer.write(line);
                writer.write("\n");
            }
            writer.flush();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
            stopThread = true;
        }
        Log.e("YYYM", "getLogcatLog: " + log.toString());
        //return log.toString();
    }

    class BackLogThread extends Thread {
        @Override
        public void run() {
         while (!stopThread)
         {
             getLogcatLog();
         }
            Log.e("YYYM", "쓰레드 종료: ");
        }
    }
}
