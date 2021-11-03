package test.uni.share.actions;


import com.uni.share.actions.boundary.ActionsBF;
import com.uni.share.actions.boundary.ActionsResource;
import com.uni.share.actions.control.ActionsMapperBA;
import com.uni.share.actions.types.ActionsBE;
import com.uni.share.actions.types.ActionsTO;
import com.uni.share.authentication.session.UserTransaction;
import com.uni.share.user.types.UserBE;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ActionsResourceTest {


    @Mock
    private ActionsBF actionsBF;

    @Mock
    private ActionsMapperBA actionsMapperBA;

    @Mock
    private UserTransaction userTransaction;

    @Mock
    private UserBE currentUser;

    @InjectMocks
    private ActionsResource underTest;


    @Before
    public void setup() {
        when(currentUser.getId()).thenReturn(1L);
        when(userTransaction.getCurrentUser()).thenReturn(currentUser);


    }


    @Test
    public void test_getAllActions() {
        final ActionsBE actionsBE = mock(ActionsBE.class);
        final ActionsTO actionsTO = mock(ActionsTO.class);
        when(actionsTO.getUserId()).thenReturn(1L);


        when(actionsBF.getAllActionsForUser(any())).thenReturn(Collections.singletonList(actionsBE));
        when(actionsMapperBA.toTO(anyList())).thenReturn(Collections.singletonList(actionsTO));

        final List<ActionsTO> actual = underTest.getAllActions();

        assertThat(actual.size()).isEqualTo(1);
        assertThat(actual.get(0).getUserId()).isEqualTo(1L);

    }
}
