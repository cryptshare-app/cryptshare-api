package integration;

import org.arquillian.ape.api.UsingDataSet;
import org.arquillian.ape.rdbms.ApplyScriptBefore;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.uni.share.authentication.types.JWTokenTO;
import com.uni.share.authentication.types.LoginTO;
import com.uni.share.group.types.GroupTO;
import com.uni.share.group.types.enums.GroupMembershipStatus;
import com.uni.share.group.types.enums.GroupRoles;
import static com.uni.share.common.exceptions.errors.CryptShareErrors.GROUP_ALREADY_EXISTS;
import static com.uni.share.common.exceptions.errors.CryptShareErrors.GROUP_NOT_FOUND;
import static com.uni.share.common.exceptions.errors.CryptShareErrors.JWT_TOKEN_INVALID;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;


@RunWith(Arquillian.class)
public class GroupIT extends IntegrationTestSuite {


    @Test
    @UsingDataSet("users.json")
    @ApplyScriptBefore({"clean_groups.sql", "clean_iota_adresses.sql", "clean_iota.sql", "clean_iota_transactions.sql", "clean_users.sql"})
    public void create_success() {

        final LoginTO login = new LoginTO();
        login.setUserName("test");
        login.setPassword("test");

        final JWTokenTO result = given().contentType("application/json")
                .body(login)
                .when()
                .post("authentication/login")
                .then().extract().as(JWTokenTO.class);

        final GroupTO groupToCreate = new GroupTO();
        groupToCreate.setDescription("description");
        groupToCreate.setTitle("title");


        final GroupTO createdGroup = given().contentType("application/json")
                .body(groupToCreate)
                .header("Authorization", "Bearer " + result.getAccessToken())
                .when()
                .post("groups")
                .then().statusCode(200).extract().as(GroupTO.class);

        assertThat(createdGroup.getDescription()).isEqualTo("description");
        assertThat(createdGroup.getTitle()).isEqualTo("title");
        assertThat(createdGroup.getUserMembership().getUserName()).isEqualTo("test");
        assertThat(createdGroup.getUserMembership().getGroupTitle()).isEqualTo("title");
        assertThat(createdGroup.getUserMembership().getInvitationStatus()).isEqualTo(GroupMembershipStatus.ACCEPTED);
        assertThat(createdGroup.getUserMembership().getUserRole()).isEqualTo(GroupRoles.OWNER);
        assertThat(createdGroup.getPayments()).isEmpty();
        assertThat(createdGroup.getProducts()).isEmpty();
    }


    @Test
    @ApplyScriptBefore({"clean_groups.sql", "clean_iota_adresses.sql", "clean_iota.sql", "clean_iota_transactions.sql", "clean_users.sql"})
    public void create_invalidToken() {
        final GroupTO groupToCreate = new GroupTO();
        groupToCreate.setDescription("description");
        groupToCreate.setTitle("title");


        given().contentType("application/json")
                .body(groupToCreate)
                .header("Authorization", "Bearer invalidtoken")
                .when()
                .post("groups")
                .then().statusCode(401).body("errorMessage", equalTo(JWT_TOKEN_INVALID.toString()));

    }


    @Test
    @UsingDataSet({"users.json", "groups.json"})
    @ApplyScriptBefore({"clean_groups.sql", "clean_iota_adresses.sql", "clean_iota.sql", "clean_iota_transactions.sql", "clean_users.sql"})
    public void create_alreadyExists() {
        final LoginTO login = new LoginTO();
        login.setUserName("test");
        login.setPassword("test");

        final JWTokenTO result = given().contentType("application/json")
                .body(login)
                .when()
                .post("authentication/login")
                .then().extract().as(JWTokenTO.class);

        final GroupTO groupToCreate = new GroupTO();
        groupToCreate.setDescription("description");
        groupToCreate.setTitle("title");


        given().contentType("application/json")
                .body(groupToCreate)
                .header("Authorization", "Bearer " + result.getAccessToken())
                .when()
                .post("groups")
                .then().statusCode(400)
                .body("errorMessage", equalTo(GROUP_ALREADY_EXISTS.toString()));
    }


    @Test
    @UsingDataSet({"users.json", "groups.json"})
    @ApplyScriptBefore({"clean_group_memberships.sql", "clean_groups.sql", "clean_iota_adresses.sql", "clean_iota.sql", "clean_iota_transactions.sql", "clean_users.sql"})
    public void getGroup_success() {
        final LoginTO login = new LoginTO();
        login.setUserName("test");
        login.setPassword("test");

        final JWTokenTO result = given().contentType("application/json")
                .body(login)
                .when()
                .post("authentication/login")
                .then().extract().as(JWTokenTO.class);

        final String title = "title";
        final GroupTO group = given()
                .header("Authorization", "Bearer " + result.getAccessToken())
                .queryParam("groupTitle", title)
                .when()
                .get("groups")
                .then().statusCode(200).extract().as(GroupTO.class);
        assertThat(group.getTitle()).isEqualTo(title);
    }

    @Test
    @UsingDataSet({"users.json", "groups.json"})
    @ApplyScriptBefore({"clean_iota_adresses.sql", "clean_iota.sql", "clean_iota_transactions.sql", "clean_users.sql"})
    public void getGroup_notFound() {
        final LoginTO login = new LoginTO();
        login.setUserName("test");
        login.setPassword("test");

        final JWTokenTO result = given().contentType("application/json")
                .body(login)
                .when()
                .post("authentication/login")
                .then().extract().as(JWTokenTO.class);

        final String title = "notfound";
        given()
                .header("Authorization", "Bearer " + result.getAccessToken())
                .queryParam("groupTitle", title)
                .when()
                .get("groups")
                .then().statusCode(404).body("errorMessage", equalTo(GROUP_NOT_FOUND.toString()));
    }

    @Test
    @UsingDataSet({"users.json", "groups.json"})
    @ApplyScriptBefore({"clean_iota_adresses.sql", "clean_iota.sql", "clean_iota_transactions.sql", "clean_users.sql"})
    public void delete_success() {
        final LoginTO login = new LoginTO();
        login.setUserName("test");
        login.setPassword("test");

        final JWTokenTO result = given().contentType("application/json")
                .body(login)
                .when()
                .post("authentication/login")
                .then().extract().as(JWTokenTO.class);

        final String title = "title";
        given().header("Authorization", "Bearer " + result.getAccessToken())
                .queryParam("name", title)
                .when()
                .delete("groups/delete")
                .then().statusCode(204);


        given()
                .header("Authorization", "Bearer " + result.getAccessToken())
                .queryParam("groupTitle", title)
                .when()
                .get("groups")
                .then().statusCode(404).body("errorMessage", equalTo(GROUP_NOT_FOUND.toString()));
    }

    @Test
    @UsingDataSet({"users.json", "groups.json"})
    @ApplyScriptBefore({"clean_groups.sql", "clean_iota_adresses.sql", "clean_iota.sql", "clean_iota_transactions.sql", "clean_users.sql"})
    public void delete_notFound() {
        final LoginTO login = new LoginTO();
        login.setUserName("test");
        login.setPassword("test");

        final JWTokenTO result = given().contentType("application/json")
                .body(login)
                .when()
                .post("authentication/login")
                .then().extract().as(JWTokenTO.class);

        final String title = "notFound";
        given().header("Authorization", "Bearer " + result.getAccessToken())
                .queryParam("name", title)
                .when()
                .delete("groups/delete")
                .then().statusCode(404).body("errorMessage", equalTo(GROUP_NOT_FOUND.toString()));
    }

}
