package org.adaschool.api.controller.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.adaschool.api.exception.ProductNotFoundException;
import org.adaschool.api.repository.product.Product;
import org.adaschool.api.repository.product.ProductDto;
import org.adaschool.api.service.product.ProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/products/")
public class ProductsController {

    private final ProductsService productsService;
    @Autowired
    public ProductsController(ProductsService productsService) {
        this.productsService = productsService;
    }

    @Operation(summary = "Create a new product")
    @ApiResponse(responseCode = "201", description = "Product created successfully")
    @PostMapping
    public ResponseEntity<Product> save(@RequestBody Product product) {
        Product savedProduct = productsService.save(product);
        URI productUri = URI.create("/v1/products/" + savedProduct.getId());
        return ResponseEntity.created(productUri).body(savedProduct);
    }

    @Operation(summary = "Get all products")
    @ApiResponse(responseCode = "200", description = "List of products")
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productsService.all();
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Get a product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("{id}")
    public ResponseEntity<Product> findById(@PathVariable("id") String id) {
        return productsService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() ->
                        new ProductNotFoundException(id));
    }

    @Operation(summary = "Update a product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping("{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable("id") String id, @RequestBody ProductDto productDto) {
        Optional<Product> optionalUser = productsService.findById(id);

        if (optionalUser.isPresent()) {
            Product existinProduct = optionalUser.get();
            existinProduct.setName(productDto.getName());
            existinProduct.setDescription(productDto.getDescription());
            existinProduct.setCategory(productDto.getCategory());
            existinProduct.setPrice(productDto.getPrice());

            productsService.save(existinProduct);

            return ResponseEntity.ok().build();
        }
        else {
            throw  new ProductNotFoundException(id);
        }
    }

    @Operation(summary = "Delete a product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") String id) {
        Optional<Product> existingProduct = productsService.findById(id);

        if (existingProduct.isEmpty()) {
            throw new ProductNotFoundException(id);
        }

        productsService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
