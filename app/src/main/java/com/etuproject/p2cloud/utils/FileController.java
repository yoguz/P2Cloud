package com.etuproject.p2cloud.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.etuproject.p2cloud.data.model.Image;
import com.etuproject.p2cloud.utils.cloud.Dropbox;
import com.etuproject.p2cloud.utils.local.Local;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
        Local.setLocalKey(localKey);
        Dropbox.setAccessToken(remotePhotoToken);
        return getInstance();
    }

    public static void sync() {

    }

    public static void save(byte[] bytes, String fileName) {
        saveToLocal(bytes, fileName);
        saveToCloud(bytes, fileName);
    }

    private static void saveToLocal(byte[] bytes, String fileName) {
        //String byteString = new String(bytes, StandardCharsets.ISO_8859_1);
        Crypto cryptoFunctions = new Crypto();
        byte[] encrypted= cryptoFunctions.encrypt(bytes, Local.getLocalKey());
        Local.save(encrypted, fileName);
    }

    private static void saveToCloud(byte[] bytes, String fileName) {
        //String byteString = new String(bytes, StandardCharsets.ISO_8859_1);
        byte[] key = Crypto.generateKey();
        System.out.println("FileController || saveToCloud || key:" + new String(key));
        Crypto cryptoFunctions = new Crypto();
        byte[] encrypted = cryptoFunctions.encrypt(bytes, key);
        Dropbox.upload(encrypted, fileName, Dropbox.FileType.PHOTO);
        Dropbox.upload(key, fileName, Dropbox.FileType.KEY);
    }

    public static void delete(String fileName) {
        deleteFromLocal(fileName);
        deleteFromCloud(fileName, fileName);
    }

    private static void deleteFromLocal(String fileName) {
         Local.delete(fileName);
    }

    private static void deleteFromCloud(String fileName, String fileHash) {
        Dropbox.delete(fileName, Dropbox.FileType.PHOTO);
        Dropbox.delete(fileHash, Dropbox.FileType.KEY);
    }

    /**
     * P2Cloud dosya dizini altında yer alan bütün encrypted resimleri enrypt edip
     * Image listesi olarak dönen method.
     * @return ArrayList<Image>
     */
    public static void prepareData(ArrayList<Image> images){
        try {
            Crypto crypto = new Crypto();
            File[] files = Local.list();

            for (int i = 0; i < files.length; i++) {
                Image image = new Image();
                image.setImageTitle(files[i].getName());
                byte fileContent[] = new byte[(int)files[i].length()];
                InputStream stream = new FileInputStream(files[i]);
                stream.read(fileContent);
                String byteString = new String(fileContent, StandardCharsets.ISO_8859_1);
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
