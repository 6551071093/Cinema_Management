package com.cinema.service;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
public class EmailService {
    private final String senderEmail = "your_email@gmail.com"; 
    private final String senderPassword = "your_app_password"; 

    public boolean sendEmail(String to, String subject, String content) {
         return true; 
    }
}
