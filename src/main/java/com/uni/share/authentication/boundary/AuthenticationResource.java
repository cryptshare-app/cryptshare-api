package com.uni.share.authentication.boundary;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.uni.share.authentication.types.JWTokenTO;
import com.uni.share.authentication.types.LoginTO;
import com.uni.share.user.boundary.UserBF;
import com.uni.share.user.types.UserTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * <p>Authentication Resource provides endpoints for users
 * to authenticate like login and registration or refreshing jwt tokens.</p>
 *
 * @author Felix Rottler , 07.11.2018
 */
@Path("authentication")
@Api(tags = "Authentication")
public class AuthenticationResource {

    @Inject
    private AuthenticationBF authenticationBF;

    @Inject
    private UserBF userBF;


    /**
     * Empty constructor.
     */
    public AuthenticationResource() {
        // empty constructor.
    }


    /**
     * Endpoint for authenticate credentials
     *
     * @param loginTO the credentials to authenticate
     * @return a response containing a valid jwt token.
     */
    @Path("login")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Login with Credentials")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Login successful", response = JWTokenTO.class),
            @ApiResponse(code = 400, message = "Provided Credentials are invalid"),

    })
    public JWTokenTO login(@ApiParam(value = "Credentials for login", required = true) final LoginTO loginTO) {
        return authenticationBF.login(loginTO);
    }


    /**
     * Endpoint for registering new user's
     *
     * @param userTO the user to register.
     * @return a response containing a valid jwt token.
     */
    @Path("register")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Register a a new User for Share")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Registration successful", response = JWTokenTO.class),
            @ApiResponse(code = 400, message = "Provided Information are invalid"),

    })
    public JWTokenTO register(
            @ApiParam(value = "User Information for Registration", required = true)
            @Valid final UserTO userTO) {
        return userBF.create(userTO);
    }


    /**
     * Endpoint for refreshing tokens for users.
     *
     * @param token The tokens to refresh
     * @return the new access- and refresh token.
     */
    @Path("refresh")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Refresh Access und Refresh Token for a given User by JWToken")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Refresh successful for User", response = JWTokenTO.class),
    })
    public JWTokenTO refreshToken(
            @ApiParam(value = "The Tokens to refresh", required = true) final JWTokenTO token) {
        return authenticationBF.refreshToken(token);
    }


    @Path("token/{time}")
    @POST
    @ApiOperation(value = "Issue Tokens for a given User")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Generating Token was successful", response = JWTokenTO.class),
            @ApiResponse(code = 400, message = "Provided Information are invalid"),
            @ApiResponse(code = 403, message = "User not allowed to issue a new Token")

    })
    public JWTokenTO issueToken(
            @ApiParam(name = "User credentials for retrieving a new token", required = true) final LoginTO loginTO,
            @ApiParam(name = "Time the Token will be valid in minutes")
            @PathParam(value = "time") final int time) {
        return authenticationBF.issueToken(loginTO, time);
    }


    @Path("validate/{token}")
    @GET
    @ApiOperation(value = "Validate a token")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful validated token", response = String.class)
    })
    public void validateToken(@NotNull @PathParam(value = "token") final String token) {
        authenticationBF.validateToken(token);
    }


}
