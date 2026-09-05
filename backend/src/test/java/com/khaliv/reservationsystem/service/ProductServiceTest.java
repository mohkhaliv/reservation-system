package com.khaliv.reservationsystem.service;

import com.khaliv.reservationsystem.entity.Product;
import com.khaliv.reservationsystem.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .name("Concert Ticket A")
                .description("Regular ticket")
                .price(new BigDecimal("250000"))
                .stock(5)
                .build();
    }

    @Test
    void createProduct_shouldSaveAndReturnProduct() {

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> {
                    Product saved = invocation.getArgument(0);
                    saved.setId(1L);
                    return saved;
                });

        Product result = productService.createProduct(product);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Concert Ticket A");
        assertThat(result.getStock()).isEqualTo(5);

        verify(productRepository).save(product);
    }

    @Test
    void getAllProducts_shouldReturnProducts() {

        product.setId(1L);

        when(productRepository.findAll())
                .thenReturn(List.of(product));

        List<Product> result = productService.getAllProducts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Concert Ticket A");

        verify(productRepository).findAll();
    }
}