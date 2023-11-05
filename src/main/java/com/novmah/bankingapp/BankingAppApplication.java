package com.novmah.bankingapp;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Banking App",
                description = "Backend Rest APIS for Manh",
                version = "v1.0",
                contact = @Contact(
                        name = "Manh Nguyen",
                        email = "manh06022003@gmail.com",
                        url = "https://github.com/novmah62/banking-app"
                ),
                license = @License(
                        name = "novmah",
                        url = "https://github.com/novmah62/banking-app"
                )
        ),
        externalDocs = @ExternalDocumentation(
                description = "Banking App Documentation",
                url = "https://github.com/novmah62/banking-app"
        )
)
public class BankingAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankingAppApplication.class, args);
    }

}
