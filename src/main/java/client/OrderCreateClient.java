package client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.CreateOrder;
import static io.restassured.RestAssured.given;

public class OrderCreateClient {
    private static final String BASE_PATH = "/api/orders";

    @Step("Creating order with authorization")
    public Response createOrderWithToken(CreateOrder order, String token) {
        return given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .body(order)
                .when()
                .post(BASE_PATH);
    }

    @Step("Creating order without authorization")
    public Response createOrderWithoutToken(CreateOrder order) {
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post(BASE_PATH);
    }

    @Step("Get all ingredients list")
    public Response getIngredients() {
        return io.restassured.RestAssured.given()
                .header("Content-type", "application/json")
                .when()
                .get("/api/ingredients");
    }
}

