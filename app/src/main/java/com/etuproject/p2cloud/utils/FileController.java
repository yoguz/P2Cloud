package com.etuproject.p2cloud.utils;

import com.etuproject.p2cloud.utils.cloud.Dropbox;
import com.etuproject.p2cloud.utils.local.Local;
import java.nio.charset.StandardCharsets;

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

    public static void sync() {
        // TODO
    }

    public static void save(byte[] bytes, String fileName) {
        saveToLocal(bytes, fileName);
        saveToCloud(bytes, fileName);
    }

    private static void saveToLocal(byte[] bytes, String fileName) {
        //String byteString = new String(bytes, StandardCharsets.ISO_8859_1);
        Crypto cryptoFunctions = new Crypto();
        byte[] encrypted= cryptoFunctions.encrypt(bytes, null);
        Local.getInstance().save(encrypted, fileName);
    }

    private static void saveToCloud(byte[] bytes, String fileName) {
        //String byteString = new String(bytes, StandardCharsets.ISO_8859_1);
        byte[] key = Crypto.generateKey();
        System.out.println("FileController || saveToCloud || key:" + new String(key));
        Crypto cryptoFunctions = new Crypto();
        byte[] encrypted = cryptoFunctions.encrypt(bytes, key);
        String fileHash = cryptoFunctions.hash(encrypted);
        Dropbox.getInstance().upload(encrypted, fileName, Dropbox.FileType.PHOTO);
        Dropbox.getInstance().upload(key, fileHash, Dropbox.FileType.KEY);
    }

    public static void delete(String fileName) {
        byte[] deletedFile = deleteFromLocal(fileName);
        Crypto cryptoFunctions = new Crypto();
        String fileHash = cryptoFunctions.hash(deletedFile);
        deleteFromCloud(fileName, fileHash);
    }

    private static byte[] deleteFromLocal(String fileName) {
        return Local.getInstance().delete(fileName);
    }

    private static void deleteFromCloud(String fileName, String fileHash) {
        Dropbox.getInstance().delete(fileName, Dropbox.FileType.PHOTO);
        Dropbox.getInstance().delete(fileHash, Dropbox.FileType.KEY);
    }
}
