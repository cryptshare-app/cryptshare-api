package test.uni.share.actions;


import com.uni.share.actions.control.ActionsMapperBA;
import com.uni.share.actions.types.ActionsBE;
import com.uni.share.actions.types.ActionsTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ActionsMapperBATest {


    @InjectMocks
    private ActionsMapperBA underTest;


    @Test
    public void test_toTO() {

        final LocalDateTime time = LocalDateTime.now();
        final ActionsBE actionsBE = mock(ActionsBE.class);
        when(actionsBE.getCategory()).thenReturn("category");
        when(actionsBE.getLinkTo()).thenReturn("linkTo");
        when(actionsBE.getUserId()).thenReturn(1L);
        when(actionsBE.getMessage()).thenReturn("message");
        when(actionsBE.getType()).thenReturn("type");
        when(actionsBE.getCreatedAt()).thenReturn(time);

        final ActionsTO actual = underTest.toTO(actionsBE);
        assertThat(actual.getCategory()).isEqualTo("category");
        assertThat(actual.getLinkTo()).isEqualTo("linkTo");
        assertThat(actual.getUserId()).isEqualTo(1L);
        assertThat(actual.getMessage()).isEqualTo("message");
        assertThat(actual.getType()).isEqualTo("type");
        assertThat(actual.getCreatedAt()).isEqualTo(time);

    }
}
