package com.example.demo.sales.rest;

import com.example.demo.DemoApplication;
import com.example.demo.common.application.dto.BusinessPeriodDTO;
import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.application.services.InventoryService;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.model.PlantInventoryItem;
import com.example.demo.inventory.domain.model.PlantReservation;
import com.example.demo.inventory.domain.repository.PlantInventoryEntryRepository;
import com.example.demo.inventory.domain.repository.PlantReservationRepository;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import com.example.demo.sales.domain.model.PurchaseOrder;
import com.example.demo.sales.domain.repository.PurchaseOrderRepository;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.SqlConfig.TransactionMode.ISOLATED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import static org.hamcrest.Matchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DemoApplication.class) // Check if the name of this class is correct or not
@WebAppConfiguration

//@DirtiesContext(classMode=DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
//@Sql(scripts = "/plants-dataset.sql")
//@Sql(
//        scripts = "/delete.sql",
//        executionPhase = AFTER_TEST_METHOD,
//        config = @SqlConfig(transactionMode = ISOLATED)
//)
public class SalesRestControllerTests {
    @Autowired
    PlantInventoryEntryRepository repo;

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Autowired
    @Qualifier("_halObjectMapper")
    ObjectMapper mapper;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                .apply(springSecurity()).build();
    }

    @Test
//    @Sql("/plants-dataset.sql")
    public  void findPlants() throws Exception {

        String name="exc";
        MvcResult result = mockMvc.perform(get("/api/sales/plants?name="+ name +"&startDate="+ LocalDate.now() +"&endDate="+ LocalDate.now().plusDays(2)
        ).with(user("admin").password("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(header().string("Location", isEmptyOrNullString()))
                .andReturn();

        Resources<PlantInventoryEntryDTO> plants = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<Resources<PlantInventoryEntryDTO>>() {});

        List<PlantInventoryEntryDTO> entryDTOS = new ArrayList<PlantInventoryEntryDTO>(plants.getContent());
        System.out.println(entryDTOS);
        assert((entryDTOS.size()==4));
    }

//    @Test
//    @Sql("/plants-dataset.sql")
//    public void testGetAllPlants() throws Exception {
//
//        // Move few lines of code to function so i can resue it.
//        List<PlantInventoryEntryDTO> plants =  this.findPlants("exc", LocalDate.of(2018,4,14), LocalDate.of(2018,4,25));
//
//        assertThat(plants.size()).isEqualTo(3);
//
//        PurchaseOrderDTO order = new PurchaseOrderDTO();
//        order.setPlant(plants.get(1));
//        order.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.now(), LocalDate.now()));
//
//        mockMvc.perform(post("/api/sales/orders").content(mapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isCreated());
//    }
//
//    // A recently created PO must have a valid reference to a plant inventory entry,
//    @Test
//    @Sql("/plants-dataset.sql")
//    public void testValidPlantEntry() throws Exception {
//
//        // checking PlantEntry after creating of Puschase Order
//        List<PlantInventoryEntryDTO> plants =  this.findPlants("exc", LocalDate.of(2018,4,14), LocalDate.of(2018,4,25));
//
//        assertThat(plants.size()).isEqualTo(3);
//
//        PurchaseOrderDTO order = new PurchaseOrderDTO();
//        order.setPlant(plants.get(1));
//        order.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.now(), LocalDate.now()));
//
//        MvcResult result  = mockMvc.perform(post("/api/sales/orders").content(mapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isCreated()).andReturn();
//
//        PurchaseOrderDTO po = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<PurchaseOrderDTO>() {});
//
//        assertThat(po.getPlant().get_id()).isNotNull();
//
//
////        // checking with null values
////        PurchaseOrderDTO order1 = new PurchaseOrderDTO();
////        order1.setPlant(PlantInventoryEntryDTO.of(null,null, null, null));
////        order1.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.now(), LocalDate.now()));
////
////        mockMvc.perform(post("/api/sales/orders").content(mapper.writeValueAsString(order1)).contentType(MediaType.APPLICATION_JSON))
////                .andExpect(status().isBadRequest());
//
//
//
//    }
//
//    //a valid rental period (e.g. start < end date, period must be in the future, and both dates must be different from null),
//    @Test
//    @Sql("/plants-dataset.sql")
//    public void testValidBusinessPeriod() throws Exception {
//
//        // Move few lines of code to function so i can resue it.
//        List<PlantInventoryEntryDTO> plants =  this.findPlants("exc", LocalDate.of(2018,4,14), LocalDate.of(2018,4,25));
//
//        assertThat(plants.size()).isEqualTo(3);
//
//        // With 2 month back dates (test to check PO will have only future date)
//        PurchaseOrderDTO order = new PurchaseOrderDTO();
//        order.setPlant(plants.get(1));
//        order.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.now().minusMonths(2), LocalDate.now()));
//
//        mockMvc.perform(post("/api/sales/orders").content(mapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//
//        // With Start date < End date
//        PurchaseOrderDTO order2 = new PurchaseOrderDTO();
//        order2.setPlant(plants.get(2));
//        order2.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.now().plusDays(10), LocalDate.now().plusDays(5)));
//
//        mockMvc.perform(post("/api/sales/orders").content(mapper.writeValueAsString(order2)).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//
//
//        // Both dates must not be Null
//        PurchaseOrderDTO order3 = new PurchaseOrderDTO();
//        order3.setPlant(plants.get(2));
//        order3.setRentalPeriod(null);
//
//        mockMvc.perform(post("/api/sales/orders").content(mapper.writeValueAsString(order3)).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//
//    }
//
//
//    //Once the PO is stored in the database, the PO must always have a valid identifier
//    @Test
//    @Sql("/plants-dataset.sql")
//    public void testValidPOIdentifier() throws Exception {
//
//        // Move few lines of code to function so i can resue it.
//        List<PlantInventoryEntryDTO> plants =  this.findPlants("exc", LocalDate.of(2018,4,14), LocalDate.of(2018,4,25));
//
//        assertThat(plants.size()).isEqualTo(3);
//
//        // Create Purchase Order to validate
//        PurchaseOrderDTO order = new PurchaseOrderDTO();
//        order.setPlant(plants.get(1));
//        order.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.now().plusDays(10), LocalDate.now().plusDays(20)));
//
//        MvcResult result = mockMvc.perform(post("/api/sales/orders").content(mapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isCreated()).andReturn();
//
//        PurchaseOrderDTO po = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<PurchaseOrderDTO>() {});
//
//        assertThat(po.get_id()).isNotNull();
//
//        // getting the same PO via rest to compare them
//        MvcResult result2 = mockMvc.perform(get("/api/sales/orders/"+po.get_id()).content(mapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk()).andReturn();
//
//        PurchaseOrderDTO po2 = mapper.readValue(result2.getResponse().getContentAsString(), new TypeReference<PurchaseOrderDTO>() {});
//
//        assertThat(po).isEqualTo(po2);
//
//
//    }
//
//
//    // An Close[Rejected] PO must have a valid total cost (e.g. positive value)
//
//    @Test
//    @Sql("/plants-dataset.sql")
//    public void testValidPriceClosedPO() throws Exception {
//
//
//        List<PlantInventoryEntryDTO> plants =  this.findPlants("exc", LocalDate.of(2018,4,14), LocalDate.of(2018,4,25));
//
//        assertThat(plants.size()).isEqualTo(3);
//
//
//        //Creation of PO
//        PurchaseOrderDTO order = new PurchaseOrderDTO();
//        order.setPlant(plants.get(1));
//        order.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.now(), LocalDate.now()));
//
//        MvcResult result = mockMvc.perform(post("/api/sales/orders").content(mapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isCreated()).andReturn();
//
//        PurchaseOrderDTO po = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<PurchaseOrderDTO>() {});
//
//
//
//        //Rejecting Created PO
//        result = mockMvc.perform(delete("/api/sales/orders/"+po.get_id()+"/accept"))
//                .andExpect(status().isOk()).andReturn();
//
//        po = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<PurchaseOrderDTO>() {});
//
//
//        //Asserting that the Closed PO has valid total price i.e >0
//        assertThat(po.getTotal().compareTo(BigDecimal.ZERO)>0);
//
//    }
//
//
//    // An OPEN PO must have a valid total cost (e.g. positive value)
//    @Test
//    @Sql("/plants-dataset.sql")
//    public void testValidPriceOpenedPO() throws Exception {
//
//        // Move few lines of code to function so i can resue it.
//        List<PlantInventoryEntryDTO> plants =  this.findPlants("exc", LocalDate.of(2018,4,14), LocalDate.of(2018,4,25));
//
//        assertThat(plants.size()).isEqualTo(3);
//
//
//        //Creation of PO
//        PurchaseOrderDTO order = new PurchaseOrderDTO();
//        order.setPlant(plants.get(1));
//        order.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.now(), LocalDate.now()));
//
//        MvcResult result = mockMvc.perform(post("/api/sales/orders").content(mapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isCreated()).andReturn();
//
//        PurchaseOrderDTO po = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<PurchaseOrderDTO>() {});
//
//
//
//        //Accepting Created PO
//        result = mockMvc.perform(post("/api/sales/orders/"+po.get_id()+"/plants/1/accept"))
//                .andExpect(status().isCreated()).andReturn();
//
//        po = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<PurchaseOrderDTO>() {});
//
//
//
//        //Asserting that the Opened PO has valid total price i.e >0
//        assertThat(po.getTotal().compareTo(BigDecimal.ZERO)>0);
//
//
//    }
//
//
//    // An OPEN PO must have a valid total cost (e.g. positive value)
//    @Test
//    @Sql("/plants-dataset.sql")
//    public void testValidPlantReservation() throws Exception {
//        // Move few lines of code to function so i can resue it.
//        List<PlantInventoryEntryDTO> plants =  this.findPlants("exc", LocalDate.of(2018,4,14), LocalDate.of(2018,4,25));
//        assertThat(plants.size()).isEqualTo(3);
//
//        //Creation of PO
//        PurchaseOrderDTO order = new PurchaseOrderDTO();
//        order.setPlant(plants.get(1));
//        order.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.now(), LocalDate.now()));
//
//        MvcResult result = mockMvc.perform(post("/api/sales/orders").content(mapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isCreated()).andReturn();
//
//        PurchaseOrderDTO po = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<PurchaseOrderDTO>() {});
//
//        //Accepting Created PO
//        result = mockMvc.perform(post("/api/sales/orders/"+po.get_id()+"/plants/1/accept"))
//                .andExpect(status().isCreated()).andReturn();
//
//        po = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<PurchaseOrderDTO>() {});
//
//
//
//
//    }

}



