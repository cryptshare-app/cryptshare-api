package integration;

import java.io.BufferedReader;
import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.arquillian.ape.rdbms.ApplyScriptBefore;
import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.BeforeClass;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.mapper.ObjectMapperType;

@ArquillianSuiteDeployment
public class IntegrationTestSuite {

    protected static final String FW_BASE = "filesystem:src/main/resources/db.migrations";

    private static String JDBC_URL = "";
    private static String JDBC_URL_PROPERTY = "arquillian.database.url";
    private static String JDBC_USER = "";
    private static String JDBC_USER_PROPERTY = "arquillian.database.user";
    private static String JDBC_PASSWORD = "";
    private static String JDBC_PASSWORD_PROPERTY = "arquillian.database.password";


    @ArquillianResource
    protected URL deploymentURL;


    @Deployment
    @ApplyScriptBefore("init.sql")
    public static WebArchive createDeployment() {

        WebArchive fromZipFile = ShrinkWrap.createFromZipFile(WebArchive.class,
                new File("target/share-api.war"));

        fromZipFile.delete("/WEB-INF/web.xml");
        return fromZipFile;

    }


    @BeforeClass
    public static void restoreDatabase() {
        initDatabase();
    }


    @Before
    public void initRestClient() {
        System.setProperty("io.swagger.parser.util.RemoteUrl.trustAll", "true");
        RestAssured.useRelaxedHTTPSValidation();
        String url = deploymentURL + "api/";
        RestAssured.baseURI = url;
        /*
        SwaggerValidationFilter validationFilter = new SwaggerValidationFilter(url.replace("api/", "swagger.json"));
        RestAssured.filters(validationFilter);
        */
        RestAssured.config = new RestAssuredConfig().objectMapperConfig(
                new ObjectMapperConfig(ObjectMapperType.GSON));


        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }


    private static void loadAndSetPropertiesFromPom() {
        if (System.getProperty(JDBC_USER_PROPERTY) == null) {
            Model pom = null;
            Path pomFile = Paths.get("pom.xml");
            try {
                BufferedReader in = new BufferedReader(Files.newBufferedReader(pomFile, StandardCharsets.UTF_8));
                MavenXpp3Reader reader = new MavenXpp3Reader();
                pom = reader.read(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.setProperty(JDBC_USER_PROPERTY, pom.getProperties().getProperty(JDBC_USER_PROPERTY));
            System.setProperty(JDBC_PASSWORD_PROPERTY, pom.getProperties().getProperty(JDBC_PASSWORD_PROPERTY));
            System.setProperty(JDBC_URL_PROPERTY, pom.getProperties().getProperty(JDBC_URL_PROPERTY));
        }
    }


    private static Flyway getFlyway() {
        Flyway flyway = new Flyway();
        flyway.setDataSource(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
        return flyway;
    }


    private static void initDatabase() {
        loadAndSetPropertiesFromPom();
        JDBC_USER = System.getProperty(JDBC_USER_PROPERTY);
        JDBC_PASSWORD = System.getProperty(JDBC_PASSWORD_PROPERTY);
        JDBC_URL = System.getProperty(JDBC_URL_PROPERTY);

        Flyway flyway = getFlyway();
        flyway.setLocations(FW_BASE);
        flyway.setBaselineVersion(MigrationVersion.fromVersion("0")); // default: 1

        flyway.clean();
        flyway.baseline();
        flyway.migrate();
    }


    protected static synchronized void migrateDatabase(final String... scriptLocationArray) {
        Flyway flyway = getFlyway();

        flyway.setLocations(scriptLocationArray);
        flyway.migrate();
    }


    protected static synchronized void cleanDatabase() {
        Flyway flyway = getFlyway();
        flyway.clean();
    }

}
