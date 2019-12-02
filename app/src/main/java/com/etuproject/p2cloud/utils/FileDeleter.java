package com.etuproject.p2cloud.utils;

import com.etuproject.p2cloud.utils.cloud.Dropbox;
import com.etuproject.p2cloud.utils.local.Local;

public class FileDeleter implements Runnable {

    public String fileName;

    FileDeleter(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void run() {
        deleteFromLocal(fileName);
        deleteFromCloud(fileName, fileName);
    }

    private static void deleteFromLocal(String fileName) {
        Local.getInstance().delete(fileName);
    }

    private static void deleteFromCloud(String fileName, String fileHash) {
        Dropbox.getInstance().delete(fileName, Dropbox.FileType.PHOTO);
        Dropbox.getInstance().delete(fileHash, Dropbox.FileType.KEY);
    }
}
