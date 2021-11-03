package com.uni.share.authentication.types;


import javax.validation.constraints.NotBlank;

/**
 * Transport object for credentials.
 *
 * @author Felix Rottler , 07.11.2018
 **/
public class LoginTO {

    @NotBlank
    private String userName;

    @NotBlank
    private String password;


    /**
     * Empty constructor.
     */
    public LoginTO() {
        // empty constructor.
    }


    /**
     * Parameterized constructor for logins.
     *
     * @param userName the name of the user
     * @param password the password of the user
     */
    public LoginTO(final String userName, final String password) {
        this.userName = userName;
        this.password = password;
    }


    public String getUserName() {
        return userName;
    }


    public void setUserName(final String userName) {
        this.userName = userName;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(final String password) {
        this.password = password;
    }
}
