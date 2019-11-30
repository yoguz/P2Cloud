package com.etuproject.p2cloud.ui.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.etuproject.p2cloud.R;
import com.google.android.material.navigation.NavigationView;


public class TokenActivity extends AppCompatActivity {
    static SharedPreferences tokenPrefs;
    protected void onCreate(Bundle savedInstanceState) {
        tokenPrefs = getSharedPreferences("tokenPrefs", MODE_PRIVATE);
        String token1 = tokenPrefs.getString("token1", "");
        String token2 = tokenPrefs.getString("token2", "");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        Button tokenSaveBtn = findViewById(R.id.tokenSaveBtn);
        assert tokenSaveBtn != null;
        tokenSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor edit = TokenActivity.tokenPrefs.edit();
                EditText token1 = (EditText)findViewById(R.id.cloudToken1);
                EditText token2 = (EditText)findViewById(R.id.cloudToken2);
                edit.putString("token1", token1.getText().toString());
                edit.putString("token2", token2.getText().toString());
                edit.commit();
                startActivity(new Intent(TokenActivity.this, CameraActivity.class));
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    // set item as selected to persist highlight
                    menuItem.setChecked(true);
                    if (menuItem.getTitle().toString().equals("Photo")) {
                        startActivity(new Intent(TokenActivity.this, CameraActivity.class));
                    } else if (menuItem.getTitle().toString().equals("Gallery")) {
                        startActivity(new Intent(TokenActivity.this, GalleryActivity.class));
                    } else if (menuItem.getTitle().toString().equals("Cloud Tokens")) {
                        startActivity(new Intent(TokenActivity.this, TokenActivity.class));
                    }
                    return true;
                }
            });
    }


}
