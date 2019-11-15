package com.etuproject.p2cloud.utils.cloud;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.UploadErrorException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Dropbox {
    private static Dropbox instance;

    private static final String ACCESS_TOKEN = "SPbzk9LCpzAAAAAAAAAADqikekU4CqzW4atXPrjoT9Zcu9nsQuW5Yzoxr0kXDR8f";
    private static DbxRequestConfig config;
    private static DbxClientV2 client;

    private Dropbox() {
        config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
        client = new DbxClientV2(config, ACCESS_TOKEN);

        /*FullAccount account = client.users().getCurrentAccount();
        System.out.println(account.getName().getDisplayName());

        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));*/

        /*List<Metadata> fileList = new ArrayList<>();
        list("", fileList);
        for (Metadata metadata : fileList) {
            System.out.println(metadata.getPathLower());
        }
        upload("files/test.txt", "/test.txt");*/
    }

    public static Dropbox getInstance() {
        if (instance == null) {
            instance = new Dropbox();
        }
        return instance;
    }

    public static void list(String folderPath, List<Metadata> fileList)  {
        try{
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

    public static boolean upload(String localFilePath, String remoteFilePath) {
        System.out.println("Dropbox:upload | localPath:" + localFilePath);
        try (InputStream in = new FileInputStream(localFilePath)) {
            FileMetadata metadata = client.files().uploadBuilder("/" + remoteFilePath)
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
}
