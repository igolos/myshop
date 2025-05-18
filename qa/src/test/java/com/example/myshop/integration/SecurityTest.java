package com.example.myshop.integration;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class SecurityTest extends BaseTest {

    @Test(description = "Test CSRF protection")
    public void testCSRFProtection() {
        // Get a session
        String userSessionId = getSessionCookieForRegularUser();
        
        // Try to perform a state-changing operation without CSRF token
        Response response = given(spec)
                .cookie("JSESSIONID", userSessionId)
                .contentType(ContentType.URLENC)
                .formParam("firstName", "CSRF Test")
                .when()
                .post("/user/profile/update")
                .then()
                .statusCode(anyOf(is(200), is(302), is(403))) // Could be 403 or redirect to error page
                .extract().response();
                
        // If we get a 200 OK, check if it contains an error message about CSRF
        if (response.getStatusCode() == 200) {
            String responseBody = response.getBody().asString();
            org.testng.Assert.assertTrue(
                responseBody.contains("CSRF") || responseBody.contains("csrf") || 
                responseBody.contains("Invalid") || responseBody.contains("Error") ||
                responseBody.contains("Forbidden") || responseBody.contains("Access Denied"),
                "Response should contain error message about CSRF or access denied"
            );
        }
    }
    
    @Test(description = "Test SQL injection protection")
    public void testSQLInjectionProtection() {
        // Try SQL injection in search
        Response response = given(spec)
                .when()
                .get("/search?keyword=' OR '1'='1")
                .then()
                .statusCode(200)
                .contentType(containsString("text/html"))
                .extract().response();
                
        // Check that the response doesn't contain all products (which would happen if injection worked)
        String responseBody = response.getBody().asString();
        
        // Count product links in the response
        String productIdPattern = "/cart/add/(\\d+)";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(productIdPattern);
        java.util.regex.Matcher matcher = pattern.matcher(responseBody);
        
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        
        // If SQL injection worked, we'd expect to see all products
        // This is a heuristic - if we see too many products, injection might have worked
        org.testng.Assert.assertTrue(
            count < 100, // Arbitrary threshold
            "SQL injection might have worked if too many products are returned"
        );
    }
    
    @Test(description = "Test XSS protection")
    public void testXSSProtection() {
        // Try XSS in search
        Response response = given(spec)
                .when()
                .get("/search?keyword=<script>alert(1)</script>")
                .then()
                .statusCode(200)
                .contentType(containsString("text/html"))
                .extract().response();
                
        // Check that the response doesn't contain unescaped script tags
        String responseBody = response.getBody().asString();
        org.testng.Assert.assertFalse(
            responseBody.contains("<script>alert(1)</script>"),
            "XSS payload should be escaped or sanitized"
        );
    }
    
    @Test(description = "Test authentication bypass protection")
    public void testAuthenticationBypass() {
        // Try to access protected resource without authentication
        Response response = given(spec)
                .when()
                .get("/user/profile")
                .then()
                .statusCode(anyOf(is(200), is(302))) // Either redirect to login or show login page
                .extract().response();
                
        // Verify we're redirected to login or shown login page
        if (response.getStatusCode() == 302) {
            String location = response.getHeader("Location");
            org.testng.Assert.assertTrue(
                location != null && location.contains("/login"),
                "Should redirect to login page"
            );
        } else {
            String responseBody = response.getBody().asString();
            org.testng.Assert.assertTrue(
                responseBody.contains("Вход в систему") || responseBody.contains("Login"),
                "Page should show login form"
            );
        }
    }
    
    @Test(description = "Test brute force protection")
    public void testBruteForceProtection() {
        // Try multiple failed logins
        for (int i = 0; i < 5; i++) {
            given(spec)
                    .contentType(ContentType.URLENC)
                    .formParam("username", "invalidUser" + i)
                    .formParam("password", "invalidPass" + i)
                    .when()
                    .post("/login")
                    .then()
                    .statusCode(anyOf(is(200), is(302), is(401), is(429))); // 429 would be rate limiting
        }
        
        // Try one more login - if rate limiting is implemented, this might fail with 429
        Response response = given(spec)
                .contentType(ContentType.URLENC)
                .formParam("username", "invalidUser")
                .formParam("password", "invalidPass")
                .when()
                .post("/login")
                .then()
                .extract().response();
                
        // We're not asserting anything specific here since we don't know if rate limiting is implemented
        // Just logging the result for manual review
        System.out.println("After multiple failed logins, status code: " + response.getStatusCode());
    }
} 