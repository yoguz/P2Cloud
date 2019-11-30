package com.etuproject.p2cloud.ui.login;

import android.content.SharedPreferences;
import android.widget.Toast;

import com.manusunny.pinlock.SetPinActivity;

public class SetPinActivities extends SetPinActivity {

    @Override
    public void onPinSet(String pin) {
        SharedPreferences.Editor edit = LoginActivity.pinLockPrefs.edit();
        edit.putString("pin", pin);
        edit.commit();
        setResult(SUCCESS);
        finish();
    }
    @Override
    public void onForgotPin() {
        Toast.makeText(this, "Sorry. Not Implemented", Toast.LENGTH_SHORT).show();
    }
}