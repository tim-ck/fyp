package com.example.fypmobile;

import android.app.Service;
import android.content.Intent;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;

public class MyHostApduService extends HostApduService {
    private static final String TAG = "JDR HostApduService";
    private static final byte[] GET_KEYID = {
            (byte)0x00, // CLA	- Class - Class of instruction
            (byte)0xA4, // INS	- Instruction - Instruction code
            (byte)0x04, // P1	- Parameter 1 - Instruction parameter 1
            (byte)0x00, // P2	- Parameter 2 - Instruction parameter 2
            (byte)0x07, // Lc field	- Number of bytes present in the data field of the command
            (byte)0xF0, (byte)0x39, (byte)0x41, (byte)0x48, (byte)0x14, (byte)0x81, (byte)0x01, // NDEF Tag Application name
            (byte)0x00  // Le field	- Maximum number of bytes expected in the data field of the response to the command
    };
    private static final byte[] GET_PASSCODE = {
        (byte)0x00, // CLA	- Class - Class of instruction
        (byte)0xA4, // INS	- Instruction - Instruction code
        (byte)0x04, // P1	- Parameter 1 - Instruction parameter 1
        (byte)0x00, // P2	- Parameter 2 - Instruction parameter 2
        (byte)0x07, // Lc field	- Number of bytes present in the data field of the command
        (byte)0xF0, (byte)0x39, (byte)0x41, (byte)0x48, (byte)0x14, (byte)0x81, (byte)0x02, // NDEF Tag Application name
        (byte)0x00  // Le
    };
    private static final byte[] RECIEVE_RANDOM_NUMBER = {
            (byte)0x01,
            (byte)0x2E
    };
    private static final byte[] A_OKAY = {
            (byte)0x90,
            (byte)0x00
    };
    private byte[] passcode = {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
    private byte[] unlock_success = {
            (byte)0x00, // CLA	- Class - Class of instruction
            (byte)0xA4, // INS	- Instruction - Instruction code
            (byte)0x04, // P1	- Parameter 1 - Instruction parameter 1
            (byte)0x00, // P2	- Parameter 2 - Instruction parameter 2
            (byte)0x07, // Lc field	- Number of bytes present in the data field of the command
            (byte)0xF0, (byte)0x39, (byte)0x41, (byte)0x48, (byte)0x14, (byte)0x81, (byte)0x03, // NDEF Tag Application name
            (byte)0x00  // Le
    };
    private byte[] unlock_failed = {
            (byte)0x00, // CLA	- Class - Class of instruction
            (byte)0xA4, // INS	- Instruction - Instruction code
            (byte)0x04, // P1	- Parameter 1 - Instruction parameter 1
            (byte)0x00, // P2	- Parameter 2 - Instruction parameter 2
            (byte)0x07, // Lc field	- Number of bytes present in the data field of the command
            (byte)0xF0, (byte)0x39, (byte)0x41, (byte)0x48, (byte)0x14, (byte)0x81, (byte)0x04, // NDEF Tag Application name
            (byte)0x00  // Le
    };
    private byte[] keyID;

    @Override
    public void onCreate() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.hasExtra("unlockBytes")) {
            passcode = intent.getByteArrayExtra("unlockBytes");
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            passcode = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};;
                        }
                    },
                    15000
            );
            //remove unlockBytes from intent
            intent.removeExtra("unlockBytes");
        }
        if (intent.hasExtra("keyID")) {
            int keyIDInt = intent.getIntExtra("keyID", -1);
            if(keyIDInt != -1) {
                keyID = utils.intToByteArray(keyIDInt);
                Log.i(TAG, "KeyID: " + utils.bytesToString(keyID));
            }
        }
        return Service.START_STICKY_COMPATIBILITY;
    }

    @Override
    public byte[] processCommandApdu(byte[] apdu, Bundle extras) {
        if(Arrays.equals(apdu, GET_KEYID)) {
            return keyID;
        }
        byte[] subArray = Arrays.copyOfRange(apdu, 0, 2);
        if(Arrays.equals(subArray, RECIEVE_RANDOM_NUMBER) && apdu.length == 5) {
            //apdu is unsigned byte, so we need to convert it to int
            int randomNum0 = apdu[4] & 0xFF;
            int randomNum1 = apdu[2] & 0xFF;
            int randomNum2 = apdu[3] & 0xFF;
            passcode = new byte[]{(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
            Intent i = new Intent();
            i.putExtra("randomNum0", randomNum0);
            i.putExtra("randomNum1", randomNum1);
            i.putExtra("randomNum2", randomNum2);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setClass(this, MainActivity.class);
            startActivity(i);
            return A_OKAY;
        }
        if(Arrays.equals(apdu, GET_PASSCODE)) {
            Toast.makeText(this, "return passcode", Toast.LENGTH_SHORT).show();
            return passcode;
        }
        if(Arrays.equals(apdu, unlock_success)) {
            Toast.makeText(this, "unlock_success", Toast.LENGTH_SHORT).show();
            Intent i = new Intent();
            i.putExtra("unlockSuccess", true);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setClass(this, MainActivity.class);
            startActivity(i);
            return A_OKAY;
        }
        if (Arrays.equals(apdu, unlock_failed)) {
            Toast.makeText(this, "unlock_failed", Toast.LENGTH_SHORT).show();
            Intent i = new Intent();
            i.putExtra("unlockSuccess", false);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setClass(this, MainActivity.class);
            startActivity(i);
            return A_OKAY;
        }
        Toast.makeText(this, "receive unknown apdu", Toast.LENGTH_SHORT).show();
        return "unknown apdu".getBytes();
    }
    @Override
    public void onDeactivated(int reason) {
        Log.d("HCE", "Deactivated: " + reason);
        return;
    }
}