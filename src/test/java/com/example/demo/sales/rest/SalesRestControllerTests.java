package com.example.demo.sales.rest;

import com.example.demo.DemoApplication;
import com.example.demo.common.application.dto.BusinessPeriodDTO;
import com.example.demo.inventory.application.dto.PlantInventoryEntryDTO;
import com.example.demo.inventory.application.dto.PlantInventoryItemDTO;
import com.example.demo.inventory.application.services.PlantInventoryEntryAssembler;
import com.example.demo.inventory.application.services.PlantInventoryItemAssembler;
import com.example.demo.inventory.domain.model.PlantInventoryEntry;
import com.example.demo.inventory.domain.repository.PlantInventoryEntryRepository;
import com.example.demo.inventory.domain.repository.PlantInventoryItemRepository;
import com.example.demo.sales.application.dto.POExtensionDTO;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import com.example.demo.sales.application.services.POExtensionAssembler;
import com.example.demo.sales.domain.model.PurchaseOrder;
import com.example.demo.sales.domain.repository.PurchaseOrderRepository;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;



import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.example.demo.sales.domain.model.POStatus.*;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;


import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DemoApplication.class)
@WebAppConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)

//@DirtiesContext(classMode=DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
////@Sql(scripts = "/plants-dataset.sql")
//@Sql(
//        scripts = "/delete.sql",
//        executionPhase = AFTER_TEST_METHOD,
//        config = @SqlConfig(transactionMode = ISOLATED)
//)


/**
 * RentIT project requirements tests.
 */
public class SalesRestControllerTests {
    @Autowired
    PlantInventoryEntryRepository repo;

    @Autowired
    PlantInventoryItemRepository itemRepo;

    @Autowired
    PlantInventoryItemAssembler plantInventoryItemAssembler;

    @Autowired
    PlantInventoryEntryAssembler plantInventoryEntryAssembler;

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    POExtensionAssembler poExtensionAssembler;

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
    /**
     * The system should allow a customer to list the available plants and their prices.
     */
    public  void findAllAvailablePlants() throws Exception {

        String name="exc";
        MvcResult result = mockMvc.perform(get("/api/inventory/plants"
        ).with(user("customer").password("customer").roles("CUSTOMER")))
                .andExpect(status().isOk())
                .andExpect(header().string("Location", isEmptyOrNullString()))
                .andReturn();

        Resources<PlantInventoryEntryDTO> plants = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<Resources<PlantInventoryEntryDTO>>() {});

        List<PlantInventoryEntryDTO> entryDTOS = new ArrayList<PlantInventoryEntryDTO>(plants.getContent());
        System.out.println(entryDTOS);
        assert((entryDTOS.size()==14));
        assert(entryDTOS.get(0).getPrice().longValue()==150);
    }

    @Test
    /**
     * The system should allow a customer to check the price for a given plant (given the plant identifier).
     */
    public  void checkPlantPrice() throws Exception {

        String name="exc";
        MvcResult result = mockMvc.perform(get("/api/inventory/plants/1"
        ).with(user("customer").password("customer").roles("CUSTOMER")))
                .andExpect(status().isOk())
                .andExpect(header().string("Location", isEmptyOrNullString()))
                .andReturn();

        Resource<PlantInventoryEntryDTO> plants = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<Resource<PlantInventoryEntryDTO>>() {});

        PlantInventoryEntryDTO entryDTOS = plants.getContent();
        System.out.println(entryDTOS);
        assert(entryDTOS.getPrice().longValue()==150);
    }

    @Test
    /**
     * The system should allow a customer to check the availability of a given plant during a given time period.
     */
    public  void findAvailablePlantsWithTimePeriod() throws Exception {

        String name="exc";
        MvcResult result = mockMvc.perform(get("/api/sales/plants?name="+ name +"&startDate="+ LocalDate.now() +"&endDate="+ LocalDate.now().plusDays(2)
        ).with(user("customer").password("customer").roles("CUSTOMER")))
                .andExpect(status().isOk())
                .andExpect(header().string("Location", isEmptyOrNullString()))
                .andReturn();

        Resources<PlantInventoryEntryDTO> plants = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<Resources<PlantInventoryEntryDTO>>() {});

        List<PlantInventoryEntryDTO> entryDTOS = new ArrayList<PlantInventoryEntryDTO>(plants.getContent());
        System.out.println(entryDTOS);
        assert((entryDTOS.size()==4));
    }

    @Test
    /**
     * The system should allow a customer to submit a PO for hiring a plant. The PO may be
     accepted or rejected depending on the plant’s availability.
     */
    public  void submit_PO_accepted() throws Exception {

        List<PlantInventoryEntry> plants = repo.findAll();

        PurchaseOrderDTO order = new PurchaseOrderDTO();
        order.setPlant(plantInventoryEntryAssembler.toResource(plants.get(0)));
        order.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.now(), LocalDate.now().plusDays(2)));

        MvcResult result1 = mockMvc.perform(post("/api/sales/orders").with(user("customer").password("customer").roles("CUSTOMER"))
                .content(mapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        Resource<PurchaseOrderDTO> orders = mapper.readValue(result1.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});

        PurchaseOrderDTO entryDTOS = orders.getContent();
        long id = entryDTOS.get_id();
        MvcResult result2 = mockMvc.perform(put("/api/sales/orders/" + id + "/allocation").with(user("employee").password("employee").roles("EMPLOYEE"))
        ).andExpect(status().isOk())
                .andExpect(header().string("Location", isEmptyOrNullString()))
                .andReturn();

        Resource<PurchaseOrderDTO> orderAccpet = mapper.readValue(result2.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS = orderAccpet.getContent();
        System.out.println(entry_DTOS.getStatus());
        assert((entry_DTOS.getStatus().equals(OPEN)));
    }

    @Test
    /**
     * The system should allow a customer to submit a PO for hiring a plant. The PO may be
     accepted or rejected depending on the plant’s availability.
     */
    public  void submit_PO_rejected() throws Exception {

        List<PlantInventoryEntry> plants = repo.findAll();

        PurchaseOrderDTO order = new PurchaseOrderDTO();
        order.setPlant(plantInventoryEntryAssembler.toResource(plants.get(0)));
        order.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.now(), LocalDate.now().plusDays(2)));

        MvcResult result1 = mockMvc.perform(post("/api/sales/orders").with(user("customer").password("customer").roles("CUSTOMER"))
                .content(mapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        Resource<PurchaseOrderDTO> orders = mapper.readValue(result1.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});

        PurchaseOrderDTO entryDTOS = orders.getContent();
        long id = entryDTOS.get_id();
        System.out.println(orders + "/api/sales/orders/" + id + "/allocation");
        MvcResult result2 = mockMvc.perform(delete("/api/sales/orders/" + id + "/allocation").with(user("employee").password("employee").roles("EMPLOYEE")))
                .andExpect(status().isOk())
                .andReturn();

        Resource<PurchaseOrderDTO> orderRejected = mapper.readValue(result2.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS = orderRejected.getContent();
        System.out.println(entry_DTOS.getStatus());
        assert((entry_DTOS.getStatus().equals(REJECTED)));
    }


    @Test
    /**
     * The system should allow employees at Rentit to determine which plants need to be delivered on a given date.
     */
    public  void plantsDeliveredOnGivenDate() throws Exception {

        List<PlantInventoryEntry> plants = repo.findAll();

        PurchaseOrderDTO order = new PurchaseOrderDTO();
        order.setPlant(plantInventoryEntryAssembler.toResource(plants.get(0)));
        order.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.now(), LocalDate.now().plusDays(2)));

        MvcResult result1 = mockMvc.perform(post("/api/sales/orders").with(user("customer").password("customer").roles("CUSTOMER"))
                .content(mapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        Resource<PurchaseOrderDTO> orders = mapper.readValue(result1.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});

        PurchaseOrderDTO entryDTOS = orders.getContent();
        long id = entryDTOS.get_id();
        MvcResult result2 = mockMvc.perform(put("/api/sales/orders/" + id + "/allocation").with(user("employee").password("employee").roles("EMPLOYEE"))
        ).andExpect(status().isOk())
                .andExpect(header().string("Location", isEmptyOrNullString()))
                .andReturn();

        Resource<PurchaseOrderDTO> orderAccept = mapper.readValue(result2.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS = orderAccept.getContent();
        System.out.println(entry_DTOS);
        System.out.println(entry_DTOS.getStatus());
        assert((entry_DTOS.getStatus().equals(OPEN)));


        MvcResult result = mockMvc.perform(get("/api/sales/plants_to_dispatch?dispatchDate="+LocalDate.now()
        ).with(user("employee").password("employee").roles("EMPLOYEE")))
                .andExpect(status().isOk())
                .andReturn();


        Resources<PlantInventoryItemDTO> plant = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<Resources<PlantInventoryItemDTO>>() {});

        List<PlantInventoryItemDTO> entryDTOS1 = new ArrayList<PlantInventoryItemDTO>(plant.getContent());
        System.out.println(entryDTOS1.get(0).get_id());
        assert((entryDTOS1.get(0).get_id().equals(entry_DTOS.getPlant().getContent().get_id())));
    }

    @Test
    /**
     * The system should allow a customer to view the status of a PO (PO accepted, PO rejected, plant dispatched,
     plant delivered, plant rejected by customer, plant returned, invoiced).
     */
    public  void viewStatus_of_PO_accepted() throws Exception {

        List<PlantInventoryEntry> plants = repo.findAll();

        PurchaseOrderDTO order = new PurchaseOrderDTO();
        order.setPlant(plantInventoryEntryAssembler.toResource(plants.get(2)));
        order.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.now(), LocalDate.now().plusDays(2)));

        MvcResult POresult = mockMvc.perform(post("/api/sales/orders").with(user("customer").password("customer").roles("CUSTOMER"))
                .content(mapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        Resource<PurchaseOrderDTO> orders = mapper.readValue(POresult.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});

        PurchaseOrderDTO entryDTOS = orders.getContent();
        long id = entryDTOS.get_id();
        System.out.println(orders + "/api/sales/orders/" + id + "/allocation");
        MvcResult result2 = mockMvc.perform(put("/api/sales/orders/" + id + "/allocation").with(user("employee").password("employee").roles("EMPLOYEE")))
                .andExpect(status().isOk())
                .andReturn();

        Resource<PurchaseOrderDTO> orderAccepted = mapper.readValue(result2.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS1 = orderAccepted.getContent();
        System.out.println(entry_DTOS1.getStatus());
        assert((entry_DTOS1.getStatus().equals(OPEN)));

        System.out.println(orders + "/api/sales/orders/" + id);
        MvcResult result3 = mockMvc.perform(get("/api/sales/orders/" + id).with(user("customer").password("customer").roles("CUSTOMER")))
                .andExpect(status().isOk())
                .andReturn();

        Resource<PurchaseOrderDTO> orderAcceptedViewByCustomer = mapper.readValue(result2.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS2 = orderAcceptedViewByCustomer.getContent();
        System.out.println(entry_DTOS2.getStatus());
        assert((entry_DTOS2.getStatus().equals(OPEN)));
    }

    @Test
    /**
     * The system should allow a customer to view the status of a PO (PO accepted, PO rejected, plant dispatched,
     plant delivered, plant rejected by customer, plant returned, invoiced).
     */
    public  void viewStatus_of_PO_rejected() throws Exception {

        List<PlantInventoryEntry> plants = repo.findAll();

        PurchaseOrderDTO order = new PurchaseOrderDTO();
        order.setPlant(plantInventoryEntryAssembler.toResource(plants.get(0)));
        order.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.now(), LocalDate.now().plusDays(2)));

        MvcResult result1 = mockMvc.perform(post("/api/sales/orders").with(user("customer").password("customer").roles("CUSTOMER"))
                .content(mapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        Resource<PurchaseOrderDTO> orders = mapper.readValue(result1.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});

        PurchaseOrderDTO entryDTOS = orders.getContent();
        long id = entryDTOS.get_id();
        System.out.println(orders + "/api/sales/orders/" + id + "/allocation");
        MvcResult result2 = mockMvc.perform(delete("/api/sales/orders/" + id + "/allocation").with(user("employee").password("employee").roles("EMPLOYEE")))
                .andExpect(status().isOk())
                .andReturn();

        Resource<PurchaseOrderDTO> orderrejected = mapper.readValue(result2.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS = orderrejected.getContent();
        System.out.println(entry_DTOS.getStatus());
        assert((entry_DTOS.getStatus().equals(REJECTED)));

        System.out.println(orders + "/api/sales/orders/" + id);
        MvcResult result3 = mockMvc.perform(get("/api/sales/orders/" + id).with(user("customer").password("customer").roles("CUSTOMER")))
                .andExpect(status().isOk())
                .andReturn();

        Resource<PurchaseOrderDTO> orderRejectedViewByCustomer = mapper.readValue(result2.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS1 = orderRejectedViewByCustomer.getContent();
        System.out.println(entry_DTOS1.getStatus());
        assert((entry_DTOS1.getStatus().equals(REJECTED)));
    }

    @Test
    /**
     * The system should allow a customer to view the status of a PO (PO accepted, PO rejected, plant dispatched,
     plant delivered, plant rejected by customer, plant returned, invoiced).
     */
    public  void viewStatus_of_PO_dispatched() throws Exception {

        List<PlantInventoryEntry> plants = repo.findAll();

        PurchaseOrderDTO order = new PurchaseOrderDTO();
        order.setPlant(plantInventoryEntryAssembler.toResource(plants.get(0)));
        order.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.now(), LocalDate.now().plusDays(2)));

        MvcResult result1 = mockMvc.perform(post("/api/sales/orders").with(user("customer").password("customer").roles("CUSTOMER"))
                .content(mapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        Resource<PurchaseOrderDTO> orders = mapper.readValue(result1.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});

        PurchaseOrderDTO entryDTOS = orders.getContent();
        long id = entryDTOS.get_id();
        System.out.println(orders + "/api/sales/orders/" + id + "/allocation");
        MvcResult result2 = mockMvc.perform(put("/api/sales/orders/" + id + "/allocation").with(user("employee").password("employee").roles("EMPLOYEE")))
                .andExpect(status().isOk())
                .andReturn();

        Resource<PurchaseOrderDTO> orderAccepted = mapper.readValue(result2.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS = orderAccepted.getContent();
        System.out.println(entry_DTOS.getStatus());
        assert((entry_DTOS.getStatus().equals(OPEN)));

        MvcResult result3 = mockMvc.perform(get("/api/sales/orders/" + id + "/dispatched").with(user("employee").password("employee").roles("EMPLOYEE"))
        ).andExpect(status().isOk())
                .andExpect(header().string("Location", isEmptyOrNullString()))
                .andReturn();

        Resource<PurchaseOrderDTO> orderdispatched = mapper.readValue(result3.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS1 = orderdispatched.getContent();
        System.out.println(entry_DTOS1.getStatus());
        assert((entry_DTOS1.getStatus().equals(DISPATCHED)));

        MvcResult orderResult = mockMvc.perform(get("/api/sales/orders/" + id).with(user("customer").password("customer").roles("CUSTOMER"))
        ).andExpect(status().isOk())
                .andExpect(header().string("Location", isEmptyOrNullString()))
                .andReturn();

        Resource<PurchaseOrderDTO> viewOrderdStatus = mapper.readValue(orderResult.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS2 = viewOrderdStatus.getContent();
        System.out.println(entry_DTOS2.getStatus());
        assert((entry_DTOS2.getStatus().equals(DISPATCHED)));
    }

    @Test
    /**
     * The system should allow a customer to view the status of a PO (PO accepted, PO rejected, plant dispatched,
     plant delivered, plant rejected by customer, plant returned, invoiced).
     */
    public  void viewStatus_of_PO_delivered() throws Exception {

        List<PlantInventoryEntry> plants = repo.findAll();

        PurchaseOrderDTO order = new PurchaseOrderDTO();
        order.setPlant(plantInventoryEntryAssembler.toResource(plants.get(0)));
        order.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.now(), LocalDate.now().plusDays(2)));

        MvcResult result1 = mockMvc.perform(post("/api/sales/orders").with(user("customer").password("customer").roles("CUSTOMER"))
                .content(mapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        Resource<PurchaseOrderDTO> orders = mapper.readValue(result1.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});

        PurchaseOrderDTO entryDTOS = orders.getContent();
        long id = entryDTOS.get_id();
        System.out.println(orders + "/api/sales/orders/" + id + "/allocation");
        MvcResult result2 = mockMvc.perform(put("/api/sales/orders/" + id + "/allocation").with(user("employee").password("employee").roles("EMPLOYEE")))
                .andExpect(status().isOk())
                .andReturn();

        Resource<PurchaseOrderDTO> orderAccepted = mapper.readValue(result2.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS = orderAccepted.getContent();
        System.out.println(entry_DTOS.getStatus());
        assert((entry_DTOS.getStatus().equals(OPEN)));

        MvcResult result3 = mockMvc.perform(get("/api/sales/orders/" + id + "/dispatched").with(user("employee").password("employee").roles("EMPLOYEE"))
        ).andExpect(status().isOk())
                .andExpect(header().string("Location", isEmptyOrNullString()))
                .andReturn();

        Resource<PurchaseOrderDTO> orderdispatched = mapper.readValue(result3.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS1 = orderdispatched.getContent();
        System.out.println(entry_DTOS1.getStatus());
        assert((entry_DTOS1.getStatus().equals(DISPATCHED)));

        MvcResult deliveredResult = mockMvc.perform(get("/api/sales/orders/" + id + "/delivered").with(user("employee").password("employee").roles("EMPLOYEE"))
        ).andExpect(status().isOk())
                .andExpect(header().string("Location", isEmptyOrNullString()))
                .andReturn();

        Resource<PurchaseOrderDTO> orderDeliveredResult = mapper.readValue(deliveredResult.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS2 = orderDeliveredResult.getContent();
        System.out.println(entry_DTOS2.getStatus());
        assert((entry_DTOS2.getStatus().equals(DELIVERED)));

        MvcResult orderResult = mockMvc.perform(get("/api/sales/orders/" + id).with(user("customer").password("customer").roles("CUSTOMER"))
        ).andExpect(status().isOk())
                .andExpect(header().string("Location", isEmptyOrNullString()))
                .andReturn();

        Resource<PurchaseOrderDTO> viewOrderdStatus = mapper.readValue(orderResult.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS3 = viewOrderdStatus.getContent();
        System.out.println(entry_DTOS3.getStatus());
        assert((entry_DTOS3.getStatus().equals(DELIVERED)));
    }

    @Test
    /**
     * The system should allow a customer to view the status of a PO (PO accepted, PO rejected, plant dispatched,
     plant delivered, plant rejected by customer, plant returned, invoiced).
     */
    public  void viewStatus_of_PO_rejected_by_customer() throws Exception {

        List<PlantInventoryEntry> plants = repo.findAll();

        PurchaseOrderDTO order = new PurchaseOrderDTO();
        order.setPlant(plantInventoryEntryAssembler.toResource(plants.get(0)));
        order.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.now(), LocalDate.now().plusDays(2)));

        MvcResult result1 = mockMvc.perform(post("/api/sales/orders").with(user("customer").password("customer").roles("CUSTOMER"))
                .content(mapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        Resource<PurchaseOrderDTO> orders = mapper.readValue(result1.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});

        PurchaseOrderDTO entryDTOS = orders.getContent();
        long id = entryDTOS.get_id();
        System.out.println(orders + "/api/sales/orders/" + id + "/allocation");
        MvcResult result2 = mockMvc.perform(put("/api/sales/orders/" + id + "/allocation").with(user("employee").password("employee").roles("EMPLOYEE")))
                .andExpect(status().isOk())
                .andReturn();

        Resource<PurchaseOrderDTO> orderAccepted = mapper.readValue(result2.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS = orderAccepted.getContent();
        System.out.println(entry_DTOS.getStatus());
        assert((entry_DTOS.getStatus().equals(OPEN)));

        MvcResult result3 = mockMvc.perform(get("/api/sales/orders/" + id + "/dispatched").with(user("employee").password("employee").roles("EMPLOYEE"))
        ).andExpect(status().isOk())
                .andExpect(header().string("Location", isEmptyOrNullString()))
                .andReturn();

        Resource<PurchaseOrderDTO> orderdispatched = mapper.readValue(result3.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS1 = orderdispatched.getContent();
        System.out.println(entry_DTOS1.getStatus());
        assert((entry_DTOS1.getStatus().equals(DISPATCHED)));

        MvcResult rejectedBycustomerResult = mockMvc.perform(get("/api/sales/orders/" + id + "/rejected_by_customer").with(user("employee").password("employee").roles("EMPLOYEE"))
        ).andExpect(status().isOk())
                .andExpect(header().string("Location", isEmptyOrNullString()))
                .andReturn();

        Resource<PurchaseOrderDTO> orderRejectedBycustomer = mapper.readValue(rejectedBycustomerResult.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS2 = orderRejectedBycustomer.getContent();
        System.out.println(entry_DTOS2.getStatus());
        assert((entry_DTOS2.getStatus().equals(REJECTED_BY_CUSTOMER)));

        MvcResult orderResult = mockMvc.perform(get("/api/sales/orders/" + id).with(user("customer").password("customer").roles("CUSTOMER"))
        ).andExpect(status().isOk())
                .andExpect(header().string("Location", isEmptyOrNullString()))
                .andReturn();

        Resource<PurchaseOrderDTO> viewOrderdStatus = mapper.readValue(orderResult.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS3 = viewOrderdStatus.getContent();
        System.out.println(entry_DTOS3.getStatus());
        assert((entry_DTOS3.getStatus().equals(REJECTED_BY_CUSTOMER)));
    }

    @Test
    /**
     * The system should allow a customer to submit an extension request for a given PO. If the plant
     is available for the requested extension period, the system should accept the request. If the plant
     is not available for the requested period, but the system finds a replacement such that the loss
     does not exceed 30% of the income, the system should accept the request and adjust the PO
     accordingly. If the plant is not available or the loss implied by a replacement exceeds 30% of
     the income, the system should reject the extension. If the extension is rejected but a replacement
     exists, the system should propose the customer the replacement.
     */
    public  void submitExtension() throws Exception {

        List<PlantInventoryEntry> plants = repo.findAll();

        PurchaseOrderDTO order = new PurchaseOrderDTO();
        order.setPlant(plantInventoryEntryAssembler.toResource(plants.get(0)));
        order.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.now(), LocalDate.now().plusDays(2)));

        MvcResult result1 = mockMvc.perform(post("/api/sales/orders").with(user("customer").password("customer").roles("CUSTOMER"))
                .content(mapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        Resource<PurchaseOrderDTO> orders = mapper.readValue(result1.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});

        PurchaseOrderDTO entryDTOS = orders.getContent();
        long id = entryDTOS.get_id();
        System.out.println(orders + "/api/sales/orders/" + id + "/allocation");
        MvcResult result2 = mockMvc.perform(put("/api/sales/orders/" + id + "/allocation").with(user("employee").password("employee").roles("EMPLOYEE")))
                .andExpect(status().isOk())
                .andReturn();

        Resource<PurchaseOrderDTO> orderAccepted = mapper.readValue(result2.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS = orderAccepted.getContent();
        System.out.println(entry_DTOS.getStatus());
        assert((entry_DTOS.getStatus().equals(OPEN)));

        POExtensionDTO poExtensionDTO = new POExtensionDTO();
        poExtensionDTO.setEndDate(LocalDate.now().plusDays(10));


        MvcResult extensions = mockMvc.perform(post("/api/sales/orders/" + id + "/extensions").with(user("customer").password("customer").roles("CUSTOMER"))
                .content(mapper.writeValueAsString(poExtensionDTO)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        Resource<PurchaseOrderDTO> orderExtensions = mapper.readValue(extensions.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS1 = orderExtensions.getContent();
        System.out.println(orderExtensions);
        assert((entry_DTOS1.getStatus().equals(PENDING_EXTENSION)));

        PurchaseOrder purchaseOrder = purchaseOrderRepository.getOne(orderExtensions.getContent().get_id());
        System.out.println(purchaseOrder);

        Resource<PlantInventoryItemDTO> plantInventoryItemDTO = plantInventoryItemAssembler.toResource(purchaseOrder.getReservations().get(0).getPlant());

        System.out.println(plantInventoryItemDTO);

        MvcResult extensionAccepted = mockMvc.perform(patch("/api/sales/orders/" + id + "/extensions").with(user("employee").password("employee").roles("EMPLOYEE"))
                .content(mapper.writeValueAsString(plantInventoryItemDTO)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Resource<PurchaseOrderDTO> orderextensionResult = mapper.readValue(extensionAccepted.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO orderExtensionResultParsed = orderextensionResult.getContent();
        System.out.println(orderExtensionResultParsed);

        assert((orderExtensionResultParsed.getStatus().equals(OPEN)));


    }

    @Test
    /**
     * The system should allow a customer to view the status of a PO (PO accepted, PO rejected, plant dispatched,
     plant delivered, plant rejected by customer, plant returned, invoiced).
     */
    public  void viewStatus_of_PO_returned() throws Exception {

        List<PlantInventoryEntry> plants = repo.findAll();

        PurchaseOrderDTO order = new PurchaseOrderDTO();
        order.setPlant(plantInventoryEntryAssembler.toResource(plants.get(0)));
        order.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.now(), LocalDate.now().plusDays(2)));

        MvcResult result1 = mockMvc.perform(post("/api/sales/orders").with(user("customer").password("customer").roles("CUSTOMER"))
                .content(mapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        Resource<PurchaseOrderDTO> orders = mapper.readValue(result1.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});

        PurchaseOrderDTO entryDTOS = orders.getContent();
        long id = entryDTOS.get_id();
        System.out.println(orders + "/api/sales/orders/" + id + "/allocation");
        MvcResult result2 = mockMvc.perform(put("/api/sales/orders/" + id + "/allocation").with(user("employee").password("employee").roles("EMPLOYEE")))
                .andExpect(status().isOk())
                .andReturn();

        Resource<PurchaseOrderDTO> orderAccepted = mapper.readValue(result2.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS = orderAccepted.getContent();
        System.out.println(entry_DTOS.getStatus());
        assert((entry_DTOS.getStatus().equals(OPEN)));

        MvcResult result3 = mockMvc.perform(get("/api/sales/orders/" + id + "/dispatched").with(user("employee").password("employee").roles("EMPLOYEE"))
        ).andExpect(status().isOk())
                .andExpect(header().string("Location", isEmptyOrNullString()))
                .andReturn();

        Resource<PurchaseOrderDTO> orderdispatched = mapper.readValue(result3.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS1 = orderdispatched.getContent();
        System.out.println(entry_DTOS1.getStatus());
        assert((entry_DTOS1.getStatus().equals(DISPATCHED)));

        MvcResult orderDelivered = mockMvc.perform(get("/api/sales/orders/" + id + "/delivered").with(user("employee").password("employee").roles("EMPLOYEE"))
        ).andExpect(status().isOk())
                .andExpect(header().string("Location", isEmptyOrNullString()))
                .andReturn();

        Resource<PurchaseOrderDTO> orderDeliveredResult = mapper.readValue(orderDelivered.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS2 = orderDeliveredResult.getContent();
        System.out.println(entry_DTOS2.getStatus());
        assert((entry_DTOS2.getStatus().equals(DELIVERED)));

        MvcResult orderReturned = mockMvc.perform(get("/api/sales/orders/" + id + "/returned").with(user("employee").password("employee").roles("EMPLOYEE"))
        ).andExpect(status().isOk())
                .andExpect(header().string("Location", isEmptyOrNullString()))
                .andReturn();

        Resource<PurchaseOrderDTO> orderReturnedResult = mapper.readValue(orderReturned.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS3 = orderReturnedResult.getContent();
        System.out.println(entry_DTOS3.getStatus());
        assert((entry_DTOS3.getStatus().equals(RETURNED)));

        MvcResult orderStatusViewByCustomer = mockMvc.perform(get("/api/sales/orders/" + id).with(user("customer").password("customer").roles("CUSTOMER"))
        ).andExpect(status().isOk())
                .andExpect(header().string("Location", isEmptyOrNullString()))
                .andReturn();

        Resource<PurchaseOrderDTO> orderStatusViewByCustomerResult = mapper.readValue(orderStatusViewByCustomer.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS4 = orderStatusViewByCustomerResult.getContent();
        System.out.println(entry_DTOS4.getStatus());
        assert((entry_DTOS4.getStatus().equals(RETURNED)));
    }

    @Test
    /**
     * The system should allow a customer to submit a request to cancel a PO. A cancellation request
     is normally accepted if the request arrives prior to the plant being dispatched. If the plant has
     already been dispatched, the cancellation request is rejected.
     */
    public  void submitRequest_to_Cancel_before_dispatched() throws Exception {

        List<PlantInventoryEntry> plants = repo.findAll();

        PurchaseOrderDTO order = new PurchaseOrderDTO();
        order.setPlant(plantInventoryEntryAssembler.toResource(plants.get(1)));
        order.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.now(), LocalDate.now().plusDays(2)));

        MvcResult result1 = mockMvc.perform(post("/api/sales/orders").with(user("customer").password("customer").roles("CUSTOMER"))
                .content(mapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        Resource<PurchaseOrderDTO> orders = mapper.readValue(result1.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});

        PurchaseOrderDTO entryDTOS = orders.getContent();
        long id = entryDTOS.get_id();
        MvcResult result2 = mockMvc.perform(put("/api/sales/orders/" + id + "/allocation").with(user("employee").password("employee").roles("EMPLOYEE"))
        ).andExpect(status().isOk())
                .andExpect(header().string("Location", isEmptyOrNullString()))
                .andReturn();

        Resource<PurchaseOrderDTO> orderAccept = mapper.readValue(result2.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS = orderAccept.getContent();
        System.out.println(entry_DTOS.getStatus());
        assert((entry_DTOS.getStatus().equals(OPEN)));

        MvcResult result3 = mockMvc.perform(get("/api/sales/orders/" + id + "/cancel").with(user("customer").password("customer").roles("CUSTOMER"))
        ).andExpect(status().isOk())
                .andExpect(header().string("Location", isEmptyOrNullString()))
                .andReturn();

        String orderCancel = result3.getResponse().getContentAsString();
        System.out.println(orderCancel);
        assert((orderCancel.equals("{\"response\": \"PO cancelled.\"}")));
    }

    @Test
    /**
     * The system should allow a customer to submit a request to cancel a PO. A cancellation request
     is normally accepted if the request arrives prior to the plant being dispatched. If the plant has
     already been dispatched, the cancellation request is rejected.
     */
    public  void submitRequest_to_Cancel_after_dispatched() throws Exception {

        List<PlantInventoryEntry> plants = repo.findAll();

        PurchaseOrderDTO order = new PurchaseOrderDTO();
        order.setPlant(plantInventoryEntryAssembler.toResource(plants.get(0)));
        order.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.now(), LocalDate.now().plusDays(2)));

        MvcResult result1 = mockMvc.perform(post("/api/sales/orders").with(user("customer").password("customer").roles("CUSTOMER"))
                .content(mapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        Resource<PurchaseOrderDTO> orders = mapper.readValue(result1.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});

        PurchaseOrderDTO entryDTOS = orders.getContent();
        long id = entryDTOS.get_id();
        MvcResult result2 = mockMvc.perform(put("/api/sales/orders/" + id + "/allocation").with(user("employee").password("employee").roles("EMPLOYEE"))
        ).andExpect(status().isOk())
                .andExpect(header().string("Location", isEmptyOrNullString()))
                .andReturn();

        Resource<PurchaseOrderDTO> orderAccpet = mapper.readValue(result2.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS = orderAccpet.getContent();
        System.out.println(entry_DTOS.getStatus());
        assert((entry_DTOS.getStatus().equals(OPEN)));

        MvcResult result3 = mockMvc.perform(get("/api/sales/orders/" + id + "/dispatched").with(user("employee").password("employee").roles("EMPLOYEE"))
        ).andExpect(status().isOk())
                .andExpect(header().string("Location", isEmptyOrNullString()))
                .andReturn();

        Resource<PurchaseOrderDTO> orderdispatched = mapper.readValue(result3.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        System.out.println(orderdispatched);


        MvcResult cancelResult = mockMvc.perform(get("/api/sales/orders/" + id + "/cancel").with(user("customer").password("customer").roles("CUSTOMER"))
        ).andExpect(status().isOk())
                .andExpect(header().string("Location", isEmptyOrNullString()))
                .andReturn();

        String orderCancel = cancelResult.getResponse().getContentAsString();
        System.out.println(orderCancel);
        assert((orderCancel.equals("{\"response\": \"Given PO cannot be cancelled now.\"}")));
    }

    @Test
    /**
     * The system should allow employees at the plant depot to mark the plant as “dispatched”. This
     happens when the plant leaves the depot.
     */
    public  void markPlant_as_dispatched_at_depot() throws Exception {

        List<PlantInventoryEntry> plants = repo.findAll();

        PurchaseOrderDTO order = new PurchaseOrderDTO();
        order.setPlant(plantInventoryEntryAssembler.toResource(plants.get(1)));
        order.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.now(), LocalDate.now().plusDays(2)));

        MvcResult result1 = mockMvc.perform(post("/api/sales/orders").with(user("customer").password("customer").roles("CUSTOMER"))
                .content(mapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        Resource<PurchaseOrderDTO> orders = mapper.readValue(result1.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});

        PurchaseOrderDTO entryDTOS = orders.getContent();
        long id = entryDTOS.get_id();
        MvcResult result2 = mockMvc.perform(put("/api/sales/orders/" + id + "/allocation").with(user("employee").password("employee").roles("EMPLOYEE"))
        ).andExpect(status().isOk())
                .andExpect(header().string("Location", isEmptyOrNullString()))
                .andReturn();

        Resource<PurchaseOrderDTO> orderAccpet = mapper.readValue(result2.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS = orderAccpet.getContent();
        System.out.println(entry_DTOS.getStatus());
        assert((entry_DTOS.getStatus().equals(OPEN)));

        MvcResult result3 = mockMvc.perform(get("/api/sales/orders/" + id + "/dispatched").with(user("employee").password("employee").roles("EMPLOYEE"))
        ).andExpect(status().isOk())
                .andExpect(header().string("Location", isEmptyOrNullString()))
                .andReturn();

        Resource<PurchaseOrderDTO> orderdispatched = mapper.readValue(result3.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS1 = orderdispatched.getContent();
        System.out.println(entry_DTOS1.getStatus());
        assert((entry_DTOS1.getStatus().equals(DISPATCHED)));
    }

    @Test
    /**
     * The system should allow employees at the plant depot to mark the plant as “delivered” or
     “rejected by customer”. This latter option happens if the customer did not accept the plant
     because the plant did not meet the specifications in the catalogue.
     */
    public  void markPlant_as_delivered_at_depot() throws Exception {

        List<PlantInventoryEntry> plants = repo.findAll();

        PurchaseOrderDTO order = new PurchaseOrderDTO();
        order.setPlant(plantInventoryEntryAssembler.toResource(plants.get(1)));
        order.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.now(), LocalDate.now().plusDays(2)));

        MvcResult result1 = mockMvc.perform(post("/api/sales/orders").with(user("customer").password("customer").roles("CUSTOMER"))
                .content(mapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        Resource<PurchaseOrderDTO> orders = mapper.readValue(result1.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});

        PurchaseOrderDTO entryDTOS = orders.getContent();
        long id = entryDTOS.get_id();
        MvcResult result2 = mockMvc.perform(put("/api/sales/orders/" + id + "/allocation").with(user("employee").password("employee").roles("EMPLOYEE"))
        ).andExpect(status().isOk())
                .andExpect(header().string("Location", isEmptyOrNullString()))
                .andReturn();

        Resource<PurchaseOrderDTO> orderAccpet = mapper.readValue(result2.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS = orderAccpet.getContent();
        System.out.println(entry_DTOS.getStatus());
        assert((entry_DTOS.getStatus().equals(OPEN)));

        MvcResult result3 = mockMvc.perform(get("/api/sales/orders/" + id + "/dispatched").with(user("employee").password("employee").roles("EMPLOYEE"))
        ).andExpect(status().isOk())
                .andExpect(header().string("Location", isEmptyOrNullString()))
                .andReturn();

        Resource<PurchaseOrderDTO> orderdispatched = mapper.readValue(result3.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS1 = orderdispatched.getContent();
        System.out.println(entry_DTOS1.getStatus());
        assert((entry_DTOS1.getStatus().equals(DISPATCHED)));

        MvcResult deliveredResult = mockMvc.perform(get("/api/sales/orders/" + id + "/delivered").with(user("employee").password("employee").roles("EMPLOYEE"))
        ).andExpect(status().isOk())
                .andExpect(header().string("Location", isEmptyOrNullString()))
                .andReturn();

        Resource<PurchaseOrderDTO> orderDeliveredResult = mapper.readValue(deliveredResult.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS2 = orderDeliveredResult.getContent();
        System.out.println(entry_DTOS2.getStatus());
        assert((entry_DTOS2.getStatus().equals(DELIVERED)));
    }

    @Test
    /**
     * The system should allow employees at the plant depot to mark the plant as “delivered” or
     “rejected by customer”. This latter option happens if the customer did not accept the plant
     because the plant did not meet the specifications in the catalogue.
     */
    public  void markPlant_as_rejected_by_customer() throws Exception {

        List<PlantInventoryEntry> plants = repo.findAll();

        PurchaseOrderDTO order = new PurchaseOrderDTO();
        order.setPlant(plantInventoryEntryAssembler.toResource(plants.get(0)));
        order.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.now(), LocalDate.now().plusDays(2)));

        MvcResult result1 = mockMvc.perform(post("/api/sales/orders").with(user("customer").password("customer").roles("CUSTOMER"))
                .content(mapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        Resource<PurchaseOrderDTO> orders = mapper.readValue(result1.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});

        PurchaseOrderDTO entryDTOS = orders.getContent();
        long id = entryDTOS.get_id();
        MvcResult result2 = mockMvc.perform(put("/api/sales/orders/" + id + "/allocation").with(user("employee").password("employee").roles("EMPLOYEE"))
        ).andExpect(status().isOk())
                .andExpect(header().string("Location", isEmptyOrNullString()))
                .andReturn();

        Resource<PurchaseOrderDTO> orderAccpet = mapper.readValue(result2.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS = orderAccpet.getContent();
        System.out.println(entry_DTOS.getStatus());
        assert((entry_DTOS.getStatus().equals(OPEN)));

        MvcResult result3 = mockMvc.perform(get("/api/sales/orders/" + id + "/dispatched").with(user("employee").password("employee").roles("EMPLOYEE"))
        ).andExpect(status().isOk())
                .andExpect(header().string("Location", isEmptyOrNullString()))
                .andReturn();

        Resource<PurchaseOrderDTO> orderdispatched = mapper.readValue(result3.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS1 = orderdispatched.getContent();
        System.out.println(entry_DTOS1.getStatus());
        assert((entry_DTOS1.getStatus().equals(DISPATCHED)));

        MvcResult rejectedBycustomerResult = mockMvc.perform(get("/api/sales/orders/" + id + "/rejected_by_customer").with(user("employee").password("employee").roles("EMPLOYEE"))
        ).andExpect(status().isOk())
                .andExpect(header().string("Location", isEmptyOrNullString()))
                .andReturn();

        Resource<PurchaseOrderDTO> orderRejectedBycustomer = mapper.readValue(rejectedBycustomerResult.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS2 = orderRejectedBycustomer.getContent();
        System.out.println(entry_DTOS2.getStatus());
        assert((entry_DTOS2.getStatus().equals(REJECTED_BY_CUSTOMER)));
    }

    @Test
    /**
     * The system should allow employees at the plant depot to mark a plant as “returned”, meaning
     that the plant has been returned in due form and the rental period has expired.
     */
    public  void markPlant_as_returned() throws Exception {

        List<PlantInventoryEntry> plants = repo.findAll();

        PurchaseOrderDTO order = new PurchaseOrderDTO();
        order.setPlant(plantInventoryEntryAssembler.toResource(plants.get(1)));
        order.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.now(), LocalDate.now().plusDays(2)));

        MvcResult result1 = mockMvc.perform(post("/api/sales/orders").with(user("customer").password("customer").roles("CUSTOMER"))
                .content(mapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        Resource<PurchaseOrderDTO> orders = mapper.readValue(result1.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});

        PurchaseOrderDTO entryDTOS = orders.getContent();
        long id = entryDTOS.get_id();
        MvcResult result2 = mockMvc.perform(put("/api/sales/orders/" + id + "/allocation").with(user("employee").password("employee").roles("EMPLOYEE"))
        ).andExpect(status().isOk())
                .andExpect(header().string("Location", isEmptyOrNullString()))
                .andReturn();

        Resource<PurchaseOrderDTO> orderAccpet = mapper.readValue(result2.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS = orderAccpet.getContent();
        System.out.println(entry_DTOS.getStatus());
        assert((entry_DTOS.getStatus().equals(OPEN)));

        MvcResult result3 = mockMvc.perform(get("/api/sales/orders/" + id + "/dispatched").with(user("employee").password("employee").roles("EMPLOYEE"))
        ).andExpect(status().isOk())
                .andExpect(header().string("Location", isEmptyOrNullString()))
                .andReturn();

        Resource<PurchaseOrderDTO> orderdispatched = mapper.readValue(result3.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS1 = orderdispatched.getContent();
        System.out.println(entry_DTOS1.getStatus());
        assert((entry_DTOS1.getStatus().equals(DISPATCHED)));

        MvcResult deliveredResult = mockMvc.perform(get("/api/sales/orders/" + id + "/delivered").with(user("employee").password("employee").roles("EMPLOYEE"))
        ).andExpect(status().isOk())
                .andExpect(header().string("Location", isEmptyOrNullString()))
                .andReturn();

        Resource<PurchaseOrderDTO> orderDeliveredResult = mapper.readValue(deliveredResult.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS2 = orderDeliveredResult.getContent();
        System.out.println(entry_DTOS2.getStatus());
        assert((entry_DTOS2.getStatus().equals(DELIVERED)));

        MvcResult returnResult = mockMvc.perform(get("/api/sales/orders/" + id + "/returned").with(user("employee").password("employee").roles("EMPLOYEE"))
        ).andExpect(status().isOk())
                .andExpect(header().string("Location", isEmptyOrNullString()))
                .andReturn();

        Resource<PurchaseOrderDTO> orderReturn = mapper.readValue(returnResult.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});
        PurchaseOrderDTO entry_DTOS3 = orderReturn.getContent();
        System.out.println(entry_DTOS3.getStatus());
        assert((entry_DTOS3.getStatus().equals(RETURNED)));
    }

    @Test
    public void creation_of_purchaseOrder() throws Exception {

        List<PlantInventoryEntry> plants = repo.findAll();

        PurchaseOrderDTO order = new PurchaseOrderDTO();
        order.setPlant(plantInventoryEntryAssembler.toResource(plants.get(0)));
        order.setRentalPeriod(BusinessPeriodDTO.of(LocalDate.now(), LocalDate.now().plusDays(2)));

        MvcResult result = mockMvc.perform(post("/api/sales/orders").with(user("customer").password("customer").roles("CUSTOMER"))
                .content(mapper.writeValueAsString(order)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        Resource<PurchaseOrderDTO> orders = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<Resource<PurchaseOrderDTO>>() {});

        PurchaseOrderDTO entryDTOS = orders.getContent();
        System.out.println(entryDTOS);
    }


    // ------------------------------------ Tests from Last Homework Commented out -----------------------------------------

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
    // An OPEN PO must have a valid total cost (e.g. positive value)
//    @Test
////    @Sql("/plants-dataset.sql")
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
//    }

}



