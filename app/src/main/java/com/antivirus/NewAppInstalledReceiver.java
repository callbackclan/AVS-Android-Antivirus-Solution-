package com.antivirus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NewAppInstalledReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, AppCheckServices.class));
    }

}