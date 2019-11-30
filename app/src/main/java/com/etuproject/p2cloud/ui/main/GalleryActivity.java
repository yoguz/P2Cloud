package com.etuproject.p2cloud.ui.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dropbox.core.v2.files.DeleteError;
import com.etuproject.p2cloud.R;
import com.etuproject.p2cloud.data.model.Image;
import com.etuproject.p2cloud.utils.Crypto;
import com.etuproject.p2cloud.utils.FileController;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class GalleryActivity  extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.container);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),2);
        recyclerView.setLayoutManager(layoutManager);
        ArrayList<Image> createLists = new ArrayList<>();
        FileController.prepareData(createLists);
        final GalleryAdapter adapter = new GalleryAdapter(getApplicationContext(), createLists);
        recyclerView.setAdapter(adapter);

        Button deleteBtn = findViewById(R.id.deleteBtn);
        assert deleteBtn != null;
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(Image image : adapter.getImages()) {
                    if(image.isSelected()) {
                        FileController.delete(image.getImageTitle());
                    }
                }
            }
        });

        NavigationView navigationView =(NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();
                        if (menuItem.getTitle().toString().equals("Photo")) {
                            startActivity(new Intent(GalleryActivity.this, CameraActivity.class));
                        } else if (menuItem.getTitle().toString().equals("Gallery")) {
                            startActivity(new Intent(GalleryActivity.this, GalleryActivity.class));
                        } else if (menuItem.getTitle().toString().equals("Cloud Tokens")) {
                            startActivity(new Intent(GalleryActivity.this, TokenActivity.class));
                        }
                        return true;
                    }
                });
    }


}
