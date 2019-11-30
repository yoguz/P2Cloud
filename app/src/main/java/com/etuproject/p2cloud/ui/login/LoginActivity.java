package com.etuproject.p2cloud.ui.login;

import androidx.annotation.NonNull;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.etuproject.p2cloud.R;
import com.etuproject.p2cloud.ui.main.CameraActivity;
import com.etuproject.p2cloud.ui.main.TokenActivity;
import com.etuproject.p2cloud.utils.FileController;
import com.manusunny.pinlock.PinListener;

public class LoginActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_SET_PIN = 0;
    public static final int REQUEST_CODE_CHANGE_PIN = 1;
    public static final int REQUEST_CODE_CONFIRM_PIN = 2;
    static SharedPreferences pinLockPrefs;
    static SharedPreferences tokenPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tokenPrefs = getSharedPreferences("tokenPrefs", MODE_PRIVATE);
        pinLockPrefs = getSharedPreferences("PinLockPrefs", MODE_PRIVATE);
        init();
    }

    private void init() {
        TextView setPin = (TextView) findViewById(R.id.set_pin);
        TextView confirmPin = (TextView) findViewById(R.id.confirm_pin);

        String pin = pinLockPrefs.getString("pin", "");
        if (pin.equals("")) {
            confirmPin.setEnabled(false);
        } else {
            setPin.setText("Change PIN");
        }

        View.OnClickListener clickListener = getOnClickListener();
        setPin.setOnClickListener(clickListener);
        confirmPin.setOnClickListener(clickListener);
    }

    @NonNull
    private View.OnClickListener getOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                String pin = pinLockPrefs.getString("pin", "");

                if (id == R.id.set_pin && pin.equals("")) {
                    Intent intent = new Intent(LoginActivity.this, SetPinActivities.class);
                    startActivityForResult(intent, REQUEST_CODE_SET_PIN);
                } else if (id == R.id.set_pin) {
                    Intent intent = new Intent(LoginActivity.this, ConfirmPinActivities.class);
                    startActivityForResult(intent, REQUEST_CODE_CHANGE_PIN);
                } else if (id == R.id.confirm_pin) {
                    Intent intent = new Intent(LoginActivity.this, ConfirmPinActivities.class);
                    startActivityForResult(intent, REQUEST_CODE_CONFIRM_PIN);
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CODE_SET_PIN : {
                if(resultCode == PinListener.SUCCESS){
                    //buradan token ekranına geçiş olacak
                    Intent intent = new Intent(LoginActivity.this, TokenActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_SET_PIN);
                } else if(resultCode == PinListener.CANCELLED) {
                    Toast.makeText(this, "Pin set cancelled", Toast.LENGTH_SHORT).show();
                }
                refreshActivity();
                break;
            }
            case REQUEST_CODE_CHANGE_PIN : {
                if(resultCode == PinListener.SUCCESS){
                    Intent intent = new Intent(LoginActivity.this, SetPinActivities.class);
                    startActivityForResult(intent, REQUEST_CODE_SET_PIN);
                } else if(resultCode == PinListener.CANCELLED){
                    Toast.makeText(this, "Pin change cancelled", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case REQUEST_CODE_CONFIRM_PIN : {
                if(resultCode == PinListener.SUCCESS){
                    //Toast.makeText(this, "Pin is correct :)", Toast.LENGTH_SHORT).show();
                    String token1 = tokenPrefs.getString("token1", "");
                    String token2 = tokenPrefs.getString("token2", "");
                    Intent intent;
                    if(token1 == null || token2 == null || token1.equals("") || token2.equals("")) {
                        intent = new Intent(LoginActivity.this, TokenActivity.class);
                    } else {
                        intent = new Intent(LoginActivity.this, CameraActivity.class);
                    }
                    startActivityForResult(intent, REQUEST_CODE_SET_PIN);
                } else if(resultCode == PinListener.CANCELLED) {
                    Toast.makeText(this, "Pin confirm cancelled", Toast.LENGTH_SHORT).show();
                }
                break;
            }

        }
    }

    private void refreshActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

}
