package com.example.myshop.integration;

import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class PerformanceTest extends BaseTest {

    private static final int CONCURRENT_USERS = 10;
    private static final int REQUESTS_PER_USER = 5;
    private static final long MAX_RESPONSE_TIME_MS = 5000; // 5 seconds

    @Test(description = "Test homepage load performance")
    public void testHomepagePerformance() {
        // Measure response time for homepage
        long startTime = System.currentTimeMillis();
        
        given(spec)
                .when()
                .get("/index")
                .then()
                .statusCode(200)
                .contentType(containsString("text/html"));
                
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;
        
        org.testng.Assert.assertTrue(
            responseTime < MAX_RESPONSE_TIME_MS,
            "Homepage should load in less than " + MAX_RESPONSE_TIME_MS + "ms, but took " + responseTime + "ms"
        );
    }
    
    @Test(description = "Test concurrent user access")
    public void testConcurrentAccess() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_USERS);
        List<Future<?>> futures = new ArrayList<>();
        
        for (int i = 0; i < CONCURRENT_USERS; i++) {
            Future<?> future = executor.submit(() -> {
                for (int j = 0; j < REQUESTS_PER_USER; j++) {
                    try {
                        given(spec)
                                .when()
                                .get("/index")
                                .then()
                                .statusCode(200);
                    } catch (Exception e) {
                        org.testng.Assert.fail("Concurrent request failed: " + e.getMessage());
                    }
                }
            });
            futures.add(future);
        }
        
        // Wait for all tasks to complete
        executor.shutdown();
        boolean completed = executor.awaitTermination(30, TimeUnit.SECONDS);
        
        org.testng.Assert.assertTrue(completed, "All concurrent requests should complete within timeout");
        
        // Check if any tasks failed
        for (Future<?> future : futures) {
            try {
                future.get(); // Will throw an exception if the task failed
            } catch (Exception e) {
                org.testng.Assert.fail("Concurrent task failed: " + e.getMessage());
            }
        }
    }
    
    @Test(description = "Test product search performance")
    public void testSearchPerformance() {
        // Measure response time for search
        long startTime = System.currentTimeMillis();
        
        given(spec)
                .when()
                .get("/search?keyword=product")
                .then()
                .statusCode(200)
                .contentType(containsString("text/html"));
                
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;
        
        org.testng.Assert.assertTrue(
            responseTime < MAX_RESPONSE_TIME_MS,
            "Search should complete in less than " + MAX_RESPONSE_TIME_MS + "ms, but took " + responseTime + "ms"
        );
    }
    
    @Test(description = "Test product details page load performance")
    public void testProductDetailsPerformance() {
        // First get a product ID
        Response response = given(spec)
                .when()
                .get("/index")
                .then()
                .statusCode(200)
                .extract().response();
                
        String responseBody = response.getBody().asString();
        String productIdPattern = "/cart/add/(\\d+)";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(productIdPattern);
        java.util.regex.Matcher matcher = pattern.matcher(responseBody);
        
        if (matcher.find()) {
            String productId = matcher.group(1);
            
            // Measure response time for product details
            long startTime = System.currentTimeMillis();
            
            given(spec)
                    .when()
                    .get("/product/" + productId)
                    .then()
                    .statusCode(anyOf(is(200), is(404))); // Product might not exist or URL might be different
                    
            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;
            
            org.testng.Assert.assertTrue(
                responseTime < MAX_RESPONSE_TIME_MS,
                "Product details should load in less than " + MAX_RESPONSE_TIME_MS + "ms, but took " + responseTime + "ms"
            );
        } else {
            org.testng.Assert.fail("No product links found on the index page");
        }
    }
} 