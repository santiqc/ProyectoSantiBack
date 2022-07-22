package com.proyecto.santi.emailpassword.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.santi.dto.Mensaje;
import com.proyecto.santi.emailpassword.dto.EmailValuesDTO;
import com.proyecto.santi.emailpassword.service.EmailService;
@RestController
@RequestMapping("/email-password")
@CrossOrigin
public class EmailController {

    @Autowired
    EmailService emailService;

    @Value("${spring.mail.username}")
    private String mailFrom;

    @PostMapping("/send-email")
    public ResponseEntity<?> sendEmailTemplate(@RequestBody EmailValuesDTO dto) {
    	dto.setMailFrom(mailFrom);
    	dto.setSubject("cambio de contrseña");
    	dto.setUserName("Cristiano");
    	UUID uuid= UUID.randomUUID();
    	String tokenPassword= uuid.toString();
    	dto.setTokenPassword(tokenPassword);
        emailService.sendEmail(dto);
        return new ResponseEntity(new Mensaje("Te hemos enviado un correo"), HttpStatus.OK);
    }
}