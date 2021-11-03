package test.uni.share.group.control;

import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.uni.share.group.control.GroupMapperBA;
import com.uni.share.group.control.membership.GroupMembershipMapperBA;
import com.uni.share.group.types.GroupBE;
import com.uni.share.group.types.GroupMembershipBE;
import com.uni.share.group.types.GroupTO;
import com.uni.share.group.types.enums.GroupMembershipStatus;
import com.uni.share.group.types.enums.GroupRoles;
import com.uni.share.user.types.UserBE;
import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * TODO
 *
 * @author Felix Rottler, MaibornWolff GmbH
 */
public class GroupMapperBATest {

    @InjectMocks
    private GroupMapperBA underTest;

    @Mock
    private GroupMembershipMapperBA membershipMapperBA;


    private GroupBE groupBE;
    private GroupTO groupTO;
    private UserBE userBE;
    private GroupMembershipBE groupMembershipBE;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userBE = new UserBE();
        groupMembershipBE = new GroupMembershipBE();
        groupMembershipBE.setId(4L);
        groupMembershipBE.setUserBE(userBE);
        groupMembershipBE.setGroupBE(groupBE);
        groupMembershipBE.setStatus(GroupMembershipStatus.ACCEPTED);
        groupMembershipBE.setRole(GroupRoles.OWNER);
        userBE.setId(1L);

        groupTO = new GroupTO("title", "description", Collections.emptyList(), Collections.emptyList(), null, "");
        groupBE = new GroupBE();
        groupBE.setDescription("description");
        groupBE.setTitle("title");
        groupBE.setUserBE(userBE);
        groupBE.setGroupMembershipBES(Collections.singletonList(groupMembershipBE));
    }


    @Test
    public void toBE() {
        final GroupBE result = underTest.toBE(groupTO, userBE);
        assertThat(result.getUserBE()).isEqualTo(userBE);
        assertThat(result.getTitle()).isEqualTo(groupTO.getTitle());
        assertThat(result.getDescription()).isEqualTo(groupTO.getDescription());
        //assertThat(result.getGroupMembershipBES()).isEqualTo(Collections.singletonList());
    }

}