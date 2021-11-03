package test.uni.share.authentication.control;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import com.uni.share.authentication.control.AuthenticationBA;
import com.uni.share.authentication.control.JWTAuthenticationBA;
import com.uni.share.authentication.types.LoginTO;
import com.uni.share.common.exceptions.BusinessValidationException;
import com.uni.share.common.exceptions.errors.CryptShareErrors;
import com.uni.share.user.control.UserBA;
import com.uni.share.user.types.UserBE;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationBATest {

    @InjectMocks
    private AuthenticationBA underTest;

    @Mock
    private UserBA userBA;

    @Mock
    private JWTAuthenticationBA jwtAuthenticationBA;


    @Test
    public void login_invalidPassword() {
        final UserBE userBEMock = mock(UserBE.class);
        final LoginTO loginTOMock = mock(LoginTO.class);
        when(loginTOMock.getPassword()).thenReturn("password");
        when(userBEMock.getPasswordHash()).thenReturn("$2a$05$bvIG6Nmid91Mu9RcmmWZfO5HJIMCT8riNW0hEp8f6/FuA2/mHZFpp");
        when(userBA.findUserByUserName(any())).thenReturn(userBEMock);

        final String errorMessage = CryptShareErrors.USER_PASSWORD_INVALID.toString();
        assertThatThrownBy(() -> underTest.login(loginTOMock)).isInstanceOf(BusinessValidationException.class)
                .hasMessage(errorMessage);
    }


    @Test
    public void issueToken() {
        final UserBE userBEMock = mock(UserBE.class);
        final LoginTO loginTOMock = mock(LoginTO.class);
        when(loginTOMock.getPassword()).thenReturn("password");
        when(userBEMock.getPasswordHash()).thenReturn("$2a$05$bvIG6Nmid91Mu9RcmmWZfO5HJIMCT8riNW0hEp8f6/FuA2/mHZFpe");
        when(userBA.findUserByUserName(any())).thenReturn(userBEMock);


        underTest.issueToken(loginTOMock, 20);
        verify(jwtAuthenticationBA, only()).issue(userBEMock, 20);
    }
}