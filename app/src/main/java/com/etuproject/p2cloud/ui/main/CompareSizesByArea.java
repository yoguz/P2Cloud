package com.etuproject.p2cloud.ui.main;

/**
 * Created by MesutKutlu on 10.03.2018.
 */

import android.util.Size;

import java.util.Comparator;

public class CompareSizesByArea implements Comparator<Size> {
    @Override
    public int compare(Size lhs, Size rhs) {
        // We cast here to ensure the multiplications won't overflow
        return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                (long) rhs.getWidth() * rhs.getHeight());
    }
}