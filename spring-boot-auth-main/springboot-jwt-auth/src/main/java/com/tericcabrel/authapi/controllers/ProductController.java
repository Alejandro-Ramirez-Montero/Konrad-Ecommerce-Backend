package com.tericcabrel.authapi.controllers;

import com.tericcabrel.authapi.dtos.ProductDto;
import com.tericcabrel.authapi.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<ProductDto> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        ProductDto product = productService.getProductById(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @GetMapping("/getByPath/{path}")
    public ResponseEntity<ProductDto> getProductByPath(@PathVariable String path) {
        ProductDto product = productService.getProductByPath(path);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Boolean> createProduct(@RequestBody ProductDto productDto) {
        boolean operationSuccess = productService.createProduct(productDto);
        return new ResponseEntity<>(operationSuccess, HttpStatus.CREATED);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Boolean> editProduct(@RequestBody ProductDto productDto) {
        boolean operationSuccess = productService.editProduct(productDto);
        return new ResponseEntity<>(operationSuccess, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Boolean> deleteProduct(@PathVariable Long id) {
        boolean operationSuccess = productService.deleteProduct(id);
        return new ResponseEntity<>(operationSuccess, HttpStatus.NO_CONTENT);
    }
}
