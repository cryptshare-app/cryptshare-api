package test.uni.share.authentication.control;

import com.uni.share.authentication.control.JWTAuthenticationBA;
import com.uni.share.authentication.types.JWTokenTO;
import com.uni.share.common.control.TimeProvider;
import com.uni.share.user.types.UserBE;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JWTAuthenticationBATest {

    @InjectMocks
    private JWTAuthenticationBA underTest;

    @Mock
    private TimeProvider timeProvider;

    private Date now;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        now = new Date();
        when(timeProvider.getCurrentDate()).thenReturn(now);
    }


    @Test
    public void issueToken() {
        UserBE userBE = createMockUser();
        JWTokenTO result = underTest.issue(userBE);
        assertThat(result).isNotNull();
        assertThat(result.getRefreshToken())
                .isNotBlank()
                .isNotEmpty()
                .isNotNull();
        assertThat(result.getAccessToken())
                .isNotNull()
                .isNotEmpty()
                .isNotBlank();

        final Claims accessTokenClaims = Jwts.parser()
                .setSigningKey(JWTAuthenticationBA.key)
                .parseClaimsJws(result.getAccessToken()).getBody();

        assertThat(accessTokenClaims.getId()).isEqualTo(userBE.getId().toString());
        assertThat(accessTokenClaims.getIssuedAt()).isInSameHourAs(now);
        assertThat(accessTokenClaims.getIssuedAt()).isInSameMinuteAs(now);
        assertThat(accessTokenClaims.getIssuedAt()).isInSameYearAs(now);
        assertThat(accessTokenClaims.getIssuedAt()).isInSameDayAs(now);

        final Claims refreshTokenClaims = Jwts.parser()
                .setSigningKey(JWTAuthenticationBA.key)
                .parseClaimsJws(result.getRefreshToken()).getBody();

        assertThat(refreshTokenClaims.getId()).isEqualTo(userBE.getId().toString());
        assertThat(refreshTokenClaims.getIssuedAt()).isInSameHourAs(now);
        assertThat(refreshTokenClaims.getIssuedAt()).isInSameMinuteAs(now);
        assertThat(refreshTokenClaims.getIssuedAt()).isInSameYearAs(now);
        assertThat(refreshTokenClaims.getIssuedAt()).isInSameDayAs(now);


        assertThat(refreshTokenClaims.getExpiration()).isAfter(accessTokenClaims.getExpiration());
    }


    @Test
    public void refreshToken() {
        UserBE userBE = createMockUser();
        final JWTokenTO tokens = underTest.issue(userBE);
        final Claims originalTokenClaims = Jwts.parser()
                .setSigningKey(JWTAuthenticationBA.key)
                .parseClaimsJws(tokens.getAccessToken()).getBody();

        final JWTokenTO result = underTest.refresh(tokens);
        assertThat(result).isNotNull();
        assertThat(result.getRefreshToken())
                .isNotBlank()
                .isNotEmpty()
                .isNotNull();
        assertThat(result.getAccessToken())
                .isNotNull()
                .isNotEmpty()
                .isNotBlank();

        final Claims accessTokenClaims = Jwts.parser()
                .setSigningKey(JWTAuthenticationBA.key)
                .parseClaimsJws(result.getAccessToken()).getBody();

        assertThat(accessTokenClaims.getId()).isEqualTo(userBE.getId().toString());
        assertThat(accessTokenClaims.getIssuedAt()).isInSameHourAs(now);
        assertThat(accessTokenClaims.getIssuedAt()).isInSameMinuteAs(now);
        assertThat(accessTokenClaims.getIssuedAt()).isInSameYearAs(now);
        assertThat(accessTokenClaims.getIssuedAt()).isInSameDayAs(now);

        final Claims refreshTokenClaims = Jwts.parser()
                .setSigningKey(JWTAuthenticationBA.key)
                .parseClaimsJws(result.getRefreshToken()).getBody();

        assertThat(refreshTokenClaims.getId()).isEqualTo(userBE.getId().toString());
        assertThat(refreshTokenClaims.getIssuedAt()).isInSameHourAs(now);
        assertThat(refreshTokenClaims.getIssuedAt()).isInSameMinuteAs(now);
        assertThat(refreshTokenClaims.getIssuedAt()).isInSameYearAs(now);
        assertThat(refreshTokenClaims.getIssuedAt()).isInSameDayAs(now);


        assertThat(refreshTokenClaims.getExpiration()).isAfter(accessTokenClaims.getExpiration());

        assertThat(originalTokenClaims.getId()).isEqualTo(accessTokenClaims.getId());
        assertThat(originalTokenClaims.getIssuer()).isEqualTo(accessTokenClaims.getIssuer());
    }

    private UserBE createMockUser() {
        UserBE userBE = mock(UserBE.class);
        when(userBE.getId()).thenReturn(1L);
        when(userBE.getUserName()).thenReturn("test");
        return userBE;
    }
}