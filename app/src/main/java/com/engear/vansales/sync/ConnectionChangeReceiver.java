package com.engear.vansales.sync;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import com.engear.vansales.common.AppConstants;
import com.engear.vansales.restAPI.NetworkUtil;


public class ConnectionChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String status = NetworkUtil.INSTANCE.getConnectivityStatusString(context);
        Toast.makeText(context,status, Toast.LENGTH_LONG).show();
        if(!status.equals(AppConstants.NO_CONNECTION)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, SyncData.class));
            } else {
                context.startService(new Intent(context, SyncData.class));
            }
        }
    }
}
