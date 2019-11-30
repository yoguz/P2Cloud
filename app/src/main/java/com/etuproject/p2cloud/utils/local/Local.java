package com.etuproject.p2cloud.utils.local;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Local {

    private static Local instance;
    private static final String APP_FOLDER_POSIX = Environment.getExternalStorageDirectory() + "/P2Cloud/Photo";

    private Local() {
        File folder = new File(APP_FOLDER_POSIX);
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    public static Local getInstance() {
        if (instance == null) {
            instance = new Local();
        }
        return instance;
    }

    public static void save(String encyptedFile, String fileName) {
        OutputStream output = null;
        try {
            output = new FileOutputStream(APP_FOLDER_POSIX + "/" + fileName);
            output.write(encyptedFile.getBytes(StandardCharsets.ISO_8859_1));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != output) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String delete(String fileName) {
        File f = new File(fileName);
        f.delete();
        return "asdasd";
    }
}
