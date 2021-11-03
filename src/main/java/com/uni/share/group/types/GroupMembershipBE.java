package com.uni.share.group.types;

import com.uni.share.common.types.AbstractBaseBE;
import com.uni.share.group.types.enums.GroupMembershipStatus;
import com.uni.share.group.types.enums.GroupRoles;
import com.uni.share.user.types.UserBE;

import javax.persistence.*;

/**
 * Business entity for group memberships.
 * Maps a group with a user and the corresponding role and invitation status
 *
 * @author Felix Rottler , 07.12.2018
 **/

@Entity
@Table(name = "t_group_memberships")
public class GroupMembershipBE extends AbstractBaseBE {


    @Id
    @GeneratedValue(generator = "g_t_group_memberships")
    @SequenceGenerator(name = "g_t_group_memberships",
            sequenceName = "sq_group_memberships", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private GroupBE groupBE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserBE userBE;


    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private GroupRoles role;


    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private GroupMembershipStatus status;

    @Column(name = "auto_pay")
    private boolean autoPay;


    /**
     * Empty constructor.
     */
    public GroupMembershipBE() {
        // needs to be empty
    }


    public boolean getAutoPay() {
        return autoPay;
    }

    public void setAutoPay(boolean autoPay) {
        this.autoPay = autoPay;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public GroupBE getGroupBE() {
        return groupBE;
    }

    public void setGroupBE(final GroupBE groupBE) {
        this.groupBE = groupBE;
    }

    public UserBE getUserBE() {
        return userBE;
    }

    public void setUserBE(final UserBE userBE) {
        this.userBE = userBE;
    }

    public GroupRoles getRole() {
        return role;
    }

    public void setRole(final GroupRoles role) {
        this.role = role;
    }

    public GroupMembershipStatus getStatus() {
        return status;
    }

    public void setStatus(final GroupMembershipStatus status) {
        this.status = status;
    }
}
