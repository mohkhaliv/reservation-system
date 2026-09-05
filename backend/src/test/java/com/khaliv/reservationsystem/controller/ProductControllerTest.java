package com.khaliv.reservationsystem.controller;

import com.khaliv.reservationsystem.entity.Product;
import com.khaliv.reservationsystem.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    void createProduct_shouldReturnCreatedProduct() throws Exception {

        Product product = Product.builder()
                .id(1L)
                .name("Concert Ticket A")
                .description("Regular ticket")
                .price(new BigDecimal("250000"))
                .stock(5)
                .build();

        when(productService.createProduct(any(Product.class)))
                .thenReturn(product);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Concert Ticket A",
                                  "description": "Regular ticket",
                                  "price": 250000,
                                  "stock": 5
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name")
                        .value("Concert Ticket A"))
                .andExpect(jsonPath("$.stock").value(5));
    }

    @Test
    void getAllProducts_shouldReturnProducts() throws Exception {

        Product product = Product.builder()
                .id(1L)
                .name("Concert Ticket A")
                .price(new BigDecimal("250000"))
                .stock(5)
                .build();

        when(productService.getAllProducts())
                .thenReturn(List.of(product));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name")
                        .value("Concert Ticket A"));
    }
}