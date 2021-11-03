package test.uni.share.authentication.boundary;


import com.uni.share.authentication.boundary.AuthenticationBF;
import com.uni.share.authentication.control.AuthenticationBA;
import com.uni.share.authentication.control.JWTAuthenticationBA;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;

public class AuthenticationBFTest {

    @InjectMocks
    private AuthenticationBF underTest;

    @Mock
    private AuthenticationBA authenticationBA;

    @Mock
    private JWTAuthenticationBA jwtAuthenticationBA;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void login() {
        underTest.login(any());
        Mockito.verify(authenticationBA, Mockito.only()).login(any());
    }

    @Test
    public void refreshToken() {
        underTest.refreshToken(any());
        Mockito.verify(jwtAuthenticationBA, Mockito.only()).refresh(any());

    }
}