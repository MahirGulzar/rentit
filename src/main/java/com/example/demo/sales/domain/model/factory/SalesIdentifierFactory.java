package com.example.demo.sales.domain.model.factory;


import com.example.demo.common.identifiers.PlantInventoryEntryID;
import com.example.demo.common.identifiers.PurchaseOrderID;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
public class SalesIdentifierFactory{

    Long id=0l;


    public PurchaseOrderID nextPurchaseOrderID()
    {
//        Long newID = (Long)
//                this.session()
//                        .createSQLQuery(
//                                "select purchase_order_seq.nextval " +
//                                        "as purchase_id from dual")
//                        .addScalar("purchase_id")
//                        .uniqueResult();
//        return new PlantInventoryEntryID(newID);
        id++;
        return PurchaseOrderID.of(id);
    }

}
