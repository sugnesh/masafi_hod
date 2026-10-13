package com.engear.vansales.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.engear.vansales.common.AppConstants;
import com.engear.vansales.utils.NetworkUtil;


public class BackgroundJobLower extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String status = NetworkUtil.getConnectivityStatusString(context);
        if (!status.equals(AppConstants.NO_CONNECTION)) {
            context.startService(new Intent(context, SyncData.class));
        }
    }
}
