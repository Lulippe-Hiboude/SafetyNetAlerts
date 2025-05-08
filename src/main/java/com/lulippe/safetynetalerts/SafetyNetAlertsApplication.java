package com.lulippe.safetynetalerts;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@OpenAPIDefinition(
        info = @Info(
                title = "SafetyNet API",
                version = "1.0",
                description = "Documentation de l'API SafetyNet"
        )
)
@Slf4j
@SpringBootApplication
public class SafetyNetAlertsApplication {
   public static void main(String[] args) {
        SpringApplication.run(SafetyNetAlertsApplication.class, args);
    }

}
