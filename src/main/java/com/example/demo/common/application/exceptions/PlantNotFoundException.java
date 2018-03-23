package com.example.demo.common.application.exceptions;

import com.example.demo.sales.application.dto.PurchaseOrderDTO;

public class PlantNotFoundException extends Throwable{
    private PurchaseOrderDTO purchaseOrder;
    public PlantNotFoundException(Long id) {
        super(String.format("Plant not found! (Plant id: %d)", id));
    }

    public PlantNotFoundException(String msg, PurchaseOrderDTO purchaseOrder) {
        super(msg);
        this.purchaseOrder = purchaseOrder;
    }


}


