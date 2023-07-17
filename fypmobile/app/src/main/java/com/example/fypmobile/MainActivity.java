package com.example.fypmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    Boolean cancelThread = false;
    SharedPreferences sharedPreferences;
    Vibrator vibrator;

    EditText etPinCode, etKeyID;
    TextView tvCountdown, tvError, tvPinCode, tvKeyDetail, tvCountDown;

    Button btnRandomCode0, btnRandomCode1, btnRandomCode2, btnGetPinCode, btnSubmitPinFromLock, btnDeleteKey;

    ConstraintLayout clUnlockDoor, clKeyManagement, clVerify;

    BottomNavigationView bottomNavigationView;

    String error = "";

    ArrayList<Thread> threads = new ArrayList<>();

    private Key key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("fyp", MODE_PRIVATE);
        key = new Key(sharedPreferences, getApplicationContext());
        Intent intent = new Intent(this, MyHostApduService.class);
        intent.putExtra("keyID", key.getKeyID());
        startService(intent);
        VibratorManager vibratorManager = (VibratorManager)getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
        vibrator = vibratorManager.getDefaultVibrator();
        tvCountdown = findViewById(R.id.tvAddKeyInstruction);
        clVerify = findViewById(R.id.clVerify);
        clUnlockDoor = findViewById(R.id.clUnlockDoor);
        initKeyManagementPage();
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.btnUnlockDoor:
                            stopThreads();
                            showUnlockDoor();
                            if (!checkKeyStat()) {
                                utils.alertBox(this, "No Key Found",
                                        "Please add a key first", false, (dialog, which) -> {
                                            dialog.dismiss();
                                            bottomNavigationView.setSelectedItemId(R.id.btnAddKey);
                                        });
                            }
                            break;
                        case R.id.btnAddKey:
                            stopThreads();
                            showKeyManagementPage();
                            break;
                    }
                    return true;
                }
        );
        if (!checkKeyStat()) {
            utils.alertBox(this, "No Key Found",
                    "Please add a key first", false, (dialog, which) -> {
                        dialog.dismiss();
                        bottomNavigationView.setSelectedItemId(R.id.btnAddKey);
                    });
            return;
        }
        bottomNavigationView.setSelectedItemId(R.id.btnUnlockDoor);
        if (getIntent().getExtras() != null) {
            int randomNum0 = getIntent().getExtras().getInt("randomNum0", 0);
            int randomNum1 = getIntent().getExtras().getInt("randomNum1", 0);
            int randomNum2 = getIntent().getExtras().getInt("randomNum2", 0);
            clearIntentExtraRandomnumber();
            if(randomNum1*randomNum2*randomNum0 != 0){
                initVerifyPage(randomNum0, randomNum1, randomNum2);
                showVerifyPage();
            }
            if (getIntent().hasExtra("unlockSuccess")) {
                if(getIntent().getBooleanExtra("unlockSuccess", false)){
                    utils.alertBox(this, "Unlock Success", "Door is unlocked", false, (dialog, which) -> {
                        dialog.dismiss();
                        showUnlockDoor();
                    });
                }else{
                    utils.alertBox(this, "Unlock Failed", "Door is not unlocked", false, (dialog, which) -> {
                        dialog.dismiss();
                        showUnlockDoor();
                    });
                }
                getIntent().removeExtra("unlockSuccess");
            }


        }

    }

    public void clearIntentExtraRandomnumber(){
        getIntent().putExtra("randomNum0", 0);
        getIntent().putExtra("randomNum1", 0);
        getIntent().putExtra("randomNum2", 0);
    }

    public void initVerifyPage(int randomNum0, int randomNum1, int randomNum2){
        if(!checkKeyStat()){
            utils.alertBox(this, "No Key Found",
                    "Please add a key first", false, (dialog, which) -> {
                        dialog.dismiss();
                        bottomNavigationView.setSelectedItemId(R.id.btnAddKey);
                    });
            return;
        }
        btnRandomCode0 = findViewById(R.id.btnRandomCode0);
        btnRandomCode1 = findViewById(R.id.btnRandomCode1);
        btnRandomCode2 = findViewById(R.id.btnRandomCode2);
        tvCountDown = findViewById(R.id.tvCountDown);
        btnRandomCode0.setText(String.valueOf(randomNum0));
        btnRandomCode1.setText(String.valueOf(randomNum1));
        btnRandomCode2.setText(String.valueOf(randomNum2));

        btnRandomCode0.setOnClickListener(v -> {
            byte[] unlockCode = key.solveChallenge(randomNum0);
            Intent intent = new Intent(v.getContext(), MyHostApduService.class);
            intent.putExtra("unlockBytes", unlockCode);
            startService(intent);
            stopThreads();
            showUnlockDoor();
        });

        btnRandomCode1.setOnClickListener(v -> {
            byte[] unlockCode = key.solveChallenge(randomNum1);
            Intent intent = new Intent(v.getContext(), MyHostApduService.class);
            intent.putExtra("unlockBytes", unlockCode);
            startService(intent);
            stopThreads();
            showUnlockDoor();
        });

        btnRandomCode2.setOnClickListener(v -> {
            byte[] unlockCode = key.solveChallenge(randomNum2);
            Intent intent = new Intent(v.getContext(), MyHostApduService.class);
            intent.putExtra("unlockBytes", unlockCode);
            startService(intent);
            stopThreads();
            showUnlockDoor();
        });

        Thread vibrateThread = new Thread(() -> {
            for(int maxTime = 15; maxTime >=0; maxTime--){
                if(cancelThread){
                    return;
                }
                vibrator.vibrate(VibrationEffect.createOneShot(50, 200));
                int finalMaxTime = maxTime;
                runOnUiThread(() -> {
                    tvCountDown.setText(String.valueOf(finalMaxTime));
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
            runOnUiThread(() -> {
                utils.alertBox(this, "Time Out", "Please try again", false, (dialog, which) -> {
                    dialog.dismiss();
                    showUnlockDoor();
                });
            });
        });
        threads.add(vibrateThread);
        vibrateThread.start();
    }



    // Key Management Page Start
    public void initKeyManagementPage(){
        clKeyManagement = findViewById(R.id.clKeyManagement);
        btnGetPinCode = findViewById(R.id.btnGetPinCode);
        btnSubmitPinFromLock = findViewById(R.id.btnSubmitPinFromLock);
        btnDeleteKey = findViewById(R.id.btnDeleteKey);
        tvPinCode = findViewById(R.id.tvPinCode);
        tvKeyDetail = findViewById(R.id.tvKeyDetail);
        etPinCode = findViewById(R.id.etPinCode);
        etKeyID = findViewById(R.id.etKeyID);
        if(!checkKeyStat()){
            tvKeyDetail.setText("Key Detail\n===========\n" + "Key ID : -");
            keyManagement_reset();
        }else{
            tvKeyDetail.setText("Key Detail\n===========\n" + "Key ID : " + key.getKeyID());
            keyManagement_enableDeleteKeyBtn();
        }
        btnGetPinCode.setOnClickListener(v -> {
            int A = key.dhKeyExchange_getA();
            tvPinCode.setText(String.valueOf(A));
            utils.alertBox(this, "Pin Code", "Please enter the pin : \"" + A +
                    "\"\non the lock to create key", false, (dialog, which) -> {
                dialog.dismiss();
                keyManagement_enableEnterPin(A);
            });
        });

        btnSubmitPinFromLock.setOnClickListener(v -> {
            if(etPinCode.getText().toString().equals("") || etKeyID.getText().toString().equals("")){
                utils.alertBox(this, "Error", "Please enter the pin code and key ID", false, (dialog, which) -> {
                    dialog.dismiss();
                });
                return;
            }
            utils.alertBox(this, "Alert", "The detail you entered is\n" +
                    "Pin Code : " + etPinCode.getText().toString() + "\n" +
                    "Key ID : " + etKeyID.getText().toString() + "\n" +
                    "Is this correct?" + "\n" +
                    "Important: Incorrect key ID will cause the lock to be unable to unlock", true, (dialog, which) -> {
                dialog.dismiss();
                int pinFromLock = Integer.parseInt(etPinCode.getText().toString());
                int keyID = Integer.parseInt(etKeyID.getText().toString());
                SharedPreferences pref = getSharedPreferences("keyID", MODE_PRIVATE);
                pref.edit()
                        .putInt("keyID", keyID)
                        .commit();
                pref = getSharedPreferences("pinFromLock", MODE_PRIVATE);
                pref.edit()
                        .putInt("pinFromLock", pinFromLock)
                        .commit();
                key.createKey(keyID, pinFromLock, sharedPreferences);
                Intent intent = new Intent(this, MyHostApduService.class);
                intent.putExtra("keyID", key.getKeyID());
                startService(intent);
                utils.alertBox(this, "Key Created", "Key created successfully", false, (d,w) -> {
                    dialog.dismiss();
                    tvKeyDetail.setText("Key Detail\n===========\n" + "Key ID : " + key.getKeyID());
                    keyManagement_enableDeleteKeyBtn();
                });

            });

        });

        btnDeleteKey.setOnClickListener(v -> {
            utils.alertBox(this, "Reset Key", "Are you sure you want to reset the key?\nCaution: This action cannot be undone",
                    true, (dialog, which) -> {
                        dialog.dismiss();
                        key.deleteKey(sharedPreferences);
                        tvKeyDetail.setText("Key Detail\n===========\n" + "Key ID : -");
                        Intent intent = new Intent(this, MyHostApduService.class);
                        intent.putExtra("keyID", key.getKeyID());
                        startService(intent);
                        keyManagement_reset();
                    });
        });
    }

    public void keyManagement_enableDeleteKeyBtn(){
        btnDeleteKey.setEnabled(true);
        etPinCode.setText("");
        etKeyID.setText("");
        tvPinCode.setText("NA");
        btnGetPinCode.setEnabled(false);
        etPinCode.setEnabled(false);
        etKeyID.setEnabled(false);
        btnSubmitPinFromLock.setEnabled(false);
    }

    public void keyManagement_enableEnterPin(int pinToLock){
        tvPinCode.setText(String.valueOf(pinToLock));
        btnGetPinCode.setEnabled(false);
        etPinCode.setEnabled(true);
        etKeyID.setEnabled(true);
        btnSubmitPinFromLock.setEnabled(true);
    }

    public void keyManagement_reset(){
        etPinCode.setText("");
        etKeyID.setText("");
        tvPinCode.setText("NA");
        btnGetPinCode.setEnabled(true);
        etPinCode.setEnabled(false);
        etKeyID.setEnabled(false);
        btnSubmitPinFromLock.setEnabled(false);
        btnDeleteKey.setEnabled(false);
    }
    // Key Management Page End
    protected boolean checkKeyStat(){
        return key.getKeyID() != -1;
    }

    protected void showUnlockDoor(){
        clKeyManagement.setVisibility(ConstraintLayout.GONE);
        clUnlockDoor.setVisibility(ConstraintLayout.VISIBLE);
        clVerify.setVisibility(ConstraintLayout.GONE);
        getSupportActionBar().setTitle("Unlock Door");

    }

    protected void showKeyManagementPage(){
        clKeyManagement.setVisibility(ConstraintLayout.VISIBLE);
        clUnlockDoor.setVisibility(ConstraintLayout.GONE);
        clVerify.setVisibility(ConstraintLayout.GONE);
        getSupportActionBar().setTitle("Key Management");
    }

    protected void showVerifyPage() {
        clKeyManagement.setVisibility(ConstraintLayout.GONE);
        clUnlockDoor.setVisibility(ConstraintLayout.GONE);
        clVerify.setVisibility(ConstraintLayout.VISIBLE);
        getSupportActionBar().setTitle("Verify");

    }


    public void stopThreads(){
        cancelThread = true;
        threads.forEach(Thread::interrupt);
        threads.clear();
        cancelThread = false;
    }

    @Override
    protected void onDestroy() {
        stopThreads();
        super.onDestroy();
    }
}