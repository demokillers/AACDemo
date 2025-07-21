package com.demokiller.host.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AppUtils {
    private static final String TAG = "AppUtils";
    private static Context sBaseContext;
    private static Context sAppContext;
    private static Activity sCurrentActivity;
    /**
     * 应用是否在后台
     */
    private static boolean isAppInForeground;
    private static boolean isMainActivityIsCreated;
    private static final List<Activity> sActivitys = new ArrayList();

    public AppUtils() {
    }

    public static void setBaseContext(Context context) {
        sBaseContext = context;
    }

    public static void init(Application application) {
        sAppContext = application;
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
                if (activity.getClass().getSimpleName().equals("MainActivity") ) {
                    isMainActivityIsCreated = true;
                }
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                isAppInForeground = true;
                AppUtils.sCurrentActivity = activity;
                synchronized(AppUtils.sActivitys) {
                    AppUtils.sActivitys.add(activity);
                }
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                synchronized(AppUtils.sActivitys) {
                    AppUtils.sActivitys.remove(activity);
                }
                String pauseActivityName = activity.getClass().getSimpleName();
                /*
                 * 介于 Activity 生命周期在切换画面时先进行要跳转画面的 onResume，
                 * 再进行当前画面 onPause，所以当用户且到后台时肯定会为当前画面直接进行 onPause，
                 * 同过此来判断是否应用在前台
                 */
                if (sCurrentActivity != null && pauseActivityName.equals(sCurrentActivity.getClass().getSimpleName())) {
                    isAppInForeground = false;
                }
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                AppUtils.sCurrentActivity = null;
                if (activity.getClass().getSimpleName().equals("MainActivity") ) {
                    isMainActivityIsCreated = false;
                }
            }
        });
    }

    @Nullable
    public static Activity getCurrentActivity() {
        Activity currentActivity = null;
        synchronized(sActivitys) {
            if (!sActivitys.isEmpty()) {
                currentActivity = (Activity)sActivitys.get(sActivitys.size() - 1);
            }
        }

        return currentActivity == null ? sCurrentActivity : currentActivity;
    }

    public static Context getContext() {
        return sAppContext == null ? sBaseContext : sAppContext;
    }

    public static boolean isAppInForeground() {
        return isAppInForeground;
    }

    public static boolean isMainActivityCreated() {
        return isMainActivityIsCreated;
    }
}
