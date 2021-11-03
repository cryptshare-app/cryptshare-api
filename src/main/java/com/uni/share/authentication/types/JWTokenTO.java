package com.uni.share.authentication.types;

/**
 * Simple transport object for jwt tokens.
 *
 * @author Felix Rottler , 07.11.2018
 **/
public class JWTokenTO {

    private String accessToken;
    private String refreshToken;


    public JWTokenTO() {
    }


    public JWTokenTO(final String accessToken, final String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }


    public String getRefreshToken() {
        return refreshToken;
    }


    public void setRefreshToken(final String refreshToken) {
        this.refreshToken = refreshToken;
    }


    public String getAccessToken() {
        return accessToken;
    }


    public void setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
    }


}
