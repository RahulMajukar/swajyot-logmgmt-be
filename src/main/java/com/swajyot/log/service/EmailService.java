package com.swajyot.log.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Send an email with a PDF attachment
     * 
     * @param to Recipient email address
     * @param subject Email subject
     * @param body Email body
     * @param pdfBytes PDF content as byte array
     * @param fileName Name of the PDF file
     */
    public void sendEmailWithAttachment(String to, String subject, String body, 
                                       byte[] pdfBytes, String fileName) throws MessagingException {
        
        MimeMessage message = mailSender.createMimeMessage();
        
        // Use MimeMessageHelper to set multipart message properties
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true); // true means text is HTML
        
        // Add the PDF attachment
        ByteArrayResource resource = new ByteArrayResource(pdfBytes);
        helper.addAttachment(fileName, resource);
        
        // Send the email
        mailSender.send(message);
    }
}