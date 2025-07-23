package com.escuelajs.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.io.File;

import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertEquals;

public class UploadFileTest {

    private static String uploadedFilename;

    @Test
    public void testUploadFile() {
        File file = new File("src/test/resources/sample-image.png"); // 🟡 Ensure this file exists locally

        Response response = RestAssured
                .given()
                .baseUri("https://api.escuelajs.co")
                .basePath("/api/v1/files/upload")
                .multiPart("file", file)
                .accept("*/*")
                .when()
                .post()
                .then()
                .statusCode(201)
                .body("originalname", notNullValue())
                .body("filename", notNullValue())
                .extract().response();

        uploadedFilename = response.path("filename");
        System.out.println("Uploaded file name: " + uploadedFilename);
    }

    @Test(dependsOnMethods = "com.escuelajs.tests.UploadFileTest.testUploadFile")
    public void testDownloadUploadedFile() {
        String uploadedFilename = UploadFileTest.uploadedFilename;

        Response response = RestAssured
                .given()
                .baseUri("https://api.escuelajs.co")
                .basePath("/api/v1/files/" + uploadedFilename)
                .accept("*/*")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().response();

        System.out.println("Downloaded file size: " + response.getBody().asByteArray().length);
        assertEquals(response.statusCode(), 200);
    }


    @Test
    public void testAllLocationsHaveValidFields() {
        Response response = RestAssured
                .given()
                .baseUri("https://api.escuelajs.co")
                .basePath("/api/v1/locations")
                .queryParam("radius", 10)
                .queryParam("size", 10)
                .queryParam("origin", "37.3382,-121.8863") // San Jose coordinates
                .header("accept", "*/*")
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("id", everyItem(notNullValue()))
                .body("name", everyItem(notNullValue()))
                .body("description", everyItem(notNullValue()))
                .body("latitude", everyItem(allOf(notNullValue(), greaterThan(37.0f), lessThan(38.0f))))
                .body("longitude", everyItem(allOf(notNullValue(), greaterThan(-123.0f), lessThan(-121.0f))))
                .extract().response();

        System.out.println("Validated All Locations:\n" + response.asPrettyString());
    }

    }


