package pl.projekt.store.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import pl.projekt.store.model.Products;
import pl.projekt.store.repository.ProductsRepository;

@Service
public class ProductsService {

    public ProductsService(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    private final ProductsRepository productsRepository;

    public List<Products> findAll() {
        return productsRepository.findAll(Sort.by(Sort.Direction.ASC, "productId")); //sortowanie bo put zmienia kolejność w bazie danych
    }

    public boolean existsById(Long id) {
        return productsRepository.existsById(id);
    }

    public Products createProduct(String name, String description, String image, BigDecimal price, Integer stock) {
        Products product = new Products();
        product.setName(name);
        product.setDescription(description);
        product.setImage(image);
        product.setPrice(price);
        product.setStock(stock);
        product.setCreatedAt(LocalDateTime.now());
        return productsRepository.save(product);
    }

    public Products updateProduct(Long id, String name, String description, String image, BigDecimal price, Integer stock) {
        Products product = productsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produkt o ID " + id + " nie istnieje."));

        if (name != null) product.setName(name);
        if (description != null) product.setDescription(description);
        if (image != null) product.setImage(image);
        if (price != null) product.setPrice(price);
        if (stock != null) product.setStock(stock);

        product.setCreatedAt(LocalDateTime.now());
        return productsRepository.save(product);
    }

    public void deleteProduct(Long id) {
        if (!productsRepository.existsById(id)) {
            throw new IllegalArgumentException("Produkt o ID " + id + " nie istnieje.");
        }
        productsRepository.deleteById(id);
    }

    

}
