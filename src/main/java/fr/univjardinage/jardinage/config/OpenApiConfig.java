package fr.univjardinage.jardinage.config;

import   io.swagger.v3.oas.models.OpenAPI;
import   io.swagger.v3.oas.models.info.Contact;
import   io.swagger.v3.oas.models.info.Info;
import   io.swagger.v3.oas.models.info.License;
import   io.swagger.v3.oas.models.servers.Server;
import   org.springframework.context.annotation.Bean;
import   org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig{

    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title(" API Gestion Produits de Jardinage ")
                        .version(" 1.0.0 ")
                        .description(" API RESTful pour la gestion des produits de jardinage ")
                                        .contact(new Contact()
                                                .name(" Equipe Dev ")
                                                .email(" dev@jardinage.fr ")
                                                .url(" https :// www.jardinage.fr "))
                                        .license(new License()
                                                .name(" Apache 2.0 ")
                                                .url(" https :// www.apache.org / licenses / LICENSE -2.0 ")))
                                                                .servers(List.of(
                                                                        new Server()
                                                                                .url(" http :// localhost :8080 ")
                                                                                .description(" Serveur de developpement ")
                                                                        ,
                                                                        new Server()
                                                                                .url(" https :// api.jardinage.fr ")
                                                                                .description(" Serveur de production ")
                                                                ));
    }
}
