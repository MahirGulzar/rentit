package com.example.demo.mailing;

import com.example.demo.invoicing.application.services.InvoiceService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
import javax.xml.ws.Action;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class TestEmailServer {
//    @Autowired
//    InvoiceService invoiceService;

    String host = "pop.gmail.com";// change accordingly
    String mailStoreType = "pop3";
    String username = "esiteam12@gmail.com";// change accordingly
    String password = "team12345";// change accordingly


//    ApplicationContext context = new ClassPathXmlApplicationContext("path/to/applicationContext.xml");


    public void check()
    {
        System.out.println("Here too.....");
        try {

            //create properties field
            Properties properties = new Properties();

            properties.put("mail.pop3.host", host);
            properties.put("mail.pop3.port", "995");
            properties.put("mail.pop3.starttls.enable", "true");
            Session emailSession = Session.getDefaultInstance(properties);

            //create the POP3 store object and connect with the pop server
            Store store = emailSession.getStore("pop3s");

            store.connect(host, username, password);

            //create the folder object and open it
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            // search for all "unseen" messages
            Flags seen = new Flags(Flags.Flag.SEEN);
            FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
            // retrieve the messages from the folder in an array and print it
            Message[] messages = emailFolder.search(unseenFlagTerm);
            System.out.println("messages.length---" + messages.length);

            List<File> attachments = new ArrayList<File>();

            for (Message message : messages) {
                MimeMessage source = (MimeMessage) message;
                MimeMessage copy = new MimeMessage(source);
                Multipart multipart = (Multipart) message.getContent();
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart bodyPart = (BodyPart)multipart.getBodyPart(i);
                    System.out.println("in LOOP");
                    System.out.println(bodyPart.getContentType());
                    System.out.println(bodyPart.getFileName());


                    if (bodyPart.getFileName() != null && bodyPart.getFileName().startsWith("invoice")) {
                        System.out.println("In true case...");
                        System.out.println(bodyPart.getFileName());
                        System.out.println(bodyPart.getDisposition());
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(bodyPart.getInputStream()));
                        String result = IOUtils.toString(bodyPart.getInputStream(), "UTF-8");
                        System.out.println(result);

                        //get application context
                        ApplicationContext context = SpringContext.getAppContext();
                        // get instance of MainSpringClass (Spring Managed class)
                        InvoiceService invoiceService = (InvoiceService) context.getBean("invoiceService");
                        // use this spring object to call its methods

//                        InvoiceService invoiceService = (InvoiceService)context.getBean("invoiceService");
                        if(invoiceService==null)
                        {
                            System.out.println("its null");
                        }
                        invoiceService.testMailmethod(result);
                    }
                }
            }
            //close the store and folder objects
            emailFolder.close(false);
            store.close();

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
