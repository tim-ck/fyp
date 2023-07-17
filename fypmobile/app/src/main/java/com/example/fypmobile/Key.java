package com.example.fypmobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import javax.crypto.Mac;

import javax.crypto.spec.SecretKeySpec;
public class Key {
    private int keyID = -1, a = -1, secret_key = -1;

    final private int p = 23;
    final private int g = 5;

    public Key(SharedPreferences sharedPreferences, Context context){
        this.keyID = sharedPreferences.getInt("keyID", -1);
        this.secret_key = sharedPreferences.getInt("secret_key", -1);
    }

    public int getKeyID(){
        return this.keyID;
    }



    public int dhKeyExchange_getA(){
        this.a = (int) (Math.random() * 12);
        int A = (int) (Math.pow(g, a) % p);
        return A;
    }

    private void dhKeyExchange(int pinFromLock) {
        int A = (int) (Math.pow(g, a) % p);
        int B = pinFromLock;
        secret_key = (int) (Math.pow(B, a) % p);
    }

    public void createKey(int keyID, int pinFromLock, SharedPreferences sharedPreferences){
        this.keyID = keyID;
        dhKeyExchange(pinFromLock);
        sharedPreferences.edit().putInt("keyID", keyID).apply();
        sharedPreferences.edit().putInt("secret_key", secret_key).apply();
    }

    public void deleteKey(SharedPreferences sharedPreferences){
        keyID = -1;
        secret_key = -1;
        sharedPreferences.edit().putInt("keyID", -1).apply();
        sharedPreferences.edit().putInt("secret_key", -1).apply();
    }

    public static byte[] HMAC_SHA256(int key, int challenge) throws Exception {
        String hash = "";
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
//        int
        SecretKeySpec secret_key = new SecretKeySpec(utils.intToByteArray(key), "HmacSHA256");
        hmacSha256.init(secret_key);
        return hmacSha256.doFinal(utils.intToByteArray(challenge));
    }

    public byte[] solveChallenge(int challenge){
        try {
            return HMAC_SHA256(secret_key, challenge);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
