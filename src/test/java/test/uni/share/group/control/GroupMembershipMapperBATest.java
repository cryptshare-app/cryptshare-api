package test.uni.share.group.control;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import com.uni.share.group.control.membership.GroupMembershipMapperBA;
import com.uni.share.group.types.GroupBE;
import com.uni.share.group.types.GroupMembershipBE;
import com.uni.share.group.types.GroupMembershipTO;
import com.uni.share.group.types.enums.GroupMembershipStatus;
import com.uni.share.group.types.enums.GroupRoles;
import com.uni.share.user.types.UserBE;
import static org.assertj.core.api.Java6Assertions.assertThat;


/**
 * TODO
 *
 * @author Felix Rottler, MaibornWolff GmbH
 */
public class GroupMembershipMapperBATest {

    @InjectMocks
    private GroupMembershipMapperBA underTest;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void toTO() {
        final GroupMembershipBE source = new GroupMembershipBE();
        final GroupBE groupBE = new GroupBE();
        final UserBE userBE = new UserBE();
        userBE.setUserName("userName");
        userBE.setId(1L);

        groupBE.setUserBE(userBE);
        groupBE.setTitle("groupTitle");
        groupBE.setId(1L);

        source.setStatus(GroupMembershipStatus.ACCEPTED);
        source.setGroupBE(groupBE);
        source.setRole(GroupRoles.MEMBER);
        source.setUserBE(userBE);

        final GroupMembershipTO result = underTest.toTO(source);
        assertThat(result.getGroupTitle()).isEqualTo(groupBE.getTitle());
        assertThat(result.getInvitationStatus()).isEqualTo(source.getStatus());
        assertThat(result.getUserName()).isEqualTo(userBE.getUserName());
        assertThat(result.getUserRole()).isEqualTo(source.getRole());
    }
}