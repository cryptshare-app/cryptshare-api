package com.uni.share.images.boundary;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import javax.annotation.PostConstruct;
import javax.ejb.Startup;
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

/**
 * HTTP Client to gain access to google cloud.
 *
 * @author Felix Rottler, MaibornWolff GmbH
 */
@Startup
public class GoogleCloudClient {

    private Credentials credentials;
    private Storage storage;

    @Inject
    @ConfigProperty(name = "GCLOUD_PROJECT_ID")
    private String PROJECT_ID;

    @Inject
    @ConfigProperty(name = "GCLOUD_KEY_FILE_PATH")
    private String KEY_FILE_PATH;


    /**
     * Initally setup a connection to the google cloud bucket.
     */
    @PostConstruct
    public void setup() {
        try {
            credentials = GoogleCredentials
                    .fromStream(
                            getClass()
                                    .getClassLoader()
                                    .getResourceAsStream(KEY_FILE_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
        storage = StorageOptions.newBuilder().setCredentials(credentials)
                .setProjectId(PROJECT_ID).build().getService();
    }


    /**
     * Upload a given given image to the previously initialized cloud strage
     *
     * @param fileName         the name of the file
     * @param imageInputStream the stream containg the bytes for the image.
     * @return the url where to image can be retrieved.
     */
    public String upload(final String fileName, final InputStream imageInputStream) {
        Blob blobInfo = storage.create(
                BlobInfo
                        .newBuilder("share_project", fileName)
                        // Modify access list to allow all users with link to read file
                        .setAcl(new ArrayList<>(Arrays.asList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))))
                        .build(),
                imageInputStream);
        return blobInfo.getMediaLink();
    }

}
