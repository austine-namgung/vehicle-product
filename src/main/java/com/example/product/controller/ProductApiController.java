package com.example.product.controller;



import com.example.product.model.VehicleProduct;
import com.example.product.repository.ProductRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductApiController {
	private final ProductRepository repository;
	 

    @GetMapping("/{tx}")
    public VehicleProduct vehicleTxInfo(@PathVariable String  tx){
        VehicleProduct vehicleProduct = repository.findById(tx).orElse(null);
        return vehicleProduct;
	}
	
	


}
