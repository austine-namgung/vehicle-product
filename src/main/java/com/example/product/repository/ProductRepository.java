package com.example.product.repository;


import com.example.product.model.VehicleProduct;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<VehicleProduct, String> {
    
}
