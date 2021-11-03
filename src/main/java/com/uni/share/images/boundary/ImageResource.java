package com.uni.share.images.boundary;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import com.uni.share.authentication.filter.JWTSecured;
import com.uni.share.authentication.session.UserTransaction;


/**
 * Resource endpoint for uploading images
 *
 * @author Felix Rottler, MaibornWolff GmbH
 */
@Path("images")
@JWTSecured
public class ImageResource {

    @Inject
    private ImageBF imageBF;

    @Inject
    private UserTransaction tx;

    private static String PLACEHOLDER_IMAGE = "https://via.placeholder.com/150";


    @GET
    public String uploadImage() {
        return PLACEHOLDER_IMAGE;
        //return imageBF.uploadImage(imageInputStream, body, tx.getCurrentUser());
    }
}
