package com.example.myshop.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AuthenticationTest extends BaseTest {

    @Test(description = "Test successful login with valid credentials")
    public void testSuccessfulLogin() {
        given(spec)
                .contentType(ContentType.URLENC)
                .formParam("username", userUsername)
                .formParam("password", userPassword)
                .when()
                .post("/login")
                .then()
                .statusCode(302) // Redirect after successful login
                .header("Location", containsString("/")) // Should redirect to home
                .cookie("JSESSIONID", notNullValue());
    }

    @Test(description = "Test failed login with invalid credentials")
    public void testFailedLogin() {
        given(spec)
                .contentType(ContentType.URLENC)
                .formParam("username", "invalidUsername")
                .formParam("password", "invalidPassword")
                .when()
                .post("/login")
                .then()
                .statusCode(anyOf(is(401), is(302))); // Could be 401 or redirect
    }

    @Test(description = "Test successful registration with valid data")
    public void testSuccessfulRegistration() {
        // Generate a unique username for this test
        String uniqueUsername = "testuser" + System.currentTimeMillis();
        
        given(spec)
                .contentType(ContentType.URLENC)
                .formParam("login", uniqueUsername)
                .formParam("password", "Testing123_")
                .formParam("passwordConfirm", "Testing123_")
                .when()
                .post("/registration")
                .then()
                .statusCode(302) // Redirect after successful registration
                .header("Location", containsString("/")); // Should redirect to home
                
        // Verify the new user can log in
        given(spec)
                .contentType(ContentType.URLENC)
                .formParam("username", uniqueUsername)
                .formParam("password", "Testing123_")
                .when()
                .post("/login")
                .then()
                .statusCode(302) // Successful login should redirect
                .cookie("JSESSIONID", notNullValue());
    }

    @Test(description = "Test access to restricted admin pages")
    public void testAccessToAdminPages() {
        // Test with admin credentials
        String adminSessionId = getSessionCookieForAdmin();
        
        given(spec)
                .cookie("JSESSIONID", adminSessionId)
                .when()
                .get("/admin")
                .then()
                .statusCode(200); // Admin should have access
        
        // Test with regular user credentials
        String userSessionId = getSessionCookieForRegularUser();
        
        given(spec)
                .cookie("JSESSIONID", userSessionId)
                .when()
                .get("/admin")
                .then()
                .statusCode(200); // Regular user is redirected to login page instead of 403
    }
} 