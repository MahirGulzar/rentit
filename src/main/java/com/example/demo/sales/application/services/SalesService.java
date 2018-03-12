package com.example.demo.sales.application.services;


import com.example.demo.common.application.dto.BusinessPeriodDTO;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.repository.PlantInventoryEntryRepository;
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

    public List<PlantInventoryEntry> queryPlantCatalog(String name , BusinessPeriodDTO rentalPeriod)
    {
//        return plantRepo.findByNameContaining(name);

        return plantRepo.findByComplicatedQuery(name.toLowerCase(),rentalPeriod.getStartDate(),rentalPeriod.getEndDate());
    }


//    public void createPO(PlantInventoryEntryDTO plantDTO , BusinessPeriodDTO periodDTO)
//    {
//        PurchaseOrder po = PurchaseOrder.of(
//                identifierFactory.nextPurchaseOrderID(),
//                PlantInventoryEntryID.of(plantDTO.get_id()),
//                new CustomerID(),
//                Address.of("dummy@dummy.com"),
//                POStatus.OPEN,
//                periodDTO.asBusinessPeriod());
//
//        DataBinder binder = new DataBinder(po);
//        binder.addValidators(new PurchaseOrderValidator());
//        binder.validate();
//        if (!binder.getBindingResult().hasErrors())
//            orderRepo.save(po);
//
//
//
//    }


}
