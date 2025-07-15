package com.jd.myutils;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

public class EmailSender {
    public void simpleMailSend(String email, String subject, String msg) {
        //创建邮件发送服务器
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("mx.jd.local");
//      mailSender.setPort(465);
        mailSender.setUsername("bdp-service");
        mailSender.setPassword("!@#QWEasd2014061");
        //加认证机制
        Properties javaMailProperties = new Properties();
        javaMailProperties.setProperty("mail.smtp.auth", "true");
        javaMailProperties.setProperty("mail.smtp.timeout", "25000");
        mailSender.setJavaMailProperties(javaMailProperties);
        //创建邮件内容
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("bdp-service@jd.com");
        message.setTo(email);
        message.setSubject(subject);
        message.setText(msg);
        //发送邮件
        mailSender.send(message);
    }

    public static void main(String[] args) {
        EmailSender sender = new EmailSender();
        sender.simpleMailSend("liujianjia@jd.com", "xxx", "xxxx");
    }
}