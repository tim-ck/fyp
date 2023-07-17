package com.example.fypmobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "MyReceiver";
    private int randomNum;
    public MyReceiver() {
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Byte tmp = intent.getByteExtra("randomNum", (byte)0x00);
        randomNum = tmp.intValue();
        Toast.makeText(context, "randomNum: " + randomNum, Toast.LENGTH_SHORT).show();
    }
}
