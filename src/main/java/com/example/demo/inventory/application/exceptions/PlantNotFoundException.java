package com.example.demo.inventory.application.exceptions;

import com.example.demo.sales.application.dto.PurchaseOrderDTO;

public class PlantNotFoundException extends Throwable {

    private PurchaseOrderDTO purchaseOrder;


    public PlantNotFoundException(String msg) {
        super(msg);
        this.purchaseOrder = null;
    }

    public PlantNotFoundException(String msg, PurchaseOrderDTO purchaseOrder) {
        super(msg);
        this.purchaseOrder = purchaseOrder;
    }

    public PurchaseOrderDTO getPurchaseOrder() {
        return purchaseOrder;
    }

}