package com.escuelajs.tests;


import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertEquals;

public class GetProductsTest {
    private String accessToken;
    private String refreshToken;
    private String testEmail;
    private int productId;

    @BeforeClass
    public void registerAndLoginUser() {
        testEmail  = "testuser" + System.currentTimeMillis() + "@example.com";
        String password = "MySecurePass123";

        // Register user
        Map<String, Object> user = new HashMap<>();
        user.put("email", testEmail);
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
                .body("email", equalTo(testEmail));

        // Login
        Response loginResponse = RestAssured
                .given()
                .baseUri("https://api.escuelajs.co")
                .basePath("/api/v1/auth/login")
                .contentType("application/x-www-form-urlencoded")
                .formParam("email", testEmail)
                .formParam("password", password)
                .when()
                .post()
                .then()
                .statusCode(201)
                .body("access_token", notNullValue())
                .body("refresh_token", notNullValue())
                .extract().response();

        accessToken = loginResponse.path("access_token");
        refreshToken = loginResponse.path("refresh_token");

        System.out.println("Access Token: " + accessToken);
        System.out.println("Refresh Token: " + refreshToken);
    }
        @Test
        public void testCreateProduct() {
            // Make title unique
            String uniqueTitle = "Roman Imperor " + System.currentTimeMillis();

            // Prepare request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("title", uniqueTitle);
            requestBody.put("price", 12.3);
            requestBody.put("description", "Roman Imperor Description");
            requestBody.put("categoryId", 24);
            requestBody.put("images", List.of("www.images.com"));

            Response response = RestAssured
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
                    .body("category.id", equalTo(24))
                    .extract().response();

            productId = response.path("id"); // ✅ Save product ID for reuse
            System.out.println("Created Product ID: " + productId);
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
    public void testGetUserProfile() {
        RestAssured
                .given()
                .baseUri("https://api.escuelajs.co")
                .basePath("/api/v1/auth/profile")
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("email", equalTo(testEmail)) // <-- use dynamic email
                .body("role", notNullValue());
    }

    @Test
    public void testRefreshAccessToken() {
        Map<String, String> body = Map.of("refreshToken", refreshToken);

        Response response = RestAssured
                .given()
                .baseUri("https://api.escuelajs.co")
                .basePath("/api/v1/auth/refresh-token")
                .header("accept", "*/*")
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post()
                .then()
                .statusCode(201)
                .body("access_token", notNullValue())
                .extract().response();

        System.out.println("New Access Token:\n" + response.path("access_token"));
    }
    @Test(dependsOnMethods = "testCreateProduct")
    public void testGetProductById() {
        RestAssured
                .given()
                .baseUri("https://api.escuelajs.co")
                .basePath("/api/v1/products/" + productId)
                .header("accept", "*/*")
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("id", equalTo(productId));
    }





        @Test(dependsOnMethods = "testCreateProduct")
        public void testGetRelatedProducts() {


            Response response = RestAssured
                    .given()
                    .baseUri("https://api.escuelajs.co")
                    .basePath("/api/v1/products/" + productId + "/related")
                    .header("accept", "*/*")
                    .when()
                    .get()
                    .then()
                    .statusCode(200)
                    .body("size()", greaterThan(0))
                    .body("[0].id", notNullValue())
                    .extract().response();

            System.out.println("Related Products:\n" + response.asPrettyString());
            assertEquals(response.statusCode(), 200);
        }




        @Test(dependsOnMethods = "testCreateProduct")
        public void testUpdateProductById() {
            String uniqueTitle = "Updated Title " + System.currentTimeMillis();

            Map<String, Object> requestBody = Map.of(
                    "title", uniqueTitle,
                    "price", 1.2,
                    "description", "Changed Description",
                    "categoryId", 24,
                    "images", List.of("www.google.com")
            );

            RestAssured
                    .given()
                    .baseUri("https://api.escuelajs.co")
                    .basePath("/api/v1/products/" + productId)
                    .header("accept", "*/*")
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                    .when()
                    .put()
                    .then()
                    .statusCode(200)
                    .body("id", equalTo(productId))
                    .body("title", equalTo(uniqueTitle))
                    .body("price", equalTo(1.2f))
                    .body("description", equalTo("Changed Description"))
                    .body("category.id", equalTo(24))
                    .body("images[0]", equalTo("www.google.com"));
        }

    @Test(dependsOnMethods = {"testCreateProduct", "testUpdateProductById"})
    public void testDeleteProductById() {


        RestAssured
                .given()
                .baseUri("https://api.escuelajs.co")
                .basePath("/api/v1/products/" + productId)
                .header("accept", "*/*")
                .when()
                .delete()
                .then()
                .statusCode(anyOf(is(200), is(204))); // API returns 200 or 204 on success
    }

}





