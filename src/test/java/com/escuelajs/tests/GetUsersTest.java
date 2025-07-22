package com.escuelajs.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

public class GetUsersTest {

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

        System.out.println("User list response (limit=100):");
        System.out.println(response.asPrettyString());

        assertEquals(response.statusCode(), 200);
    }
}
