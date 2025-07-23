package com.escuelajs.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

public class GetCategoriesTest {

    private static int categoryId; // 🔹 Class-level variable
    @Test
    public void testGetCategoriesWithLimit() {
        Response response = RestAssured
                .given()
                .baseUri("https://api.escuelajs.co")
                .basePath("/api/v1/categories")
                .queryParam("limit", 200)
                .header("accept", "*/*")
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("$", not(empty()))
                .extract().response();

        System.out.println("Categories Response:\n" + response.asPrettyString());
        assertEquals(response.statusCode(), 200);
    }
    @Test
    public void testCreateUniqueCategory() {
        String uniqueCategoryName = "Appliances-" + System.currentTimeMillis();
        String categoryImage = "www.google.com";

        Response response = RestAssured
                .given()
                .baseUri("https://api.escuelajs.co")
                .basePath("/api/v1/categories")
                .header("accept", "*/*")
                .contentType("application/json")
                .body("{ \"name\": \"" + uniqueCategoryName + "\", \"image\": \"" + categoryImage + "\" }")
                .when()
                .post()
                .then()
                .statusCode(201)
                .body("name", equalTo(uniqueCategoryName))
                .body("image", equalTo(categoryImage))
                .extract().response();

        categoryId = response.path("id"); // ✅ Extract ID
        System.out.println("Created Unique Category ID: " + categoryId);
    }
    @Test(dependsOnMethods = "testCreateUniqueCategory")
    public void testGetCategoryById() {
        Response response = RestAssured
                .given()
                .baseUri("https://api.escuelajs.co")
                .basePath("/api/v1/categories/" + categoryId)
                .header("accept", "*/*")
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("id", equalTo(categoryId))
                .body("name", notNullValue())
                .body("image", notNullValue())
                .extract().response();

        System.out.println("Fetched Category by ID:\n" + response.asPrettyString());
    }

}
