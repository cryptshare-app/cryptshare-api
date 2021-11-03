package com.uni.share.user.control;

import com.uni.share.authentication.control.JWTAuthenticationBA;
import com.uni.share.authentication.types.JWTokenTO;
import com.uni.share.common.exceptions.BusinessValidationException;
import com.uni.share.common.exceptions.errors.CryptShareErrors;
import com.uni.share.user.entity.UserEM;
import com.uni.share.user.types.UserBE;
import com.uni.share.user.types.UserTO;
import org.mindrot.jbcrypt.BCrypt;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Security handler for users.
 *
 * @author Felix Rottler , 08.11.2018
 **/
@Stateless
public class UserSecurityBA {

    @Inject
    private UserEM userEM;

    @Inject
    private JWTAuthenticationBA jwtAuthenticationBA;

    @Inject
    private UserFactoryBA userFactoryBA;


    /**
     * Start a secure user creation
     *
     * @param userTO  the user transport object to secure
     * @param success the success callback if user is valid.
     * @return the valid jwt token.
     */
    public JWTokenTO secureUserCreation(final UserTO userTO, final Consumer<UserBE> success) {
        final String passwordSalt = generateUniqueSalt();
        final String passwordHash = generateHash(userTO.getPassword(), passwordSalt);
        // TODO Check if user with this salt and hash already exists?
        Optional<UserBE> userBE = userEM.findByUserNameOrEmail(userTO.getUserName(), userTO.getEmail());
        userBE.ifPresent(user -> {
            throw new BusinessValidationException(CryptShareErrors.USER_ALREADY_EXISTS, Response.Status.CONFLICT);
        });
        final UserBE newUserBE = userFactoryBA.create(userTO.getUserName(),
                userTO.getEmail(),
                passwordHash,
                passwordSalt);
        success.accept(newUserBE);
        return jwtAuthenticationBA.issue(newUserBE);
    }


    private String generateHash(final String password, final String passwordSalt) {
        return BCrypt.hashpw(password, passwordSalt);
    }


    private String generateUniqueSalt() {
        return BCrypt.gensalt();
    }
}
