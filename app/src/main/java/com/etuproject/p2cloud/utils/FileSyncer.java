package com.etuproject.p2cloud.utils;

import com.dropbox.core.v2.files.Metadata;
import com.etuproject.p2cloud.utils.cloud.Dropbox;
import com.etuproject.p2cloud.utils.local.Local;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileSyncer implements Runnable {
    @Override
    public void run() {
        System.out.println("FileSyncer || Sync started");
        File[] localFiles = Local.list();
        List<Metadata> remoteFiles = new ArrayList<>();
        Dropbox.getInstance().list(remoteFiles);

        for (Metadata remoteFile: remoteFiles) {
            boolean doesExist = false;
            for (File localFile: localFiles) {
                if (remoteFile.getName().equals(localFile.getName())) {
                    doesExist = true;
                    break;
                }
            }

            if (!doesExist) {
                downloadFromCloud(remoteFile.getName());
            }
        }

        for (File localFile: localFiles) {
            boolean doesExist = false;
            for (Metadata remoteFile: remoteFiles) {
                if (localFile.getName().equals(remoteFile.getName())) {
                    doesExist = true;
                    break;
                }
            }

            if (!doesExist) {
                uploadToCloud(localFile.getName());
            }
        }
    }

    private void downloadFromCloud(String fileName) {
        System.out.println("FileSyncer || downloadFromCloud || fileName:" + fileName);
        byte[] photo = Dropbox.getInstance().download(fileName, Dropbox.FileType.PHOTO);
        byte[] key = Dropbox.getInstance().download(fileName, Dropbox.FileType.KEY);
        if (photo != null && key != null && photo.length > 0 && key.length > 0) {
            Crypto crypto = new Crypto();
            byte[] decrypted = crypto.decrypt(photo, key);

            if (decrypted != null && decrypted.length > 0) {
                byte[] localEncrypted = crypto.encrypt(decrypted, Local.getLocalKey());

                if (localEncrypted != null && localEncrypted.length > 0) {
                    Local.save(localEncrypted, fileName);
                }
            }
        }
    }

    private void uploadToCloud(String fileName) {
        System.out.println("FileSyncer || uploadToCloud || fileName:" + fileName);
        byte[] encryptedLocal = Local.getInstance().read(fileName);

        if (encryptedLocal != null && encryptedLocal.length > 0) {
            Crypto crypto = new Crypto();
            byte[] decrypted = crypto.decrypt(encryptedLocal, Local.getLocalKey());
            if (decrypted != null && decrypted.length > 0) {
                byte[] key = Crypto.generateKey();
                System.out.println("FileSyncer || uploadToCloud || key:" + new String(key));
                Crypto cryptoFunctions = new Crypto();
                byte[] encrypted = cryptoFunctions.encrypt(decrypted, key);
                Dropbox.getInstance().upload(encrypted, fileName, Dropbox.FileType.PHOTO);
                Dropbox.getInstance().upload(key, fileName, Dropbox.FileType.KEY);
            }

        }

    }
}
