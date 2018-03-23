package com.example.demo.sales.domain.repository;


import com.example.demo.sales.domain.model.POStatus;
import com.example.demo.sales.domain.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder,Long> {


    List<PurchaseOrder> findPurchaseOrderByStatus(POStatus status);

    PurchaseOrder findPurchaseOrderById(String ID);
}
