import client.OrderCreateClient;
import client.UserCreateClient;
import client.UserLoginClient;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;


public class BaseTest {

    protected static final String DEFAULT_PASSWORD = "1005";
    protected static final String DEFAULT_NAME = "sasha";

    protected UserCreateClient userCreateClient;
    protected UserLoginClient userLoginClient;
    protected OrderCreateClient orderCreateClient;
    protected String token;

    @Before
    public void setUp() {
        initBaseUri();
        userCreateClient = new UserCreateClient();
        userLoginClient = new UserLoginClient();
        orderCreateClient = new OrderCreateClient();
    }

    @After
    public void tearDown() {
        if (token != null) {
            deleteUser(token);
        }
    }

    @Step("Initializing base URI")
    public void initBaseUri() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru";
    }

    @Step("Deleting user")
    public void deleteUser(String token) {
        userCreateClient.deleteUser(token)
                .then()
                .statusCode(202);
    }
}