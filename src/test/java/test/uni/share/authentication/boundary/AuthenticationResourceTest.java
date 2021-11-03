package test.uni.share.authentication.boundary;

import com.uni.share.authentication.boundary.AuthenticationBF;
import com.uni.share.authentication.boundary.AuthenticationResource;
import com.uni.share.authentication.types.JWTokenTO;
import com.uni.share.authentication.types.LoginTO;
import com.uni.share.user.boundary.UserBF;
import com.uni.share.user.types.UserTO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class AuthenticationResourceTest {

    @InjectMocks
    private AuthenticationResource underTest;

    @Mock
    private AuthenticationBF authenticationBF;

    @Mock
    private UserBF userBF;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void login() {
        LoginTO loginTO = new LoginTO();
        loginTO.setPassword("test");
        loginTO.setUserName("test");
        underTest.login(loginTO);

        Mockito.verify(authenticationBF, Mockito.only()).login(loginTO);
    }

    @Test
    public void register() {
        UserTO userTO = new UserTO();
        userTO.setPassword("test");
        userTO.setUserName("test");
        userTO.setEmail("test@test.de");

        underTest.register(userTO);

        Mockito.verify(userBF, Mockito.only()).create(userTO);
    }

    @Test
    public void refreshUserToken() {
        JWTokenTO jwTokenTO = new JWTokenTO();
        jwTokenTO.setAccessToken("accessToken");
        jwTokenTO.setRefreshToken("refresh");

        underTest.refreshToken(jwTokenTO);

        Mockito.verify(authenticationBF, Mockito.only()).refreshToken(jwTokenTO);
    }

    @Test
    public void test_issueToken() {
        final LoginTO loginTO = new LoginTO();
        loginTO.setPassword("test");
        loginTO.setUserName("test");
        final int time = 100;

        underTest.issueToken(loginTO, time);

        Mockito.verify(authenticationBF, Mockito.only()).issueToken(loginTO, time);
    }
}