package com.example.myshop.integration;

import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class SearchTest extends BaseTest {

    @Test(description = "Test searching for products")
    public void testSearchProducts() {
        // Search for a common term that should return results
        Response response = given(spec)
                .when()
                .get("/search?keyword=product")
                .then()
                .statusCode(200)
                .contentType(containsString("text/html"))
                .extract().response();
                
        String responseBody = response.getBody().asString();
        org.testng.Assert.assertTrue(
            responseBody.contains("product") || responseBody.contains("Product"),
            "Search results should contain the searched keyword"
        );
    }
    
    @Test(description = "Test searching with empty query")
    public void testEmptySearch() {
        given(spec)
                .when()
                .get("/search?keyword=")
                .then()
                .statusCode(200)
                .contentType(containsString("text/html"));
    }
    
    @Test(description = "Test searching for non-existent products")
    public void testSearchNonExistentProducts() {
        // Search for a term that should not return results
        String randomSearchTerm = "nonexistentproduct" + System.currentTimeMillis();
        
        Response response = given(spec)
                .when()
                .get("/search?keyword=" + randomSearchTerm)
                .then()
                .statusCode(200)
                .contentType(containsString("text/html"))
                .extract().response();
                
        String responseBody = response.getBody().asString();
        org.testng.Assert.assertTrue(
            responseBody.contains("No products found") || 
            !responseBody.contains("/cart/add/"),
            "Search for non-existent product should not return product links"
        );
    }
    
    @Test(description = "Test search with special characters")
    public void testSearchWithSpecialCharacters() {
        // Search with special characters to test input sanitization
        given(spec)
                .when()
                .get("/search?keyword=%3Cscript%3Ealert(1)%3C/script%3E")
                .then()
                .statusCode(200)
                .contentType(containsString("text/html"))
                .body(not(containsString("<script>alert(1)</script>")));
    }
} 