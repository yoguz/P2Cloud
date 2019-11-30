package com.etuproject.p2cloud.utils.local;

import android.os.Build;
import android.os.Environment;

import com.dropbox.core.util.IOUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Local {

    private static Local instance;
    private static final String APP_FOLDER_POSIX = Environment.getExternalStorageDirectory() + "/P2Cloud/Photo";

    private Local() {
        System.out.println("Local constructor");
        File folder = new File(APP_FOLDER_POSIX);
        if (!folder.exists()) {
            boolean res = folder.mkdir();
            if (res) {
                System.out.println("Local || Succesfully created Folder.");
            }
        }
    }

    public static Local getInstance() {
        if (instance == null) {
            instance = new Local();
        }
        return instance;
    }

    public static void save(byte[] encyptedFile, String fileName) {
        OutputStream output = null;
        try {
            output = new FileOutputStream(APP_FOLDER_POSIX + "/" + fileName);
            output.write(encyptedFile);
            System.out.println("Local || Succesfully saved file: " + fileName);
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

    public static byte[] delete(String fileName) {
        File f = new File(fileName);
        byte[] fileContent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                fileContent = Files.readAllBytes(Paths.get(fileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        f.delete();
        return fileContent;
    }
}
