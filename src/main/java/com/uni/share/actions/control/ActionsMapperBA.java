package com.uni.share.actions.control;

import com.uni.share.actions.types.ActionsBE;
import com.uni.share.actions.types.ActionsTO;

import javax.ejb.Stateless;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for transforming TO's to BE's and vice versa.
 */
@Stateless
public class ActionsMapperBA {


    /**
     * Map a given actions business entity to the corresponding transport object
     *
     * @param be the business entity to map
     * @return the mapped action transport object.
     */
    public ActionsTO toTO(ActionsBE be) {
        ActionsTO actionsTO = new ActionsTO();
        actionsTO.setCategory(be.getCategory());
        actionsTO.setType(be.getType());
        actionsTO.setMessage(be.getMessage());
        actionsTO.setLinkTo(be.getLinkTo());
        actionsTO.setUserId(be.getUserId());
        actionsTO.setCreatedAt(be.getCreatedAt());
        return actionsTO;
    }

    public List<ActionsTO> toTO(List<ActionsBE> bes) {
        return bes.stream().map(this::toTO).collect(Collectors.toList());
    }
}
