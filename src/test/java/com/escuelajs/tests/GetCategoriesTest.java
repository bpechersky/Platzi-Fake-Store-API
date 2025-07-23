package com.escuelajs.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

public class GetCategoriesTest {

    private static int categoryId; // 🔹 Class-level variable
    private static  String uniqueCategoryName ;
    private static String categoryImage;
    private static String categorySlug;
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
         uniqueCategoryName = "Appliances-" + System.currentTimeMillis();
         categoryImage = "www.google.com";

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
        categorySlug = response.path("slug");
        categoryImage = response.path("image");
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
                .body("name", equalTo(uniqueCategoryName))
                .body("image", equalTo(categoryImage))
                .body("slug", equalTo(categorySlug))
                .extract().response();

        System.out.println("Fetched Category by ID:\n" + response.asPrettyString());
    }
    @Test(dependsOnMethods = "testGetCategoryById")
    public void testUpdateCategoryById() {
        String updatedName = "NewAppliances_" + System.currentTimeMillis();

        Map<String, Object> updatedCategory = new HashMap<>();
        updatedCategory.put("name", updatedName);
        updatedCategory.put("image", "https://www.google.com/logo.png");

        Response response = RestAssured
                .given()
                .baseUri("https://api.escuelajs.co")
                .basePath("/api/v1/categories/" + categoryId)
                .contentType(ContentType.JSON)
                .accept("*/*")
                .body(updatedCategory)
                .when()
                .put()
                .then()
                .statusCode(200)
                .body("id", equalTo(categoryId))
                .body("name", equalTo(updatedName))
                .body("image", equalTo("https://www.google.com/logo.png"))
                .extract().response();

        System.out.println("Updated Category:\n" + response.asPrettyString());
    }


}
