package com.uni.share.images.control;

import java.io.InputStream;
import java.util.UUID;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import com.uni.share.images.boundary.GoogleCloudClient;

/**
 * Business activity for handling logic related to hosting images in the cloud
 *
 * @author Felix Rottler, MaibornWolff GmbH
 */
public class ImageBA {

    @Inject
    private GoogleCloudClient googleCloudClient;


    /**
     * Upload a given image.
     *
     * @param imageInputStream image input stream
     * @param body             the body of the request
     * @param id               the id of the user that performs this request
     * @return the url where the image can then be received.
     */
    public String uploadImage(final InputStream imageInputStream, final FormDataBodyPart body, final Long id) {
        final String mimeType = body.getMediaType().toString();
        if (!mimeType.startsWith("image")) {
            return StringUtils.EMPTY;
        }
        final String fileName = body.getFormDataContentDisposition().getFileName();
        final String uploadName = "image_" + fileName + id + "_" + UUID.randomUUID().toString();

        return googleCloudClient.upload(uploadName, imageInputStream);
    }
}
