package com.example.demo.invoicing.application;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import javax.mail.*;

import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Service
class InvoiceProcessor {


    public String extractInvoice(MimeMessage msg) throws Exception{

        Folder folder = msg.getFolder();
        folder.open(Folder.READ_WRITE);

        for (int i = 0; i < folder.getMessageCount(); i++) {
            Message message = folder.getMessage(i+1);
            if (message instanceof MimeMessage && ((MimeMessage) message).getMessageID().equals(msg.getMessageID())) {
                message.setFlag(Flags.Flag.DELETED, true);
                msg = (MimeMessage) message;
                break;
            }
        }

        Multipart multipart = (Multipart) msg.getContent();
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = (BodyPart)multipart.getBodyPart(i);
            System.out.println("in LOOP");
            System.out.println(bodyPart.getContentType());
            System.out.println(bodyPart.getFileName());

            if (bodyPart.getFileName() != null && bodyPart.getFileName().startsWith("invoice")) {
                System.out.println("In true case...");
                System.out.println(bodyPart.getFileName());
                System.out.println(bodyPart.getDisposition());
                System.out.println(folder.isOpen());
//                InputStream inputStream=bodyPart.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(bodyPart.getInputStream()));
//                bodyPart.saveFile(bodyPart.getFileName());
//                bodyPart
                String result = IOUtils.toString(bodyPart.getInputStream(), "UTF-8");
                folder.expunge();
                return result;
            }
        }
        folder.expunge();
        throw new Exception("oops ... no invoice included in email");
    }


}

