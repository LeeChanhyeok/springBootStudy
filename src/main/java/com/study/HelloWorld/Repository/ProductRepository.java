package com.study.HelloWorld.Repository;

import com.study.HelloWorld.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
