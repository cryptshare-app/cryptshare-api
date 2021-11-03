package com.uni.share.actions.boundary;


import com.uni.share.actions.entity.ActionsEM;
import com.uni.share.actions.types.ActionsBE;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Facade  for wrapping and executing logic related to events that occur in cryptshare
 */
@Stateless
public class ActionsBF {
    @Inject
    private ActionsEM actionsEM;


    /**
     * Get all actions for a user
     *
     * @param _userId the id of the user
     * @return a list of actions
     */
    public List<ActionsBE> getAllActionsForUser(Long _userId) {
        Optional<List<ActionsBE>> o = actionsEM.findActionsByUserId(_userId);
        return o.orElseGet(ArrayList::new);
    }

    /**
     * Create a new action based on the given types
     *
     * @param _category
     * @param _type
     * @param _message
     * @param _linkTo
     * @param _userId
     */
    public ActionsBE create(String _category, String _type, String _message, String _linkTo, Long _userId) {
        ActionsBE newAction = new ActionsBE();
        newAction.setCategory(_category);
        newAction.setType(_type);
        newAction.setMessage(_message);
        newAction.setLinkTo(_linkTo);
        newAction.setUserId(_userId);
        return actionsEM.persist(newAction);
    }
}
