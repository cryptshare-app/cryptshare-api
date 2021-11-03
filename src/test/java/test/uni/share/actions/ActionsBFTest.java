package test.uni.share.actions;


import com.uni.share.actions.boundary.ActionsBF;
import com.uni.share.actions.entity.ActionsEM;
import com.uni.share.actions.types.ActionsBE;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ActionsBFTest {


    @Mock
    private ActionsEM actionsEM;
    @InjectMocks
    private ActionsBF underTest;

    @Test
    public void test_create() {
        final ActionsBE actual = underTest.create("category", "type",
                "message", "linkTo", 1L);
        verify(actionsEM).persist(any());
    }


    @Test
    public void test_getAllActionsForUser() {

        when(actionsEM.findActionsByUserId(any())).thenReturn(Optional.of(Collections.emptyList()));
        final List<ActionsBE> actual = underTest.getAllActionsForUser(1L);

        assertThat(actual).isEmpty();


    }
}
