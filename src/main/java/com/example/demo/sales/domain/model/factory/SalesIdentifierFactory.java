package com.example.demo.sales.domain.model.factory;


import com.example.demo.common.identifiers.PlantInventoryEntryID;
import com.example.demo.common.identifiers.PurchaseOrderID;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public class SalesIdentifierFactory{
    public static String nextPOID() {
        return UUID.randomUUID().toString();
    }
}
