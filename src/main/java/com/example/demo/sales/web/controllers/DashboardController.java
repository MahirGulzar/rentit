package com.example.demo.sales.web.controllers;

import com.example.demo.common.application.dto.BusinessPeriodDTO;
import com.example.demo.inventory.application.PlantInventoryEntryDTO;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.sales.application.dto.CatalogQueryDTO;
import com.example.demo.sales.application.services.SalesService;
import com.example.demo.sales.domain.model.PurchaseOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;


@Controller
@RequestMapping("/dashboard")
public class    DashboardController	{

    @Autowired
    SalesService salesService;



//    @GetMapping("/home")
//    public String getSomething(Model model)	{
////        model.addAttribute("catalogQuery",	new CatalogQueryDTO());
//        return	"hello";
//    }


    @GetMapping("/catalog/form")
    public String getQueryForm(Model model)	{
        model.addAttribute("catalogQuery",	new CatalogQueryDTO());
        return	"dashboard/catalog/query-form";
    }
    @PostMapping("/catalog/query")
    public String executeQuery(CatalogQueryDTO query, Model model) {

        //TODO need to change the query in SalesService to incorporate PlantInventoryEntry instead of PlantsWithCount
        model.addAttribute("plants", salesService.queryPlantCatalog(query.getName(), query.getRentalPeriod()));
        model.addAttribute("po",new PurchaseOrder());
        return "dashboard/catalog/query-result";
    }

}