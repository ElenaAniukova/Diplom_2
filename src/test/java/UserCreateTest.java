import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.CreateUser;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;

public class UserCreateTest extends BaseTest {

    @Test
    public void createUserReturns200AndSuccessTrue() {
        String randomEmail = "ivan_" + System.currentTimeMillis() + "@yandex.ru";
        CreateUser user = new CreateUser(randomEmail, DEFAULT_PASSWORD, DEFAULT_NAME);

        Response response = userCreateClient.userCreate(user);
        checkStatusCode(response, 200);
        createUSerReturnsSuccessTrue(response);

        token = response.path("accessToken");
    }

    @Test
    public void cannotCreateDuplicateUser() {
        String randomEmail = "ivan_" + System.currentTimeMillis() + "@yandex.ru";
        CreateUser user = new CreateUser(randomEmail, DEFAULT_PASSWORD, DEFAULT_NAME);

        Response response = userCreateClient.userCreate(user);
        checkStatusCode(response, 200);
        createUSerReturnsSuccessTrue(response);

        token = response.path("accessToken");

        //Пробуем создать пользователя снова
        Response duplicateResponse = userCreateClient.userCreate(user);
        checkStatusCode(duplicateResponse, 403);
        createUSerReturnsSuccessFalse(duplicateResponse);
        checkDuplicateError(duplicateResponse);
    }

    @Test
    public void cannotCreateUserWithoutEmail() {

        CreateUser user = new CreateUser("", DEFAULT_PASSWORD, DEFAULT_NAME);
        Response response = userCreateClient.userCreate(user);
        checkStatusCode(response, 403);
        createUSerReturnsSuccessFalse(response);
        checkMissingDataError(response);
    }

    @Test
    public void cannotCreateUserWithoutPassword() {
        String randomEmail = "ivan_" + System.currentTimeMillis() + "@yandex.ru";

        CreateUser user = new CreateUser(randomEmail, "", DEFAULT_NAME);
        Response response = userCreateClient.userCreate(user);
        checkStatusCode(response, 403);
        createUSerReturnsSuccessFalse(response);
        checkMissingDataError(response);
    }

    @Test
    public void cannotCreateUserWithoutName() {
        String randomEmail = "ivan_" + System.currentTimeMillis() + "@yandex.ru";

        CreateUser user = new CreateUser(randomEmail, DEFAULT_PASSWORD, "");
        Response response = userCreateClient.userCreate(user);
        checkStatusCode(response, 403);
        createUSerReturnsSuccessFalse(response);
        checkMissingDataError(response);
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

    @Step("Checking duplicate user creation error")
    public void checkDuplicateError(Response duplicateResponse) {
        duplicateResponse
                .then()
                .body("message", equalTo("User already exists"));
    }

    @Step("Checking error: insufficient data to create user account")
    public void checkMissingDataError(Response response) {
        response.then()
                .body("message", equalTo("Email, password and name are required fields"));
    }
}

