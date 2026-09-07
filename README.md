# Mr-DIY Spring Boot Rewrite

This folder is a Java Spring Boot rewrite of the original Laravel ecommerce API in `../Mr-DIY`.

## What Was Assessed

The original project is a Laravel 11 API, not a Java app. Its main business areas are:

- user registration/login with token auth and roles
- public product/category browsing
- admin product/category/tag/attribute management
- cart handling for guests and logged-in users
- favorites
- delivery addresses
- order creation from cart
- Paystack-style payment transaction records
- payment-success order updates
- returned item tracking

Important issues found in the Laravel code:

- database migrations use string IDs for many foreign keys, so referential integrity is weak
- several relationships are enforced only in application code
- product image upload/optimization code is heavily duplicated
- `ProductService::delete_product` references `$product_attribute_value` outside its scope
- cart creation accepts repeated cart rows instead of merging duplicate items
- payment webhook handling and order updates should be more isolated and testable
- very little project-specific test coverage exists

## Rewrite Choices

- Spring Boot 2.7.18 because the machine currently has Java 11 installed
- Spring Web, Spring Data JPA, Spring Security, Bean Validation
- MySQL for runtime persistence
- H2 in MySQL compatibility mode for JUnit tests
- UUID primary keys for the ecommerce entities
- database-backed bearer tokens as a straightforward replacement for Sanctum-style tokens
- explicit service validations for cart attributes, favorites, orders, and returns

## Run With MySQL

Create a MySQL database/user or rely on the default `createDatabaseIfNotExist=true` URL.

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/mr_diy?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_password"
mvn spring-boot:run
```

Seeded admin login:

```json
{
  "email": "admin@mrdiy.local",
  "password": "password"
}
```

## Test

```powershell
mvn test
```

The current machine did not have Maven installed during the rewrite. A local Maven 3.9.9 copy was downloaded for verification, so this also works from this folder:

```powershell
$env:JAVA_HOME="C:\Program Files\Microsoft\jdk-11.0.16.101-hotspot"
.\apache-maven-3.9.9\bin\mvn.cmd test
```

## Main Endpoint Mapping

- `POST /api/register`
- `POST /api/auth`
- `POST /api/admin/auth`
- `GET /api/product/get-all-products`
- `GET /api/product/view-product/{id}`
- `POST /api/admin/product`
- `GET /api/category/get-all-categories`
- `GET /api/category/view-category/{id}`
- `PUT /api/cart/add-to-cart`
- `PUT /api/cart/update-cart-item-quantity`
- `GET /api/cart/view-cart`
- `DELETE /api/cart/remove-from-cart`
- `DELETE /api/cart/clear-cart`
- `POST /api/user/favorite/add-to-favorites`
- `GET /api/user/favorite/view-favorites`
- `DELETE /api/user/favorite/remove-from-favorites`
- `POST /api/user/delivery-address`
- `GET /api/user/delivery-address`
- `PUT /api/user/delivery-address/{id}`
- `DELETE /api/user/delivery-address/{id}`
- `POST /api/user/order/create-order`
- `POST /api/user/order/test-order-payment`
- `GET /api/user/order/view-user-orders`
- `PUT /api/admin/order/update-order-delivery-status/{id}`
- `POST /api/admin/order/return-items/{orderId}`
- `PUT /api/admin/order/returned-items/{id}/sell`
# Ecommerce-Store
