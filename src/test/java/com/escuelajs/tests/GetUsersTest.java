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

public class    GetUsersTest {
private static int userId;
    @BeforeClass
    public void registerAndLoginUser() {
        String testEmail  = "testuser" + System.currentTimeMillis() + "@example.com";
        String password = "MySecurePass123";

        // Register user and capture userId
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

        userId = registerResponse.path("id"); // ✅ Extract userId correctly
        System.out.println("User ID: " + userId);

        // Login to get tokens
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

        String accessToken = loginResponse.path("access_token");
        String refreshToken = loginResponse.path("refresh_token");

        System.out.println("Access Token: " + accessToken);
        System.out.println("Refresh Token: " + refreshToken);
    }

    @Test
    public void testGetAllUsersWithLimit() {
        Response response = RestAssured
                .given()
                .baseUri("https://api.escuelajs.co")
                .basePath("/api/v1/users")
                .header("accept", "*/*")
                .queryParam("limit", 100)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("[0].id", notNullValue())
                .body("[0].email", notNullValue())
                .extract().response();

         userId = response.path("[0].id");

         System.out.println(("userId is " +userId));

        System.out.println("User list response (limit=100):");
        System.out.println(response.asPrettyString());

        assertEquals(response.statusCode(), 200);
    }
    @Test(dependsOnMethods = "testGetAllUsersWithLimit")
    public void testGetUserById() {


        Response response = RestAssured
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
                .extract().response();

        System.out.println("User Details:\n" + response.asPrettyString());
        assertEquals(response.statusCode(), 200);
    }
    @Test
    public void testUpdateUserById() {
        RestAssured
                .given()
                .baseUri("https://api.escuelajs.co")
                .basePath("/api/v1/users/" + userId)
                .header("accept", "*/*")
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "  \"email\": \"bp+1@gmail.com\",\n" +
                        "  \"name\": \"John Smith\",\n" +
                        "  \"password\": \"Test123\",\n" +
                        "  \"role\": \"customer\",\n" +
                        "  \"avatar\": \"www.google.com\"\n" +
                        "}")
                .when()
                .put()
                .then()
                .statusCode(200)
                .log().all();
    }

    @Test
    public void testDeleteUserById() {
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
