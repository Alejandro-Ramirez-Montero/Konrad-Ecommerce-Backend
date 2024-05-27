package com.tericcabrel.authapi.services;

import com.tericcabrel.authapi.dtos.ProductDto;
import com.tericcabrel.authapi.entities.Product;
import com.tericcabrel.authapi.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ModelMapper modelMapper;


    public List<ProductDto> getAllProducts() {
        List<Product> products = this.productRepository.findAll();
        return products.stream().map(product -> this.modelMapper.map(product, ProductDto.class)).toList();
    }

    public ProductDto getProductById(Long id) {
        Optional<Product> product = this.productRepository.findById(id);
        return product.map(productEntity -> this.modelMapper.map(productEntity, ProductDto.class)).orElse(null);
    }

    public ProductDto getProductByPath(String path) {
        Optional<Product> product = this.productRepository.findByPath(path);
        return product.map(productEntity -> this.modelMapper.map(productEntity, ProductDto.class)).orElse(null);
    }

    public boolean createProduct(ProductDto productDto){
        List<Product> products = this.productRepository.findAll();
        if(products.stream().noneMatch(product -> product.getId().equals(productDto.getId()))){
            this.productRepository.saveAndFlush(this.modelMapper.map(productDto, Product.class));
            return true;
        }
        else{
            return false;
        }
    }

    public boolean editProduct(ProductDto productDto) {
        Optional<Product> matchedProduct = this.productRepository.findById(productDto.getId());
        if(matchedProduct.isPresent()){
            Product productToEdit = matchedProduct.get();
            Product product = this.modelMapper.map(productDto, Product.class);
            productToEdit.setName(product.getName() != null? product.getName() : productToEdit.getName());
            productToEdit.setDescription(product.getName() != null? product.getDescription() : productToEdit.getDescription());
            productToEdit.setImage(product.getImage() != null? product.getImage() : productToEdit.getImage());
            productToEdit.setPrice(product.getPrice() > 0? product.getPrice() : productToEdit.getPrice());
            productToEdit.setCategory(product.getCategory() != null? product.getCategory() : productToEdit.getCategory());
            this.productRepository.save(productToEdit);
            return true;
        }
        else{
            return false;
        }
    }

    public boolean deleteProduct(Long id){
        Optional<Product> matchedProduct = this.productRepository.findById(id);
        if(matchedProduct.isPresent()){
            this.productRepository.delete(matchedProduct.get());
            return true;
        }
        else{
            return false;
        }
    }
}
