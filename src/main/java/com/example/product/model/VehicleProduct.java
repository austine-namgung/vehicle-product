package com.example.product.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;



import lombok.Data;

@Data
@Entity
@Table(name = "vehicle_product")
public class VehicleProduct {
    @Id
    @Column(name = "order_tx")
    private String orderTx;
    @Column(name = "object_id")
    private String objectId;
    @Column(name = "make")
    private String make;
    @Column(name = "year")
    private String year;
    @Column(name = "model")
    private String model;
    @Column(name = "category")
    private String category;

  

}
