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
        String byteString = new String(bytes, StandardCharsets.ISO_8859_1);
        Crypto cryptoFunctions = new Crypto();
        String encryptedString = cryptoFunctions.encrypt(byteString, null);
        Local.getInstance().save(encryptedString, fileName);
    }

    private static void saveToCloud(byte[] bytes, String fileName) {
        String byteString = new String(bytes, StandardCharsets.ISO_8859_1);
        String key = Crypto.generateKey();
        Crypto cryptoFunctions = new Crypto();
        String encryptedFile = cryptoFunctions.encrypt(byteString, key);
        String fileHash = cryptoFunctions.hash(encryptedFile);
        Dropbox.getInstance().uploadPhoto(encryptedFile, fileName);
        Dropbox.getInstance().uploadKey(key, fileHash);
    }

    public static void delete(String fileName) {
        String deletedFileHash = deleteFromLocal(fileName);
        deleteFromCloud(fileName, deletedFileHash);
    }

    private static String deleteFromLocal(String fileName) {
        return Local.getInstance().delete(fileName);
    }

    private static void deleteFromCloud(String fileName, String fileHash) {
    }
}
