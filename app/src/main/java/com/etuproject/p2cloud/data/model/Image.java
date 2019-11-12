package com.etuproject.p2cloud.data.model;

import android.graphics.Bitmap;

public class Image {

    private String title;
    private Bitmap bitmap;
    private boolean selected;

    public String getImageTitle() {
        return title;
    }

    public void setImageTitle(String title) {
        this.title = title;
    }

    public Bitmap getImageBitmap() {
        return bitmap;
    }

    public void setImageBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}