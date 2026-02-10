import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.CreateOrder;
import models.CreateUser;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;


public class CreateOrderTest extends BaseTest {

    private List<String> validIngredients;

    @Before
    public void prepareUser() {

        // создаём пользователя
        String randomEmail = "ivan_" + System.currentTimeMillis() + "@yandex.ru";
        CreateUser user = new CreateUser(randomEmail, DEFAULT_PASSWORD, DEFAULT_NAME);

        Response response = userCreateClient.userCreate(user);
        checkStatusCode(response, 200);
        createUSerReturnsSuccessTrue(response);
        token = response.path("accessToken");

        // получаем ингредиенты
        Response ingredientsResponse = orderCreateClient.getIngredients();
        validIngredients = ingredientsResponse.path("data._id");
    }

    @Test
    public void createOrderWithAuthorizationSuccess() {

        List<String> ingredientsToOrder = List.of(validIngredients.get(0), validIngredients.get(1));

        CreateOrder order = new CreateOrder(ingredientsToOrder);
        Response response = orderCreateClient.createOrderWithToken(order, token);
        checkStatusCode(response, 200);
        createUSerReturnsSuccessTrue(response);
        checkTrackNotNull(response);

    }

    @Test
    public void createOrderWithoutAuthorizationSuccess() {

        List<String> ingredients = List.of(validIngredients.get(0), validIngredients.get(4)
        );
        CreateOrder order = new CreateOrder(ingredients);
        Response response = orderCreateClient.createOrderWithoutToken(order);
        checkStatusCode(response, 200);
        createUSerReturnsSuccessTrue(response);
        checkTrackNotNull(response);
    }

    @Test
    public void createOrderWithoutIngredients() {

        List<String> ingredients = List.of(
        );
        CreateOrder order = new CreateOrder(ingredients);
        Response response = orderCreateClient.createOrderWithoutToken(order);
        checkStatusCode(response, 400);
        createUSerReturnsSuccessFalse(response);
        createOrderEmptyListError(response);
    }

    @Test
    public void createOrderInvalidHashError() {

        List<String> ingredients = List.of(
                "61c0c5a71d1f82001bda8648",
                "61c0c5a71d1f82001bda964c"
        );
        CreateOrder order = new CreateOrder(ingredients);
        Response response = orderCreateClient.createOrderWithoutToken(order);
        checkStatusCode(response, 400);
        createUSerReturnsSuccessFalse(response);
        checkInvalidHashError(response);
    }

    @Step("Checking that response status = {expectedStatus}")
    public void checkStatusCode(Response response, int expectedStatus) {
        response.
                then().
                statusCode(expectedStatus);
    }

    @Step("Checking that successful request returns success: true")
    public void createUSerReturnsSuccessTrue(Response response) {
        response
                .then()
                .body("success", equalTo(true));
    }

    @Step("Checking that 'order.number' field is present in response")
    public void checkTrackNotNull(Response response) {
        response
                .then()
                .body("order.number", notNullValue());
    }

    @Step("Checking error: Ingredient ids must be provided")
    public void createOrderEmptyListError(Response response) {
        response
                .then()
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Step("Checking error: One or more ids provided are incorrect")
    public void checkInvalidHashError(Response response) {
        response
                .then()
                .body("message", equalTo("One or more ids provided are incorrect"));
    }

    @Step("Checking that successful request returns success: false")
    public void createUSerReturnsSuccessFalse(Response response) {
        response
                .then()
                .body("success", equalTo(false));
    }
}
