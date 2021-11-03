package com.uni.share.group.boundary;

import com.uni.share.group.control.GroupBA;
import com.uni.share.group.types.GroupTO;
import com.uni.share.user.types.UserBE;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Business Facade for handling logic related to groups.
 *
 * @author Felix Rottler , 07.12.2018
 **/
@Stateless
public class GroupBF {

    @Inject
    private GroupBA groupBA;


    /**
     * Creates a new group from the group transport object {@link GroupTO} .
     * Needs also a valid json-web-token to verify that the user that has an account.
     *
     * @param groupTO the group transport object created by the user
     * @param userBE  the id of the user.
     * @return the newly created group
     */
    public GroupTO create(final GroupTO groupTO, final UserBE userBE) {
        return groupBA.create(groupTO, userBE);
    }


    /**
     * Delete a group by its title
     *
     * @param title  the title of the group
     * @param userBE the id of the user
     */
    public void deleteByTitle(final String title, final UserBE userBE) {
        groupBA.deleteByTitle(title, userBE);
    }


    /**
     * Updates a group within the database.
     *
     * @param groupTO the group containing new values
     * @param userBE  the id of the user.
     * @return the updated group
     */
    public GroupTO update(final String groupToUpdate, final GroupTO groupTO, final UserBE userBE) {
        return groupBA.update(groupToUpdate, groupTO, userBE);
    }


    /**
     * Wrapper for finding a group by its title.
     *
     * @param groupTitle the title of this group
     * @return the identified group.
     */
    public GroupTO findGroupByTitle(final String groupTitle, final UserBE user) {
        return groupBA.findGroupByTitle(groupTitle, user);

    }


    public Long[] getAllUserIdsByGroupTitle(final String groupTitle){
        return groupBA.getAllUserIdsByGroupTitle(groupTitle);
    }
}
