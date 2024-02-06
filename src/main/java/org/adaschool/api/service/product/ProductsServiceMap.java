package org.adaschool.api.service.product;

import org.adaschool.api.exception.ProductNotFoundException;
import org.adaschool.api.repository.product.Product;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductsServiceMap implements ProductsService {
    private final Map<String, Product> productMap = new HashMap<>();

    @Override
    public Product save(Product product) {
        String idProduct = product.getId();
        productMap.put(idProduct, product);
        return product;
    }

    @Override
    public Optional<Product> findById(String id) {
        return Optional.ofNullable(productMap.get(id));
    }

    @Override
    public List<Product> all() {
        return productMap.values().stream().collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        if (!productMap.containsKey(id)) {

            throw new ProductNotFoundException(id);
        }
        productMap.remove(id);
    }

    @Override
    public Product update(Product product, String productId) {
        if (productMap.containsKey(productId)) {
            Product existingProduct = productMap.get(productId);

            existingProduct.setName(product.getName());
            existingProduct.setDescription(product.getDescription());
            existingProduct.setCategory(product.getCategory());
            existingProduct.setTags(product.getTags());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setImageUrl(product.getImageUrl());

            productMap.put(productId, existingProduct);

            return existingProduct;  // Asegúrate de devolver el producto actualizado
        } else {
            throw new ProductNotFoundException("Producto no encontrado con id: " + productId);
        }
    }
}
