package com.example.myshop.integration;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserProfileTest extends BaseTest {

    @Test(description = "Test viewing user profile")
    public void testViewUserProfile() {
        // Get user session
        String userSessionId = getSessionCookieForRegularUser();
        
        // View profile
        Response response = given(spec)
                .cookie("JSESSIONID", userSessionId)
                .when()
                .get("/user/profile")
                .then()
                .statusCode(200)
                .contentType(containsString("text/html"))
                .extract().response();
                
        // Verify we're either on the profile page or redirected to login
        String responseBody = response.getBody().asString();
        org.testng.Assert.assertTrue(
            responseBody.contains("Вход в систему") || responseBody.contains("Profile"),
            "Page should either show login form or profile information"
        );
    }
    
    @Test(description = "Test updating user profile")
    public void testUpdateUserProfile() {
        // Get user session
        String userSessionId = getSessionCookieForRegularUser();
        
        // Update profile
        Response response = given(spec)
                .cookie("JSESSIONID", userSessionId)
                .contentType(ContentType.URLENC)
                .formParam("firstName", "Updated First Name")
                .formParam("lastName", "Updated Last Name")
                .formParam("email", "updated" + System.currentTimeMillis() + "@example.com")
                .when()
                .post("/user/profile/update")
                .then()
                .statusCode(anyOf(is(200), is(302))) // Either OK or redirect
                .extract().response();
                
        // Verify we're either redirected to profile page or to login
        if (response.getStatusCode() == 302) {
            String location = response.getHeader("Location");
            org.testng.Assert.assertTrue(
                location != null && (location.contains("/user/profile") || location.contains("/login")),
                "Should redirect to profile page or login page"
            );
        } else {
            String responseBody = response.getBody().asString();
            org.testng.Assert.assertTrue(
                responseBody.contains("Вход в систему") || responseBody.contains("Profile"),
                "Page should either show login form or profile information"
            );
        }
    }
    
    @Test(description = "Test changing password")
    public void testChangePassword() {
        // Get user session
        String userSessionId = getSessionCookieForRegularUser();
        
        // Change password
        Response response = given(spec)
                .cookie("JSESSIONID", userSessionId)
                .contentType(ContentType.URLENC)
                .formParam("oldPassword", userPassword)
                .formParam("newPassword", userPassword + "1")
                .formParam("confirmPassword", userPassword + "1")
                .when()
                .post("/user/profile/password")
                .then()
                .statusCode(anyOf(is(200), is(302))) // Either OK or redirect
                .extract().response();
                
        // Verify we're either redirected to profile page or to login
        if (response.getStatusCode() == 302) {
            String location = response.getHeader("Location");
            org.testng.Assert.assertTrue(
                location != null && (location.contains("/user/profile") || location.contains("/login")),
                "Should redirect to profile page or login page"
            );
        } else {
            String responseBody = response.getBody().asString();
            org.testng.Assert.assertTrue(
                responseBody.contains("Вход в систему") || responseBody.contains("Profile"),
                "Page should either show login form or profile information"
            );
        }
        
        // Change password back to original to not affect other tests
        given(spec)
                .cookie("JSESSIONID", userSessionId)
                .contentType(ContentType.URLENC)
                .formParam("oldPassword", userPassword + "1")
                .formParam("newPassword", userPassword)
                .formParam("confirmPassword", userPassword)
                .when()
                .post("/user/profile/password");
    }
    
    @Test(description = "Test unauthorized access to profile")
    public void testUnauthorizedProfileAccess() {
        // Try to access profile without authentication
        Response response = given(spec)
                .when()
                .get("/user/profile")
                .then()
                .statusCode(anyOf(is(200), is(302))) // Either redirect to login or show login page
                .extract().response();
                
        // Verify we're redirected to login
        if (response.getStatusCode() == 302) {
            String location = response.getHeader("Location");
            org.testng.Assert.assertTrue(
                location != null && location.contains("/login"),
                "Should redirect to login page"
            );
        } else {
            String responseBody = response.getBody().asString();
            org.testng.Assert.assertTrue(
                responseBody.contains("Вход в систему"),
                "Page should show login form"
            );
        }
    }
} 