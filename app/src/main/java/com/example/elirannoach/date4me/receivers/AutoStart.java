package com.example.elirannoach.date4me.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.elirannoach.date4me.service.MessageService;

public class AutoStart extends BroadcastReceiver
{
    public void onReceive(Context context, Intent arg1)
    {
        Intent intent = new Intent(context,MessageService.class);
        context.startService(intent);
        Log.i("Autostart", "started");
    }
}
