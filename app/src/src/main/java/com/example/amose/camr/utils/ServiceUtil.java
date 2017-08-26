package com.example.amose.camr.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

import java.util.List;

public class ServiceUtil {
    public static boolean isServiceRunning(Context context, String serviceName){
        ActivityManager manager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> runningServiceInfos = manager.getRunningServices(Integer.MAX_VALUE);
        for(RunningServiceInfo runningServiceInfo : runningServiceInfos){
            String name = runningServiceInfo.service.getClassName();
            if(name.equals(serviceName)){
                return true;
            }
        }
        return false;
    }
}