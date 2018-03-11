package com.example.demo.sales.application.services;


import com.example.demo.common.application.dto.BusinessPeriodDTO;
import com.example.demo.common.domain.model.Address;
import com.example.demo.common.identifiers.CustomerID;
import com.example.demo.common.identifiers.PlantInventoryEntryID;
import com.example.demo.common.identifiers.PurchaseOrderID;
import com.example.demo.inventory.application.PlantInventoryEntryDTO;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.repository.PlantInventoryEntryRepository;
import com.example.demo.sales.domain.model.POStatus;
import com.example.demo.sales.domain.model.PurchaseOrder;
import com.example.demo.sales.domain.model.factory.SalesIdentifierFactory;
import com.example.demo.sales.domain.model.validators.PurchaseOrderValidator;
import com.example.demo.sales.domain.repository.PurchaseOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.DataBinder;

import java.util.List;

@Service
public class SalesService {
    @Autowired
    PlantInventoryEntryRepository plantRepo;

    @Autowired
    PurchaseOrderRepository orderRepo;

//    @Autowired // Todo make it autowire , have to verifyy
    SalesIdentifierFactory identifierFactory;

//    public List<PlantInventoryEntry> queryPlantCatalog(String name , BusinessPeriodDTO rentalPeriod)
//    {
//        return plantRepo.findAll();
//    }

    public void createPO(PlantInventoryEntryDTO plantDTO , BusinessPeriodDTO periodDTO)
    {
        PurchaseOrder po = PurchaseOrder.of(
                new PurchaseOrderID(),
                PlantInventoryEntryID.of(plantDTO.get_id()),
                new CustomerID(),
                Address.of("dummy@dummy.com"),
                POStatus.OPEN,
                periodDTO.asBusinessPeriod());

        DataBinder binder = new DataBinder(po);
        binder.addValidators(new PurchaseOrderValidator());
        binder.validate();
        if (!binder.getBindingResult().hasErrors())
            orderRepo.save(po);



    }
}
