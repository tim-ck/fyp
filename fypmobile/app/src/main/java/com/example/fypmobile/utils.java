package com.example.fypmobile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.nio.ByteBuffer;

public class utils {

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String bytesToString(byte[] bytes) {
//        return string in format of 0x00 0x00 0x00
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("0x%02X ", b));
        }
        return sb.toString();
    }

   public static byte[] intToByteArray(int value) {
       return ByteBuffer.allocate(4).putInt(value).array();
    }
    public static void alertBox( Context context,String title, String message,
                                 Boolean cancelable, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("OK", listener);
        if (cancelable)
            alertDialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        else
            alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.show();
    }

}