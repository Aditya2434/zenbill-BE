package com.project.backend.invoice_management_system.product.service;

import com.project.backend.invoice_management_system.auth.model.User;
import com.project.backend.invoice_management_system.common.exception.ResourceNotFoundException;
import com.project.backend.invoice_management_system.company.model.Company;
import com.project.backend.invoice_management_system.product.dto.ProductRequest;
import com.project.backend.invoice_management_system.product.dto.ProductResponse;
import com.project.backend.invoice_management_system.product.model.Product;
import com.project.backend.invoice_management_system.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public ProductResponse createProduct(ProductRequest productRequest, User currentUser) {
        Company company = getCompanyFromUser(currentUser);

        // Check if HSN Code already exists for this company (only if HSN Code is provided)
        if (productRequest.getHsnCode() != null && !productRequest.getHsnCode().trim().isEmpty()) {
            if (productRepository.existsByHsnCodeAndCompanyId(productRequest.getHsnCode(), company.getId())) {
                throw new IllegalStateException("HSN Code '" + productRequest.getHsnCode() + "' is already present. Please use a different HSN Code.");
            }
        }

        Product product = Product.builder()
                .productName(productRequest.getProductName())
                .hsnCode(productRequest.getHsnCode())
                .uom(productRequest.getUom())
                .company(company) // The multi-tenancy link
                .build();

        Product savedProduct = productRepository.save(product);
        return productToResponse(savedProduct);
    }

    @Override
    public List<ProductResponse> getAllProducts(User currentUser) {
        Company company = getCompanyFromUser(currentUser);
        return productRepository.findByCompanyId(company.getId())
                .stream()
                .map(this::productToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse getProductById(Long productId, User currentUser) {
        Company company = getCompanyFromUser(currentUser);
        Product product = productRepository.findByIdAndCompanyId(productId, company.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        return productToResponse(product);
    }

    @Override
    public ProductResponse updateProduct(Long productId, ProductRequest productRequest, User currentUser) {
        Company company = getCompanyFromUser(currentUser);
        Product product = productRepository.findByIdAndCompanyId(productId, company.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        // Check if the new HSN Code already exists for this company (excluding current product)
        if (productRequest.getHsnCode() != null && !productRequest.getHsnCode().trim().isEmpty()) {
            Optional<Product> existingProduct = productRepository.findByHsnCodeAndCompanyIdAndIdNot(
                    productRequest.getHsnCode(), company.getId(), productId);
            if (existingProduct.isPresent()) {
                throw new IllegalStateException("HSN Code '" + productRequest.getHsnCode() + "' is already present. Please use a different HSN Code.");
            }
        }

        product.setProductName(productRequest.getProductName());
        product.setHsnCode(productRequest.getHsnCode());
        product.setUom(productRequest.getUom());

        Product updatedProduct = productRepository.save(product);
        return productToResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Long productId, User currentUser) {
        Company company = getCompanyFromUser(currentUser);
        Product product = productRepository.findByIdAndCompanyId(productId, company.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        productRepository.delete(product);
    }

    // --- HELPER METHODS ---

    private Company getCompanyFromUser(User user) {
        if (user.getCompany() == null) {
            throw new IllegalStateException("User is not associated with a company.");
        }
        return user.getCompany();
    }

    private ProductResponse productToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .hsnCode(product.getHsnCode())
                .uom(product.getUom())
                .companyId(product.getCompany().getId())
                .build();
    }
}