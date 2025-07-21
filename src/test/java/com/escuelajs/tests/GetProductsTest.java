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
    }


