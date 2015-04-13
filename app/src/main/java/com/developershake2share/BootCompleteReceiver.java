package com.developershake2share;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

/**
 * Created by Krishna on 22/Mar/2015.
 */
public class BootCompleteReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent service = new Intent(context, MyService.class);
            context.startService(service);
            Log.e("BootCompleteReceiver", " __________BootCompleteReceiver _________");

        }
    }


}
