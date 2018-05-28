package com.example.demo.invoicing.application.services;

import com.example.demo.inventory.domain.repository.PlantInventoryEntryRepository;
import com.example.demo.invoicing.application.dto.InvoiceDTO;
import com.example.demo.invoicing.application.integrations.flows.InvoicingFlow;
import com.example.demo.invoicing.application.integrations.gateways.InvoicingGateway;
import com.example.demo.invoicing.domain.model.Invoice;
import com.example.demo.invoicing.domain.model.InvoiceStatus;
import com.example.demo.invoicing.domain.repository.InvoiceRepository;
import com.example.demo.invoicing.infrastructure.InvoiceIdentifierFactory;
import com.example.demo.mailing.USER;
import com.example.demo.mailing.domain.repository.CustomerRepository;
import com.example.demo.sales.application.dto.PurchaseOrderDTO;
import com.example.demo.sales.domain.model.PurchaseOrder;
import com.example.demo.sales.domain.repository.PurchaseOrderRepository;
import com.example.demo.sales.rest.controllers.SalesRestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import javax.sql.DataSource;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;


@Service
public class InvoiceService {



    @Autowired
    InvoiceRepository invoiceRepository;

    @Autowired
    PlantInventoryEntryRepository plantRepo;

    @Autowired
    InvoiceAssembler invoiceAssembler;

    @Autowired
    InvoicingGateway invoicingGateway;

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    @Qualifier("_halObjectMapper")
    ObjectMapper mapper;



    @Value("${gmail.from}")
    String emailFrom;

//    @Value("${gmail.to}")
//    String emailTo;

    InvoiceIdentifierFactory invoiceIdentifierFactory = new InvoiceIdentifierFactory();


    @Autowired
    @Qualifier("dataSource")
    DataSource dataSource;

    JdbcTemplate jt;

    public void executesDynamicQueries() {



        jt = new JdbcTemplate(dataSource);
        jt.execute("insert into plant_inventory_entry (id, name, description, price) values (1, 'Mini excavator', '1.5 Tonne Mini excavator', 150);" +
                "insert into plant_inventory_entry (id, name, description, price) values (2, 'Mini excavator', '3 Tonne Mini excavator', 200);" +
                "insert into plant_inventory_entry (id, name, description, price) values (3, 'Midi excavator', '5 Tonne Midi excavator', 250);" +
                "insert into plant_inventory_entry (id, name, description, price) values (4, 'Midi excavator', '8 Tonne Midi excavator', 300);" +
                "insert into plant_inventory_entry (id, name, description, price) values (5, 'Maxi excavator', '15 Tonne Large excavator', 400);" +
                "insert into plant_inventory_entry (id, name, description, price) values (6, 'Maxi excavator', '20 Tonne Large excavator', 450);" +
                "insert into plant_inventory_entry (id, name, description, price) values (7, 'HS dumper', '1.5 Tonne Hi-Swivel Dumper', 150);" +
                "insert into plant_inventory_entry (id, name, description, price) values (8, 'FT dumper', '2 Tonne Front Tip Dumper', 180);" +
                "insert into plant_inventory_entry (id, name, description, price) values (9, 'FT dumper', '2 Tonne Front Tip Dumper', 200);" +
                "insert into plant_inventory_entry (id, name, description, price) values (10, 'FT dumper', '2 Tonne Front Tip Dumper', 300);" +
                "insert into plant_inventory_entry (id, name, description, price) values (11, 'FT dumper', '3 Tonne Front Tip Dumper', 400);" +
                "insert into plant_inventory_entry (id, name, description, price) values (12, 'Loader', 'Hewden Backhoe Loader', 200);" +
                "insert into plant_inventory_entry (id, name, description, price) values (13, 'D-Truck', '15 Tonne Articulating Dump Truck', 250);" +
                "insert into plant_inventory_entry (id, name, description, price) values (14, 'D-Truck', '30 Tonne Articulating Dump Truck', 300);" +
                "insert into plant_inventory_item (id, plant_info_id, serial_number, equipment_condition) values (1, 1, 'A01', 'SERVICEABLE');" +
                "insert into plant_inventory_item (id, plant_info_id, serial_number, equipment_condition) values (2, 2, 'A02', 'SERVICEABLE');"+
                "insert into plant_inventory_item (id, plant_info_id, serial_number, equipment_condition) values (3, 1, 'A02', 'SERVICEABLE');" +
                "insert into plant_inventory_item (id, plant_info_id, serial_number, equipment_condition) values (4, 3, 'A03', 'UNSERVICEABLE_REPAIRABLE');" +
                "create table if not exists users (username varchar(50) not null primary key, password varchar(50) not null, enabled boolean not null);" +
                "create table if not exists authorities (username varchar(50) not null, authority varchar(50) not null, constraint FK_AUTHORITIES_USERS foreign key(username) references users(username));" +
                "insert into users (username, password, enabled) values ('admin', 'admin', true);" +
                "insert into authorities (username, authority) values ('admin', 'ROLE_ADMIN');" +
                "insert into users (username, password, enabled) values ('employee', 'employee', true);" +
                "insert into authorities (username, authority) values ('employee', 'ROLE_EMPLOYEE');" +
                "insert into users (username, password, enabled) values ('customer', 'customer', true);" +
                "insert into authorities (username, authority) values ('customer', 'ROLE_CUSTOMER');");

    }








    public List<InvoiceDTO> getInvoices() {
        return invoiceAssembler.toResources(invoiceRepository.findAll());
    }

    public InvoiceDTO getsingleInvoices(String id) {
        return invoiceAssembler.toResource(invoiceRepository.getOne(id));
    }


    public void sendInvoice(Resource<PurchaseOrderDTO> purchaseOrderDTO) {

        USER.current_uri=purchaseOrderDTO.getContent().getConsumerURI();
        USER.destination_email=USER.users.get(USER.current_uri);

        USER.destination_email = customerRepository.findByConsumerURI(purchaseOrderDTO.getContent().getConsumerURI()).get(0).getEmailAddress();




        LocalDate localDate = LocalDate.now();
        localDate.plusDays(14);


        Invoice invoice = Invoice.of(invoiceIdentifierFactory.nextInvoiceID(), purchaseOrderDTO.getContent().get_id(), purchaseOrderDTO.getContent().getTotal(),
                localDate, InvoiceStatus.UNPAID);

        invoiceRepository.save(invoice);

        sendInvoice(invoiceAssembler.toResource(invoice));
    }

    public void sendInvoice(InvoiceDTO invoiceDTO) {
//        sendInvoiceHTTP(invoiceDTO);
        sendInvoiceMAIL(invoiceDTO);
    }

    private void sendInvoiceHTTP(InvoiceDTO invoiceDTO) {
        String json = null;
        try {
            json = mapper.writeValueAsString(invoiceDTO);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        invoicingGateway.sendInvoice(json);
    }

    private void sendInvoiceMAIL(InvoiceDTO invoiceDTO) {
        String MAIL_SUBJECT = "Invoice Purchase Order No.";
        final String MAIL_TEXT = "Dear customer,\n\nPlease find attached the Invoice corresponding to your above mentioned Purchase Order." +
                "\n\nKindly yours,\n\nRentIt Team!";
        JavaMailSender mailSender = new JavaMailSenderImpl();

        String invoice;
        try {
            invoice = mapper.writeValueAsString(invoiceDTO);
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
            invoice="{\n" +
                    "  \"order\":{\"_links\":{\"self\":{\"href\": \"http://rentit.com/api/sales/orders/1\"}}},\n" +
                    "  \"amount\":800,\n" +
                    "  \"dueDate\": \"2018-07-15\"\n" +
                    "}\n";
        }
        MimeMessage rootMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(rootMessage, true);
            helper.setFrom(emailFrom);
            helper.setTo(USER.destination_email);
            helper.setSubject(MAIL_SUBJECT + invoiceDTO.get_id());
            helper.setText(MAIL_TEXT);

            String filename = "invoice-po-"+invoiceDTO.getPoID()+".json";

            helper.addAttachment(filename, new ByteArrayDataSource(invoice, "application/json"));
        } catch (MessagingException | IOException m) {
            m.printStackTrace();
        }

        invoicingGateway.sendInvoice(rootMessage);
    }



    private void sendInvoiceReminder(InvoiceDTO invoiceDTO) {
        String MAIL_SUBJECT = "Reminder Invoice Purchase Order No.";
        final String MAIL_TEXT = "Dear customer,\n\nThis is a reminder email for the invoice which is still unpaid for following" +
                "\nPurchase order No: " +invoiceDTO.getPoID()+
                "\n\nKindly yours,\n\nRentIt Team!";

        JavaMailSender mailSender = new JavaMailSenderImpl();

        PurchaseOrder poByInvoice= purchaseOrderRepository.getOne(invoiceDTO.getPoID());
        String destinationEmail = USER.users.get(poByInvoice.getConsumerURI());

        String invoice;
        try {
            invoice = mapper.writeValueAsString(invoiceDTO);
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
            invoice="{\n" +
                    "  \"order\":{\"_links\":{\"self\":{\"href\": \"http://rentit.com/api/sales/orders/1\"}}},\n" +
                    "  \"amount\":800,\n" +
                    "  \"dueDate\": \"2018-07-15\"\n" +
                    "}\n";
        }
        MimeMessage rootMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(rootMessage, true);
            helper.setFrom(emailFrom);
            helper.setTo(destinationEmail);
            helper.setSubject(MAIL_SUBJECT + invoiceDTO.get_id());
            helper.setText(MAIL_TEXT);

            String filename = "invoice-po-"+invoiceDTO.getPoID()+".json";

            helper.addAttachment(filename, new ByteArrayDataSource(invoice, "application/json"));
        } catch (MessagingException | IOException m) {
            m.printStackTrace();
        }

        invoicingGateway.sendInvoice(rootMessage);
    }

    /**
     * Send Reminders for UNPAID Invoices every Monday at 10:00
     */
    @Scheduled(cron = "0 0 10 * * MON")
    private void InvoiceReminders()
    {
        List<Invoice> unpaidInvoices=invoiceRepository.findInvoiceByStatus(InvoiceStatus.UNPAID);
        List<InvoiceDTO> unpaidInvoicesDtos = invoiceAssembler.toResources(unpaidInvoices);

        for(InvoiceDTO invoiceDTO : unpaidInvoicesDtos)
        {
            sendInvoiceReminder(invoiceDTO);
        }

    }



    //-------------------------------------------------------------------------------------------------

    // Test methods to check inbound Flows of BuiltIT..

    // Added for testing source and destination of gateway
    public void processInvoice(String invoiceStr) {
        System.out.println("Yeee invoice haiiiiiiiiii !------------->"+invoiceStr);

        InvoiceDTO invoiceDTO = null;
        try {
            invoiceDTO = mapper.readValue(invoiceStr, new TypeReference<InvoiceDTO>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Yeee DTO :O !------------->"+invoiceDTO);

    }

    public void testmethod()
    {
        System.out.println("In test.....");
    }

    public void testMailmethod(String invoiceStr)
    {

        InvoiceDTO invoiceDTO = null;
        try {
            invoiceDTO = mapper.readValue(invoiceStr, new TypeReference<InvoiceDTO>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(invoiceDTO);

    }

}
