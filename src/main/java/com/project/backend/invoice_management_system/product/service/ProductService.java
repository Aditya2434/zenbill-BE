package com.project.backend.invoice_management_system.product.service;

import com.project.backend.invoice_management_system.auth.model.User;
import com.project.backend.invoice_management_system.product.dto.ProductRequest;
import com.project.backend.invoice_management_system.product.dto.ProductResponse;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductRequest productRequest, User currentUser);

    List<ProductResponse> getAllProducts(User currentUser);

    ProductResponse getProductById(Long productId, User currentUser);

    ProductResponse updateProduct(Long productId, ProductRequest productRequest, User currentUser);

    void deleteProduct(Long productId, User currentUser);
}