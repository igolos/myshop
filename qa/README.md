# MyShop Integration Tests

This module contains integration tests for the MyShop application using REST Assured and TestNG.

## Test Coverage

The tests cover the following functionality:

1. **Authentication Tests**
   - User registration
   - User login
   - Access control

2. **Product Tests**
   - Listing products
   - Viewing product details
   - Admin product management

3. **Shopping Cart Tests**
   - Adding products to cart
   - Removing products from cart
   - Clearing the cart

4. **Order Tests**
   - Placing orders
   - Viewing order history
   - Viewing order details
   - Deleting orders
   - Admin order management

## Running the Tests

These integration tests are designed to run against a running instance of the MyShop application. Follow these steps to run the tests:

1. Start the MyShop application:
```
cd ..
./mvnw spring-boot:run
```

2. Wait for the application to fully start up.

3. In a separate terminal, run the integration tests:
```
./mvnw test -pl qa -DskipIntegrationTests=false
```

Note: The `-DskipIntegrationTests=false` parameter is needed to run the integration tests since they are configured to be skipped by default in the regular build process.

## Test Configuration

The tests expect the application to be running on `http://localhost:8080` with the default test users:

- Admin user: username `admin`, password `admin`
- Regular user: username `user`, password `Qwerty12_`

If your application uses different credentials or is running on a different URL, update the corresponding values in the `BaseTest.java` file. 