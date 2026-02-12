import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.CreateUser;
import models.UserLogin;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;

public class UserLoginTest extends BaseTest {

    private String randomEmail;

    @Before
    public void prepareUser() {
        // создаём пользователя
        randomEmail = "ivan_" + System.currentTimeMillis() + "@yandex.ru";
        CreateUser user = new CreateUser(randomEmail, DEFAULT_PASSWORD, DEFAULT_NAME);

        Response response = userCreateClient.userCreate(user);
        checkStatusCode(response, 200);
        createUSerReturnsSuccessTrue(response);

        token = response.path("accessToken");
    }

    @Test
    public void createdUserCanLogin() {

        UserLogin loginUser = new UserLogin(randomEmail, DEFAULT_PASSWORD);

        Response response = userLoginClient.userLogin(loginUser);
        checkStatusCode(response, 200);
        createUSerReturnsSuccessTrue(response);

    }

    @Test
    public void cannotLoginWithWrongEmail() {
        String email = "nonexistent_" + System.currentTimeMillis() + "@yandex.ru";

        UserLogin loginUser = new UserLogin(email, DEFAULT_PASSWORD);
        Response response = userLoginClient.userLogin(loginUser);
        checkStatusCode(response, 401);
        createUSerReturnsSuccessFalse(response);
        checkAccountNotFoundError(response);
    }

    @Test
    public void cannotLoginWithWrongPassword() {

        // готовим данные для неправильного пароля
        UserLogin wrong = new UserLogin(randomEmail, "wrongPass");
        Response wrongLoginResponse = userLoginClient.userLogin(wrong);
        checkStatusCode(wrongLoginResponse, 401);
        createUSerReturnsSuccessFalse(wrongLoginResponse);
        checkAccountNotFoundError(wrongLoginResponse);
    }

    @Step("Checking that response status = {expectedStatus}")
    public void checkStatusCode(Response response, int expectedStatus) {
        response
                .then()
                .statusCode(expectedStatus);
    }

    @Step("Checking that successful request returns success: true")
    public void createUSerReturnsSuccessTrue(Response response) {
        response
                .then()
                .body("success", equalTo(true));
    }

    @Step("Checking that successful request returns success: false")
    public void createUSerReturnsSuccessFalse(Response response) {
        response
                .then()
                .body("success", equalTo(false));
    }

    @Step("Checking error: account not found")
    public void checkAccountNotFoundError(Response response) {
        response
                .then()
                .body("message", equalTo("email or password are incorrect"));
    }
}
