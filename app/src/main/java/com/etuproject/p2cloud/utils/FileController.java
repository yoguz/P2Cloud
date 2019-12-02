package com.etuproject.p2cloud.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.etuproject.p2cloud.data.model.Image;
import com.etuproject.p2cloud.utils.cloud.Dropbox;
import com.etuproject.p2cloud.utils.local.Local;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class FileController {

    private static FileController instance;

    private FileController() {
    }

    public static FileController getInstance() {
        if (instance == null) {
            instance = new FileController();
        }
        return instance;
    }

    public static FileController setPrefs(String localKey, String remotePhotoToken, String remoteKeyToken) {
        Local.setLocalKey(localKey + localKey + localKey + localKey);
        Dropbox.setAccessToken(remotePhotoToken);
        return getInstance();
    }

    public static void sync() {
        Runnable runnable = new FileSyncer();
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public static void save(byte[] bytes, String fileName) {
        FileSaver saver = new FileSaver(bytes, fileName);
        Thread t = new Thread(saver);
        t.start();
    }

    public static void delete(String fileName) {
        FileDeleter deleter = new FileDeleter(fileName);
        Thread t = new Thread(deleter);
        t.start();
    }

    /**
     * P2Cloud dosya dizini altında yer alan bütün encrypted resimleri enrypt edip
     * Image listesi olarak dönen method.
     * @return ArrayList<Image>
     */
    public static void prepareData(ArrayList<Image> images){
        try {
            Crypto crypto = new Crypto();
            File[] files = Local.getInstance().list();

            for (int i = 0; i < files.length; i++) {
                Image image = new Image();
                image.setImageTitle(files[i].getName());
                byte fileContent[] = new byte[(int)files[i].length()];
                InputStream stream = new FileInputStream(files[i]);
                stream.read(fileContent);
                byte[] decrypted = crypto.decrypt(fileContent, Local.getLocalKey());
                if(decrypted != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decrypted, 0, decrypted.length);
                    image.setImageBitmap(bitmap);
                    images.add(image);
                }
                stream.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
