package com.uni.share.images.boundary;

import java.io.InputStream;
import javax.inject.Inject;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import com.uni.share.images.control.ImageBA;
import com.uni.share.user.types.UserBE;

/**
 * Facade for wrapping image hosting
 *
 * @author Felix Rottler, MaibornWolff GmbH
 */
public class ImageBF {

    @Inject
    private ImageBA imageBA;


    /**
     * Upload a parts of multiform data to an cloud storage
     *
     * @param imageInputStream the image input stream
     * @param body             the body of the request
     * @param currentUser      the user that has performed the request
     * @return the url where the image can then be received.
     */
    public String uploadImage(final InputStream imageInputStream,
                              final FormDataBodyPart body,
                              final UserBE currentUser) {

        return imageBA.uploadImage(imageInputStream, body, currentUser.getId());
    }
}
