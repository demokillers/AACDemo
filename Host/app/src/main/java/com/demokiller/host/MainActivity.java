package com.demokiller.host;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import java.io.File;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class MainActivity extends BaseActivity {

    private DexClassLoader classLoader;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linearLayout=findViewById(R.id.layout);
        String filePath = "/data/data/com.demokiller.host/cache/app-release-unsigned.apk";
        filePath="/mnt/usb/sda1/app-release-unsigned.apk";
        File file=new File(filePath);
        if(!file.exists())
            filePath="/mnt/usb/sdb1/app-release-unsigned.apk";
        String releasePath = "/data/data/com.demokiller.host/cache";
        Log.d("wzh", filePath);
        Log.d("wzh", releasePath);
        Log.d("wzh", getClassLoader().toString());
        classLoader = new DexClassLoader(filePath, releasePath, null, getClassLoader());
        loadResources(filePath);
        setBackground();
    }

    private void setBackground() {
        try {
            Class clazz = classLoader.loadClass("com.demokiller.resource.UIutils");
            Method method = clazz.getMethod("getImageDrawable", Context.class);
            Drawable drawable = (Drawable) method.invoke(null, this);
            linearLayout.setBackground(drawable);
        } catch (Exception e) {
            Log.i("wzh", "error:" + Log.getStackTraceString(e));
        }
    }
}