package com.uni.share.authentication.control;

import com.uni.share.authentication.types.JWToken;
import com.uni.share.authentication.types.JWTokenTO;
import com.uni.share.authentication.types.ValidationResult;
import com.uni.share.common.control.TimeProvider;
import com.uni.share.common.exceptions.BusinessValidationException;
import com.uni.share.user.types.UserBE;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.time.DateUtils;

import javax.crypto.SecretKey;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Date;

import static com.uni.share.common.exceptions.errors.CryptShareErrors.JWT_AUTHORIZATION_INVALID;
import static com.uni.share.common.exceptions.errors.CryptShareErrors.USER_ID_INVALID;

/**
 * Business activity for handling json web token related logic like issuing a new token or validating an existing one.
 *
 * @author Felix Rottler , 07.11.2018
 */
public class JWTAuthenticationBA {


    private static final String JWT_ISSUER = "share";
    private static final String JWT_USERNAME_CLAIM = "user";
    private static final int JWT_ACCESS_EXPIRATION_FACTOR_IN_MINUTES = 1500;
    private static final int JWT_REFRESH_EXPIRATION_FACTOR_IN_MINUTES = 100800;
    public static final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final int BEARER_LENGTH = 7;


    @Inject
    private TimeProvider timeProvider;


    /**
     * Extract the id of the user, who requested the JSON-Web-Token.
     *
     * @param jwToken the json-web-token as string.
     * @return the id of the user.
     */
    public Long getUserId(final String jwToken) {
        final Claims claims = getClaims(jwToken, JWToken.ACCESS_TOKEN);
        return Long.parseLong(claims.getId());
    }


    /**
     * Extract a json web token from a given authentication header in String Format
     * Throws an exception if the header does not start with "Bearer"
     *
     * @param authenticationHeader Authentication header (Bearer xyz)
     * @return the extracted json web token.
     */
    public String extractToken(final String authenticationHeader) {
        if (!authenticationHeader.startsWith("Bearer ")) {
            throw new BusinessValidationException(JWT_AUTHORIZATION_INVALID, Response.Status.BAD_REQUEST);
        }
        return authenticationHeader.substring(BEARER_LENGTH);
    }


    /**
     * Issues a JSON-Web-Token Object {@link JWTokenTO} containing an access-token and
     * an Refresh-Token for given user {@link UserBE}.
     * <p>
     * The Token has the following fields
     * issuer = "share"
     * expiration_date = currentDate + 15 Minutes (or 7 Days if Refresh Token)
     * username_claim = the name of the user.
     *
     * @param userBE the user to issue the token for.
     * @return a valid jwt token.
     */
    public JWTokenTO issue(final UserBE userBE) {
        return issue(userBE, 0);
    }


    public JWTokenTO issue(final UserBE userBE, final int time) {
        final String accessToken = generateToken(userBE.getUserName(),
                userBE.getId(),
                JWT_ACCESS_EXPIRATION_FACTOR_IN_MINUTES + time);
        final String refreshToken = generateToken(userBE.getUserName(),
                userBE.getId(),
                JWT_REFRESH_EXPIRATION_FACTOR_IN_MINUTES + time);

        return new JWTokenTO(accessToken, refreshToken);
    }


    /**
     * Refresh an incoming JSON-Web-Token Object {@link JWTokenTO}.
     * Refresh is only possible if the tokens are signed with the internal key
     * and if hte Refresh-Token is not already expired.
     *
     * @param current The current json web token wrapper
     * @return the refreshed JSON-Web-Token Object.
     */
    public JWTokenTO refresh(final JWTokenTO current) {
        final Claims refreshTokenClaims = getClaims(current.getRefreshToken(), JWToken.REFRESH_TOKEN);
        final String userName = refreshTokenClaims.get(JWT_USERNAME_CLAIM, String.class);
        final long id;
        try {
            id = Long.parseLong(refreshTokenClaims.getId());
        } catch (final NumberFormatException e) {
            // TODO This should be a TechnicalException
            throw new BusinessValidationException(USER_ID_INVALID, Response.Status.BAD_REQUEST);
        }
        final String accessToken = generateToken(userName,
                id,
                JWT_ACCESS_EXPIRATION_FACTOR_IN_MINUTES);
        final String refreshToken = generateToken(userName,
                id,
                JWT_REFRESH_EXPIRATION_FACTOR_IN_MINUTES);

        return new JWTokenTO(accessToken, refreshToken);
    }


    public ValidationResult validate(final String token) {
        try {
            Jwts.parser()
                    .setSigningKey(key)
                    .requireIssuer(JWT_ISSUER)
                    .parseClaimsJws(token);
            return new ValidationResult(true);
        } catch (final JwtException e) {
            return new ValidationResult(false);
        }


    }


    private Claims getClaims(final String jwToken, final JWToken type) {
        validate(jwToken).orElseThrow(JWTValidationException::new);
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(jwToken).getBody();
    }


    private String generateToken(final String userName,
                                 final Long userId,
                                 final int expirationTime) {
        final Date issuedAt = timeProvider.getCurrentDate();
        final Date expirationDate = DateUtils.addMinutes(issuedAt, expirationTime);

        return Jwts.builder()
                .setId(userId.toString())
                .setIssuer(JWT_ISSUER)
                .setIssuedAt(issuedAt)
                .setExpiration(expirationDate)
                .signWith(key)
                .claim(JWT_USERNAME_CLAIM, userName)
                .compact();
    }


}
