package io.appactive.demo.product.repository;

import io.appactive.demo.common.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {

}
