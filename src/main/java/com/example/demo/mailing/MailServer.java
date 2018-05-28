package com.example.demo.mailing;

import com.example.demo.invoicing.application.services.InvoiceService;
import org.apache.commons.io.IOUtils;
import org.springframework.context.ApplicationContext;


import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
import java.util.Properties;


public class MailServer {

    public void initDb()
    {
        ApplicationContext context = SpringContext.getAppContext();
        // get instance of MainSpringClass (Spring Managed class)
        InvoiceService invoiceService = (InvoiceService) context.getBean("invoiceService");
        invoiceService.executesDynamicQueries();
    }

}
