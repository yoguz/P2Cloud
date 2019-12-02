package com.etuproject.p2cloud.utils.cloud;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.UploadErrorException;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Dropbox {
    private static Dropbox instance;

    // p2cloud.bil520@gmail.com - Bil420520.Siber
    // p2cloud.bil520.2@gmail.com - Bil420520.Siber

    private static String ACCESS_TOKEN = "SPbzk9LCpzAAAAAAAAAAKxIQNrmXH4_STRmQdCvuJ0gBVp2zcjtMxWXmHuuL1K3G";
    private static final String PHOTOS_PATH_PREFIX = "/photos";
    private static final String KEYS_PATH_PREFIX = "/keys";
    private static DbxRequestConfig config;
    private static DbxClientV2 client;

    public enum FileType {
        PHOTO,
        KEY
    }

    private Dropbox() {
        System.out.println("Dropbox constructor");
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

    public static void setAccessToken(String accessToken) {
        ACCESS_TOKEN = accessToken;
    }

    public static void list(List<Metadata> fileList) {
        try {
            ListFolderResult result = client.files().listFolder(PHOTOS_PATH_PREFIX);
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

    public static boolean upload(byte[] fileContent, String fileName, FileType fileType) {
        System.out.println("Dropbox:uploadPhoto | fileName:" + fileName + ", Type:" + fileType);
        try (InputStream in = new ByteArrayInputStream(fileContent)) {
            String prefix;
            if (fileType == FileType.PHOTO) {
                prefix = PHOTOS_PATH_PREFIX;
            } else {
                prefix = KEYS_PATH_PREFIX;
            }
            FileMetadata metadata = client.files().uploadBuilder(prefix + "/" + fileName)
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

    public static void delete(String fileName, FileType fileType) {
        System.out.println("Dropbox:uploadPhoto | fileName:" + fileName + ", Type:" + fileType);
        String prefix;
        if (fileType == FileType.PHOTO) {
            prefix = PHOTOS_PATH_PREFIX;
        } else {
            prefix = KEYS_PATH_PREFIX;
        }
        try {
            client.files().deleteV2(prefix + "/" + fileName);
        } catch (DbxException e) {
            e.printStackTrace();
        }
    }
}
