package com.escuelajs.tests;


import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertEquals;

public class GetProductsTest {







        @Test
        public void testCreateProduct() {
            // Make title unique
            String uniqueTitle = "Roman Imperor " + System.currentTimeMillis();

            // Prepare request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", uniqueTitle);
            requestBody.put("price", 12.3);
            requestBody.put("description", "Roman Imperor Description");
            requestBody.put("categoryId", 3);
            requestBody.put("images", List.of("www.images.com"));

            RestAssured
                    .given()
                    .baseUri("https://api.escuelajs.co")
                    .basePath("/api/v1/products")
                    .header("accept", "*/*")
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                    .when()
                    .post()
                    .then()
                    .statusCode(201)
                    .body("id", notNullValue())
                    .body("title", equalTo(uniqueTitle))
                    .body("price", equalTo(12.3f))
                    .body("description", equalTo("Roman Imperor Description"))
                    .body("category.id", equalTo(3));
        }

    @Test
    public void testGetLimitedProducts() {
            testCreateProduct();
        Response response = RestAssured
                .given()
                .baseUri("https://api.escuelajs.co")
                .basePath("/api/v1/products")
                .header("accept", "*/*")
                .queryParam("limit", 1)
                .queryParam("offset", 0)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].id", notNullValue())
                .extract().response();

        assertEquals(response.statusCode(), 200);
        System.out.println("Response: " + response.asPrettyString());
    }
    @Test
    public void testRegisterAndLoginUser() {
        String email = "testuser" + System.currentTimeMillis() + "@example.com";
        String password = "MySecurePass123";

        // Step 1: Register new user
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("password", password);
        user.put("name", "Test User");
        user.put("avatar", "https://api.lorem.space/image/face?w=150&h=150");

        RestAssured
                .given()
                .baseUri("https://api.escuelajs.co")
                .basePath("/api/v1/users")
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post()
                .then()
                .statusCode(201)
                .body("email", equalTo(email));

        // Step 2: Login with the same credentials
        String accessToken = RestAssured
                .given()
                .baseUri("https://api.escuelajs.co")
                .basePath("/api/v1/auth/login")
                .contentType("application/x-www-form-urlencoded")
                .formParam("email", email)
                .formParam("password", password)
                .when()
                .post()
                .then()
                .statusCode(201)
                .body("access_token", notNullValue())
                .extract()
                .path("access_token");

        System.out.println("Access Token: " + accessToken);
    }





}


