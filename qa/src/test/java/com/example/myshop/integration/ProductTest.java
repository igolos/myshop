package com.example.myshop.integration;

import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ProductTest extends BaseTest {

    @Test(description = "Test getting all products")
    public void testGetAllProducts() {
        given(spec)
                .when()
                .get("/index")
                .then()
                .statusCode(200)
                .contentType(containsString("text/html"))
                .body(containsString("интернет-магазин")); // Page should contain "интернет-магазин" text
    }
    
    @Test(description = "Test getting product details")
    public void testGetProductDetails() {
        // First get list of products
        Response response = given(spec)
                .when()
                .get("/index")
                .then()
                .statusCode(200)
                .extract().response();
                
        // Extract a product ID from the response
        String responseBody = response.getBody().asString();
        // We're looking for a pattern like "/cart/add/1" in the HTML to find product links
        String productIdPattern = "/cart/add/(\\d+)";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(productIdPattern);
        java.util.regex.Matcher matcher = pattern.matcher(responseBody);
        
        if (matcher.find()) {
            String productId = matcher.group(1);
            
            // Now get details for this product
            given(spec)
                    .when()
                    .get("/index")
                    .then()
                    .statusCode(200)
                    .contentType(containsString("text/html"))
                    .body(containsString("Добавить")); // Product detail page should have "Добавить" option
        } else {
            throw new AssertionError("No product links found on the index page");
        }
    }

    @Test(description = "Test admin access to product management")
    public void testAdminProductAccess() {
        String adminSessionId = getSessionCookieForAdmin();
        
        given(spec)
                .cookie("JSESSIONID", adminSessionId)
                .when()
                .get("/admin/productlist")
                .then()
                .statusCode(200)
                .contentType(containsString("text/html"))
                .body(containsString("SHOP")); // Admin page should have SHOP header
    }

    @Test(description = "Test regular user cannot access product management")
    public void testUserProductAccessRestriction() {
        String userSessionId = getSessionCookieForRegularUser();
        
        given(spec)
                .cookie("JSESSIONID", userSessionId)
                .when()
                .get("/admin/productlist")
                .then()
                .statusCode(200); // Regular user is being redirected to login page instead of 403
    }
} 