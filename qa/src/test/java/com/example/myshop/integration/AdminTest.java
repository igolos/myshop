package com.example.myshop.integration;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AdminTest extends BaseTest {

    @Test(description = "Test admin dashboard access")
    public void testAdminDashboardAccess() {
        // Get admin session
        String adminSessionId = getSessionCookieForAdmin();
        
        // Access admin dashboard
        Response response = given(spec)
                .cookie("JSESSIONID", adminSessionId)
                .when()
                .get("/admin")
                .then()
                .statusCode(200)
                .contentType(containsString("text/html"))
                .extract().response();
                
        // Verify we're on the admin dashboard
        String responseBody = response.getBody().asString();
        org.testng.Assert.assertTrue(
            responseBody.contains("Admin") || responseBody.contains("Dashboard"),
            "Page should show admin dashboard"
        );
    }
    
    @Test(description = "Test admin user management")
    public void testAdminUserManagement() {
        // Get admin session
        String adminSessionId = getSessionCookieForAdmin();
        
        // Access user management
        Response response = given(spec)
                .cookie("JSESSIONID", adminSessionId)
                .when()
                .get("/admin/user")
                .then()
                .statusCode(200)
                .contentType(containsString("text/html"))
                .extract().response();
                
        // Verify we're on the user management page
        String responseBody = response.getBody().asString();
        org.testng.Assert.assertTrue(
            responseBody.contains("Users") || responseBody.contains("user list"),
            "Page should show user management"
        );
    }
    
    @Test(description = "Test admin product management")
    public void testAdminProductManagement() {
        // Get admin session
        String adminSessionId = getSessionCookieForAdmin();
        
        // Access product management
        Response response = given(spec)
                .cookie("JSESSIONID", adminSessionId)
                .when()
                .get("/admin/product")
                .then()
                .statusCode(200)
                .contentType(containsString("text/html"))
                .extract().response();
                
        // Verify we're on the product management page
        String responseBody = response.getBody().asString();
        org.testng.Assert.assertTrue(
            responseBody.contains("Products") || responseBody.contains("product list"),
            "Page should show product management"
        );
    }
    
    @Test(description = "Test admin order management")
    public void testAdminOrderManagement() {
        // Get admin session
        String adminSessionId = getSessionCookieForAdmin();
        
        // Access order management
        Response response = given(spec)
                .cookie("JSESSIONID", adminSessionId)
                .when()
                .get("/admin/orders")
                .then()
                .statusCode(200)
                .contentType(containsString("text/html"))
                .extract().response();
                
        // Verify we're on the order management page
        String responseBody = response.getBody().asString();
        org.testng.Assert.assertTrue(
            responseBody.contains("Orders") || responseBody.contains("order list"),
            "Page should show order management"
        );
    }
    
    @Test(description = "Test regular user cannot access admin pages")
    public void testRegularUserCannotAccessAdmin() {
        // Get regular user session
        String userSessionId = getSessionCookieForRegularUser();
        
        // Try to access admin dashboard
        Response response = given(spec)
                .cookie("JSESSIONID", userSessionId)
                .when()
                .get("/admin")
                .then()
                .statusCode(anyOf(is(200), is(302), is(403))) // Either redirect to login, access denied, or show login page
                .extract().response();
                
        // Verify we're either redirected to login or shown access denied
        if (response.getStatusCode() == 302) {
            String location = response.getHeader("Location");
            org.testng.Assert.assertTrue(
                location != null && (location.contains("/login") || location.contains("/access-denied")),
                "Should redirect to login page or access denied page"
            );
        } else {
            String responseBody = response.getBody().asString();
            org.testng.Assert.assertTrue(
                responseBody.contains("Вход в систему") || responseBody.contains("Access Denied") || responseBody.contains("Forbidden"),
                "Page should show login form or access denied message"
            );
        }
    }
    
    @Test(description = "Test admin can create new product")
    public void testAdminCreateProduct() {
        // Get admin session
        String adminSessionId = getSessionCookieForAdmin();
        
        // Create a new product
        String uniqueProductName = "Test Product " + System.currentTimeMillis();
        
        Response response = given(spec)
                .cookie("JSESSIONID", adminSessionId)
                .contentType(ContentType.URLENC)
                .formParam("name", uniqueProductName)
                .formParam("price", "99.99")
                .formParam("description", "Test product description")
                .formParam("quantity", "10")
                .when()
                .post("/admin/product/new")
                .then()
                .statusCode(anyOf(is(200), is(302))) // Either OK or redirect
                .extract().response();
                
        // Verify we're redirected to product management or shown success message
        if (response.getStatusCode() == 302) {
            String location = response.getHeader("Location");
            org.testng.Assert.assertTrue(
                location != null && location.contains("/admin/product"),
                "Should redirect to product management page"
            );
        } else {
            String responseBody = response.getBody().asString();
            org.testng.Assert.assertTrue(
                responseBody.contains("success") || responseBody.contains("Success") || responseBody.contains("product list"),
                "Page should show success message or product list"
            );
        }
    }
} 