package ru.duremika.productservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.duremika.productservice.model.Product;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
}
