package org.adaschool.api.controller.product;

import org.adaschool.api.exception.ProductNotFoundException;
import org.adaschool.api.repository.product.Product;
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

    @PostMapping
    public ResponseEntity<Product> save(@RequestBody Product product) {
        Product savedProduct = productsService.save(product);
        URI productUri = URI.create("/v1/products/" + savedProduct.getId());
        return ResponseEntity.created(productUri).body(savedProduct);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productsService.all();
        return ResponseEntity.ok(products);
    }

    @GetMapping("{id}")
    public ResponseEntity<Product> findById(@PathVariable("id") String id) {
        return productsService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() ->
                        new ProductNotFoundException(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable("id") String id, @RequestBody Product product) {
        Product updatedProduct = productsService.update(product, id);
        if (updatedProduct != null) {
            return ResponseEntity.ok(updatedProduct);
        } else {
            throw new ProductNotFoundException(id);
        }
    }

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
