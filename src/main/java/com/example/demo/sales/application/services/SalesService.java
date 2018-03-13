package com.example.demo.sales.application.services;


import com.example.demo.common.application.dto.BusinessPeriodDTO;
import com.example.demo.common.domain.model.BusinessPeriod;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.repository.PlantInventoryEntryRepository;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import com.example.demo.sales.domain.model.PurchaseOrder;
import com.example.demo.sales.domain.model.factory.SalesIdentifierFactory;
import com.example.demo.sales.domain.repository.PurchaseOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalesService {


    @Autowired
    PlantInventoryEntryRepository plantRepo;

    @Autowired
    PurchaseOrderRepository orderRepo;


    // Todo make it autowire , have to verifyy
    @Autowired
    SalesIdentifierFactory identifierFactory;

    @Autowired
    PurchaseOrderAssembler purchaseOrderAssembler;

    public List<PlantInventoryEntry> queryPlantCatalog(String name , BusinessPeriodDTO rentalPeriod)
    {
//        return plantRepo.findByNameContaining(name);

        return plantRepo.findByComplicatedQuery(name.toLowerCase(),rentalPeriod.getStartDate(),rentalPeriod.getEndDate());
    }


    public PurchaseOrderDTO createPO(PurchaseOrderDTO purchaseOrderDTO)
    {
        PurchaseOrder po = PurchaseOrder.of(
                plantRepo.findOne(purchaseOrderDTO.getPlant().get_id()),
                BusinessPeriod.of(
                        purchaseOrderDTO.getRentalPeriod().getStartDate(),
                        purchaseOrderDTO.getRentalPeriod().getEndDate()
                        ));


        orderRepo.save(po);

        return purchaseOrderAssembler.toResource(po);


//        DataBinder binder = new DataBinder(po);
//        binder.addValidators(new PurchaseOrderValidator());
//        binder.validate();
//        if (!binder.getBindingResult().hasErrors())
//            orderRepo.save(po);



    }


}
