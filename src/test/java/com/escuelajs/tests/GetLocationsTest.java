package com.escuelajs.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class GetLocationsTest {

    @Test
    public void testAllLocationsHaveValidFields() {
        Response response = given()
                .baseUri("https://api.escuelajs.co")
                .basePath("/api/v1/locations")
                .queryParam("radius", 10)
                .queryParam("size", 10)
                .queryParam("origin", "37.3382,-121.8863") // San Jose coords
                .header("accept", "*/*")
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("id", everyItem(notNullValue()))
                .body("name", everyItem(notNullValue()))
                .body("description", everyItem(notNullValue()))
                .body("latitude", everyItem(allOf(notNullValue(), greaterThan(37.0f), lessThan(38.0f))))
                .body("longitude", everyItem(allOf(notNullValue(), lessThan(-121.0f), greaterThan(-123.0f))))
                .extract().response();

        System.out.println("Validated All Items:\n" + response.asPrettyString());
    }
}
