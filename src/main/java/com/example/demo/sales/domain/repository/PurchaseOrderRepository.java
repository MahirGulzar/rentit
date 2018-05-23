package com.example.demo.sales.domain.repository;


import com.example.demo.inventory.domain.model.PlantInventoryItem;
import com.example.demo.sales.domain.model.POStatus;
import com.example.demo.sales.domain.model.PurchaseOrder;
import com.example.demo.inventory.domain.model.PlantReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder,Long> {


    List<PurchaseOrder> findPurchaseOrderByStatus(POStatus status);

    PurchaseOrder findPurchaseOrderById(Long ID);


    // TODO might verify
    @Query("select r.plant from PlantReservation r where r.schedule.startDate=?1")
    List<PlantInventoryItem> findPlantsToDispatch(LocalDate startDate);
}
