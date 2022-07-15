package com.avsolution;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.lang.UCharacter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import com.avsolution.modules.Util;

public class NetworkChangeListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        View internetErrorLayout= LayoutInflater.from(context).inflate(R.layout.no_internet, null);
        if(!Util.isOnline(context)){
            internetErrorLayout.setVisibility(View.VISIBLE);
        }
        else{
            internetErrorLayout.setVisibility(View.GONE);
        }
    }
}
