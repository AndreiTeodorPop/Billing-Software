package com.marketplace.billingsoftware.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfiguration {

    @Bean
    public OpenAPI defineOpenAPI() {
        Server server = new Server();
        server.setUrl("http://localhost:8080/api/v1.0");
        server.setDescription("Development");

        Contact myContact = new Contact();
        myContact.setName("Andrei-Teodor Pop");
        myContact.setEmail("popcfrcluj@yahoo.com");

        Info information = new Info()
                .title("Billing Software API")
                .version("1.0")
                .description("This API exposes endpoints to products.")
                .contact(myContact);
        return new OpenAPI().info(information).servers(List.of(server));
    }
}
