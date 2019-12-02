package com.etuproject.p2cloud.utils;

import com.etuproject.p2cloud.utils.cloud.Dropbox;
import com.etuproject.p2cloud.utils.local.Local;

public class FileSaver implements Runnable {

    public byte[] fileContent;
    public String fileName;

    FileSaver (byte[] fileContent, String fileName) {
        this.fileContent = fileContent;
        this.fileName  = fileName;
    }
    @Override
    public void run() {
        saveToLocal(fileContent, fileName);
        saveToCloud(fileContent, fileName);
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
        Dropbox.getInstance().upload(encrypted, fileName, Dropbox.FileType.PHOTO);
        Dropbox.getInstance().upload(key, fileName, Dropbox.FileType.KEY);
    }
}
