package pl.projekt.store.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.projekt.store.model.Products;

public interface ProductsRepository extends JpaRepository<Products, Long> {                                                                                                                                                                                                                                       //igorstefaniak
    @Override
    Optional<Products> findById(Long id);

    Optional<Products> findByName(String name);

}