package com.escuelajs.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.assertEquals;
import static org.hamcrest.Matchers.notNullValue;

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
}
