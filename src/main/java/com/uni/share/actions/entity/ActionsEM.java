package com.uni.share.actions.entity;

import com.uni.share.actions.types.ActionsBE;
import com.uni.share.common.entity.AbstractBaseEM;

import java.util.List;
import java.util.Optional;

import static com.uni.share.actions.types.QActionsBE.actionsBE;

public class ActionsEM extends AbstractBaseEM<ActionsBE> {

    public static final String CATEGORY_REVENUES = "Revenues";
    public static final String CATEGORY_EXPENSES = "Expenses";
    public static final String CATEGORY_GROUP = "Group";
    public static final String CATEGORY_PAYMENTS = "Payments";
    public static final String CATEGORY_OTHER = "Other";


    @Override
    protected Class<ActionsBE> getEntityClass() {
        return ActionsBE.class;
    }

    public Optional<List<ActionsBE>> findActionsByUserId(final Long _userID) {
        return Optional.ofNullable(queryFactory().selectFrom(actionsBE)
                .where(actionsBE.userId.eq(_userID)).fetch());
    }
}
