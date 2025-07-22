package com.escuelajs.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

public class GetUsersTest {
private static int userId;
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
}
