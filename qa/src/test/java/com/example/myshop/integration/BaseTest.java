package com.example.myshop.integration;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;

public class BaseTest {

    protected RequestSpecification spec;
    protected String baseUrl = "http://localhost:8080";
    protected String adminUsername = "admin";
    protected String adminPassword = "admin";
    protected String userUsername = "user";
    protected String userPassword = "Qwerty12_";

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = baseUrl;
        
        spec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();
    }
    
    protected String getSessionCookieForUser(String username, String password) {
        return RestAssured.given(spec)
                .contentType(ContentType.URLENC)
                .formParam("username", username)
                .formParam("password", password)
                .when()
                .post("/login")
                .then()
                .statusCode(302) // Expect redirect after successful login
                .extract()
                .cookie("JSESSIONID");
    }
    
    protected String getSessionCookieForAdmin() {
        return getSessionCookieForUser(adminUsername, adminPassword);
    }
    
    protected String getSessionCookieForRegularUser() {
        return getSessionCookieForUser(userUsername, userPassword);
    }
} 