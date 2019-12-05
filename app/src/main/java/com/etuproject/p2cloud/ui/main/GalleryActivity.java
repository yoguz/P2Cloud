package com.etuproject.p2cloud.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.etuproject.p2cloud.R;
import com.etuproject.p2cloud.data.model.Image;
import com.etuproject.p2cloud.utils.FileController;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class GalleryActivity  extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private GalleryAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        final RecyclerView recyclerView = (RecyclerView)findViewById(R.id.container);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),2);
        recyclerView.setLayoutManager(layoutManager);
        ArrayList<Image> createLists = new ArrayList<>();
        FileController.prepareData(createLists);
        adapter = new GalleryAdapter(getApplicationContext(), createLists);
        recyclerView.setAdapter(adapter);

        Button deleteBtn = findViewById(R.id.deleteBtn);
        assert deleteBtn != null;
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Image> newList = new ArrayList<>();
                for(Image image : adapter.getImages()) {
                    if (image.isSelected()) {
                        FileController.delete(image.getImageTitle());
                    } else {
                        newList.add(image);
                    }
                }
                adapter =  new GalleryAdapter(getApplicationContext(), newList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });

        NavigationView navigationView =(NavigationView) findViewById(R.id.nav_view);
        navigationView.bringToFront();
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
