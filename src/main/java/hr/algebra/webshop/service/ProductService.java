package hr.algebra.webshop.service;

import hr.algebra.webshop.dto.ProductDto;
import hr.algebra.webshop.exception.EntityNotFoundException;
import hr.algebra.webshop.model.Category;
import hr.algebra.webshop.model.Product;
import hr.algebra.webshop.repository.CategoryRepository;
import hr.algebra.webshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public ProductDto getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Product with id: " + id + " not found"));
    }

    public void saveProduct(ProductDto productDto) {
        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setImageUrl(productDto.getImageUrl());

        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category with id: " + productDto.getCategoryId() + " not found"));

        product.setCategory(category);
        productRepository.save(product);
    }

    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setImageUrl(productDto.getImageUrl());

        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category with id: " + productDto.getCategoryId() + " not found"));

        product.setCategory(category);
        productRepository.save(product);
        return toDto(product);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        if (!product.getOrderItems().isEmpty()) {
            throw new IllegalStateException("Cannot delete product that is part of completed orders");
        }

        productRepository.deleteById(id);
    }


    public List<ProductDto> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public Product getProductEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with id: " + id + " not found"));
    }

    private ProductDto toDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getImageUrl()
        );
    }
}
