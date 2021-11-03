package com.uni.share.user.entity;

import java.util.List;
import java.util.Optional;

import com.uni.share.common.entity.AbstractBaseEM;
import com.uni.share.user.types.UserBE;

import static com.uni.share.user.types.QUserBE.userBE;

/**
 * The entity manager for users
 *
 * @author Felix Rottler
 */
public class UserEM extends AbstractBaseEM<UserBE> {

    @Override
    protected Class<UserBE> getEntityClass() {
        return UserBE.class;
    }


    /**
     * Finds a given user by user name or email
     *
     * @param userName the name of the user
     * @param email    the email of the user
     * @return an optional containing the potential user.
     */
    public Optional<UserBE> findByUserNameOrEmail(final String userName, final String email) {
        return Optional.ofNullable(
                queryFactory()
                        .selectFrom(userBE)
                        .where(userBE.userName.eq(userName).or(userBE.email.eq(email)))
                        .fetchOne());
    }


    /**
     * Find a user by user name
     *
     * @param userName the name of the user
     * @return an optional containing the potential user.
     */
    public Optional<UserBE> findByUserName(final String userName) {
        return Optional.ofNullable(
                queryFactory()
                        .selectFrom(userBE)
                        .where(userBE.userName.eq(userName))
                        .fetchOne());
    }

    public List<String> getAllUserNames() {
        return queryFactory().select(userBE.userName).from(userBE).fetch();
    }
}
