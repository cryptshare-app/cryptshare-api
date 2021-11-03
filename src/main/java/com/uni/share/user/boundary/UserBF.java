package com.uni.share.user.boundary;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.SecurityContext;
import com.uni.share.authentication.types.JWTokenTO;
import com.uni.share.iota.boundary.IotaBF;
import com.uni.share.user.control.UserBA;
import com.uni.share.user.control.UserSecurityBA;
import com.uni.share.user.types.UserBE;
import com.uni.share.user.types.UserInformationTO;
import com.uni.share.user.types.UserTO;

import java.util.List;

/**
 * Business facade entry point for handling user related logic.
 *
 * @author Felix Rottler
 */
@Stateless
public class UserBF {

    @Inject
    private UserBA userBA;

    @Inject
    private IotaBF iotaBF;

    @Inject
    private UserSecurityBA userSecurityBA;


    /**
     * Secures user credentials and if successfully secured creates the user.
     *
     * @param userTO the user to persist
     * @return a valid jwt token.
     */
    public JWTokenTO create(final UserTO userTO) {
        return userSecurityBA.secureUserCreation(userTO, user -> {
            final UserBE createdUser = userBA.create(user);
            iotaBF.create(createdUser);
        });
    }


    public void update(final UserTO userTO, final UserBE userBE) {
        userBA.update(userTO, userBE);
    }


    public void deleteByName(final String name, final UserBE userBE) {
        // TODO
    }


    public UserBE getCurrentUser(final SecurityContext securityContext) {
        return userBA.getCurrentUser(securityContext);
    }


    public UserBE findUserByUserName(final String userName) {
        return userBA.findUserByUserName(userName);
    }


    public UserInformationTO findUserInformationById(final long userId) {
        return userBA.getUserInformationById(userId);
    }


    public UserInformationTO getCurrentUserInformation(final SecurityContext securityContext) {
        return userBA.getCurrentUserInformation(securityContext);
    }

    public List<String> fetchUserNames() {
        return userBA.fetchUserNames();
    }
}
