package com.lincbandapp.lincband;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Maria Carabes on 11/26/2014.
 */
public class MyBroadcastReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(MyBroadcastReceiver.class.getSimpleName(), "received broadcast");
    }
}
