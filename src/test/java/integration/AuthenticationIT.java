package integration;

import org.arquillian.ape.api.UsingDataSet;
import org.arquillian.ape.rdbms.ApplyScriptBefore;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.uni.share.authentication.types.JWTokenTO;
import com.uni.share.authentication.types.LoginTO;
import com.uni.share.common.exceptions.types.BusinessErrorTO;
import com.uni.share.user.types.UserTO;
import static com.uni.share.common.exceptions.errors.CryptShareErrors.JWT_TOKEN_INVALID;
import static com.uni.share.common.exceptions.errors.CryptShareErrors.USER_ALREADY_EXISTS;
import static com.uni.share.common.exceptions.errors.CryptShareErrors.USER_EMAIL_INVALID;
import static com.uni.share.common.exceptions.errors.CryptShareErrors.USER_NAME_INVALID;
import static com.uni.share.common.exceptions.errors.CryptShareErrors.USER_NOT_FOUND;
import static com.uni.share.common.exceptions.errors.CryptShareErrors.USER_PASSWORD_INVALID;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;


@RunWith(Arquillian.class)
public class AuthenticationIT extends IntegrationTestSuite {


    @Test
    @ApplyScriptBefore({"clean_users.sql", "clean_iota.sql", "clean_iota_transactions.sql"})
    public void register_success() {
        final UserTO user = new UserTO();
        user.setPassword("test");
        user.setUserName("test");
        user.setEmail("test@test.de");

        final JWTokenTO response = given()
                .contentType("application/json")
                .body(user)
                .when()
                .post("authentication/register")
                .then()
                .assertThat().statusCode(200).extract().as(JWTokenTO.class);
    }


    @Test
    @UsingDataSet("users.json")
    @ApplyScriptBefore({"clean_users.sql", "clean_iota.sql", "clean_iota_transactions.sql"})
    public void register_userAlreadyExists() {
        final UserTO user = new UserTO();
        user.setPassword("test");
        user.setUserName("test");
        user.setEmail("test@test.de");

        final BusinessErrorTO result = given().contentType("application/json")
                .body(user)
                .when()
                .post("authentication/register")
                .thenReturn().as(BusinessErrorTO.class);
        assertThat(result.getErrorMessage()).isEqualTo(USER_ALREADY_EXISTS.toString());
    }


    @Test
    @ApplyScriptBefore({"clean_users.sql", "clean_iota.sql", "clean_iota_transactions.sql"})
    public void register_invalidUserName() {
        final UserTO user = new UserTO();
        user.setPassword("test");
        user.setUserName("");
        user.setEmail("test@test.de");

        given().contentType("application/json")
                .body(user)
                .when()
                .post("authentication/register")
                .then().statusCode(400).body("errors[0].errorMessage", equalTo(USER_NAME_INVALID.toString()));
    }


    @Test
    @ApplyScriptBefore({"clean_iota_adresses.sql", "clean_iota.sql", "clean_iota_transactions.sql", "clean_users.sql"})
    public void register_invalidEmail() {
        final UserTO user = new UserTO();
        user.setPassword("test");
        user.setUserName("test");
        user.setEmail("test@ test.de");

        given().contentType("application/json")
                .body(user)
                .when()
                .post("authentication/register")
                .then().statusCode(400).body("errors[0].errorMessage", equalTo(USER_EMAIL_INVALID.toString()));
    }


    @Test
    @UsingDataSet({"users.json"})
    @ApplyScriptBefore({"clean_iota_adresses.sql", "clean_iota.sql", "clean_iota_transactions.sql", "clean_users.sql"})
    public void login_wrongCredentials() {
        final LoginTO login = new LoginTO();
        login.setUserName("test");
        login.setPassword("unknown");

        given().contentType("application/json")
                .body(login)
                .when()
                .post("authentication/login")
                .then().statusCode(400).body("errorMessage", equalTo(USER_PASSWORD_INVALID.toString()));
    }


    @Test
    @UsingDataSet({"users.json"})
    @ApplyScriptBefore({"clean_iota_adresses.sql", "clean_iota.sql", "clean_iota_transactions.sql", "clean_users.sql"})
    public void login_unknownUser() {
        final LoginTO login = new LoginTO();
        login.setUserName("unknown");
        login.setPassword("unknown");

        given().contentType("application/json")
                .body(login)
                .when()
                .post("authentication/login")
                .then().statusCode(400).body("errorMessage", equalTo(USER_NOT_FOUND.toString()));

    }


    @Test
    @UsingDataSet({"users.json"})
    @ApplyScriptBefore({"clean_iota_adresses.sql", "clean_iota.sql", "clean_iota_transactions.sql", "clean_users.sql"})
    public void login_success() {
        final LoginTO login = new LoginTO();
        login.setUserName("test");
        login.setPassword("test");

        final JWTokenTO result = given().contentType("application/json")
                .body(login)
                .when()
                .post("authentication/login")
                .then().extract().as(JWTokenTO.class);


        given()
                .when()
                .get("authentication/validate/{token}", result.getAccessToken())
                .then()
                .statusCode(204);
        given()
                .when()
                .get("authentication/validate/{token}", result.getRefreshToken())
                .then()
                .statusCode(204);


    }


    @Test
    @UsingDataSet({"users.json"})
    @ApplyScriptBefore({"clean_iota_adresses.sql", "clean_iota.sql", "clean_iota_transactions.sql", "clean_users.sql"})
    public void refresh_invalidAccessToken() {
        final LoginTO login = new LoginTO();
        login.setUserName("test");
        login.setPassword("test");

        final JWTokenTO result = given().contentType("application/json")
                .body(login)
                .when()
                .post("authentication/login")
                .then().extract().as(JWTokenTO.class);
        result.setAccessToken("accesstoken");

        final BusinessErrorTO errorTO = given()
                .when()
                .get("authentication/validate/{token}", result.getAccessToken())
                .then().extract().as(BusinessErrorTO.class);
        assertThat(errorTO.getErrorMessage()).isEqualTo(JWT_TOKEN_INVALID.toString());

        given()
                .when()
                .get("authentication/validate/{token}", result.getRefreshToken())
                .then()
                .statusCode(204);


        final JWTokenTO refreshedToken = given().body(result).contentType("application/json")
                .when()
                .post("authentication/refresh")
                .then().extract().as(JWTokenTO.class);

        given()
                .when()
                .get("authentication/validate/{token}", refreshedToken.getAccessToken())
                .then().statusCode(204);

        given()
                .when()
                .get("authentication/validate/{token}", refreshedToken.getRefreshToken())
                .then().statusCode(204);
    }

}
