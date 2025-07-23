package com.escuelajs.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

public class  GetUsersTest {
    private static int userId;
    private static String accessToken; // 🔹 Class-level variable to store token

    @BeforeClass
    public void registerAndLoginUser() {
        String testEmail = "testuser" + System.currentTimeMillis() + "@example.com";
        String password = "MySecurePass123";

        // Register user
        Map<String, Object> user = new HashMap<>();
        user.put("email", testEmail);
        user.put("password", password);
        user.put("name", "Test User");
        user.put("avatar", "https://api.lorem.space/image/face?w=150&h=150");

        Response registerResponse = RestAssured
                .given()
                .baseUri("https://api.escuelajs.co")
                .basePath("/api/v1/users")
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post()
                .then()
                .statusCode(201)
                .body("email", equalTo(testEmail))
                .extract().response();

        userId = registerResponse.path("id"); // ✅ Capture user ID correctly

        // Login
        Response loginResponse = RestAssured
                .given()
                .baseUri("https://api.escuelajs.co")
                .basePath("/api/v1/auth/login")
                .contentType(ContentType.JSON)
                .body(Map.of("email", testEmail, "password", password)) // Fixed payload format
                .when()
                .post()
                .then()
                .statusCode(201)
                .body("access_token", notNullValue())
                .body("refresh_token", notNullValue())
                .extract().response();

        accessToken = loginResponse.path("access_token"); // ✅ Save token to class
        System.out.println("Access Token: " + accessToken);
        System.out.println("User ID: " + userId);
    }



    @Test(priority = 1)
    public void testGetPublicUserById() {
       // int publicUserId = 1; // Hardcoded public user ID

        RestAssured
                .given()
                .baseUri("https://api.escuelajs.co")
                .basePath("/api/v1/users/" + userId)
                .header("accept", "*/*")
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("id", equalTo(userId))
                .body("email", notNullValue())
                .body("name", notNullValue())
                .log().body();
    }


    @Test(priority = 2)
    public void testUpdateUserWithTimestamps() {
        Map<String, Object> userPayload = new HashMap<>();
        userPayload.put("id", userId); // Replace with actual userId variable if available
        userPayload.put("email", "bp+1@gmail.com");
        userPayload.put("password", "Test123");
        userPayload.put("name", "John Smith");
        userPayload.put("role", "customer");
        userPayload.put("avatar", "www.google.com");
        userPayload.put("creationAt", "2025-07-23T01:09:47.000Z");
        userPayload.put("updatedAt", "2025-07-23T01:10:01.000Z");

        RestAssured
                .given()
                .baseUri("https://api.escuelajs.co")
                .basePath("/api/v1/users/" + userId)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(userPayload)
                .when()
                .put()
                .then()
                .statusCode(200)
                .body("email", equalTo("bp+1@gmail.com"))
                .body("name", equalTo("John Smith"))
                .body("role", equalTo("customer"))
                .log().all();
    }



    @Test(priority = 3)
    public void testDeleteUserById() {
        registerAndLoginUser();
        RestAssured
                .given()
                .baseUri("https://api.escuelajs.co")
                .basePath("/api/v1/users/" + userId)
                .header("accept", "*/*")
                .when()
                .delete()
                .then()
                .statusCode(200) // or 204 based on API response
                .log().all();
    }

}
