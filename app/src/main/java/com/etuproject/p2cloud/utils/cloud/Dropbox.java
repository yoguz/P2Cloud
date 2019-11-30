package com.etuproject.p2cloud.utils.cloud;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.UploadErrorException;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Dropbox {
    private static Dropbox instance;

    // p2cloud.bil520@gmail.com - Bil420520.Siber
    // p2cloud.bil520.2@gmail.com - Bil420520.Siber

    private static final String ACCESS_TOKEN = "SPbzk9LCpzAAAAAAAAAADqikekU4CqzW4atXPrjoT9Zcu9nsQuW5Yzoxr0kXDR8f";
    private static final String PHOTOS_PATH_PREFIX = "/photos";
    private static final String KEYS_PATH_PREFIX = "/keys";
    private static DbxRequestConfig config;
    private static DbxClientV2 client;

    private Dropbox() {
        config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
        client = new DbxClientV2(config, ACCESS_TOKEN);

        try {
            client.files().createFolderV2(PHOTOS_PATH_PREFIX);
        } catch (DbxException e) {
            System.out.println("Error occurred while creating PHOTOS folder in Dropbox.");
        }

        try {
            client.files().createFolderV2(KEYS_PATH_PREFIX);
        } catch (DbxException e) {
            System.out.println("Error occurred while creating KEYS folder in Dropbox.");
        }
    }

    public static Dropbox getInstance() {
        if (instance == null) {
            instance = new Dropbox();
        }
        return instance;
    }

    public static void list(String folderPath, List<Metadata> fileList) {
        try {
            ListFolderResult result = client.files().listFolder(folderPath);
            while (true) {
                for (Metadata metadata : result.getEntries()) {
                    fileList.add(metadata);
                }
                if (!result.getHasMore()) {
                    break;
                }
                result = client.files().listFolderContinue(result.getCursor());
            }
        } catch (DbxException e) {
            e.printStackTrace();
        }
    }

    public static boolean uploadPhoto(String filePath, String remotePath) {
        System.out.println("Dropbox:uploadPhoto | localPath:" + filePath + ", remotePath:" + PHOTOS_PATH_PREFIX + remotePath);
        try (InputStream in = new FileInputStream(filePath)) {
            FileMetadata metadata = client.files().uploadBuilder(PHOTOS_PATH_PREFIX + remotePath)
                    .uploadAndFinish(in);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UploadErrorException e) {
            e.printStackTrace();
        } catch (DbxException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean uploadKey(String key, String remotePath) {
        System.out.println("Dropbox:uploadPhoto | key:" + key + ", remotePath:" + KEYS_PATH_PREFIX + remotePath);
        try (InputStream in = new ByteArrayInputStream(key.getBytes())) {
            FileMetadata metadata = client.files().uploadBuilder(KEYS_PATH_PREFIX + remotePath)
                    .uploadAndFinish(in);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UploadErrorException e) {
            e.printStackTrace();
        } catch (DbxException e) {
            e.printStackTrace();
        }
        return false;
    }
}
