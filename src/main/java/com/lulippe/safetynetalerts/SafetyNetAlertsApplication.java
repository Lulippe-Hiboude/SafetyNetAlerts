package com.lulippe.safetynetalerts;

import com.lulippe.safetynetalerts.dto.DataContainer;
import com.lulippe.safetynetalerts.repository.manager.DataManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class SafetyNetAlertsApplication {
   public static void main(String[] args) {
        SpringApplication.run(SafetyNetAlertsApplication.class, args);
    }

}
