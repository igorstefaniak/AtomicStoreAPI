package pl.projekt.store.config;

import java.util.List;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI defineOpenApi() {
        Server server = new Server();
        server.setUrl("http://localhost:8080");
        server.setDescription("Atomic Store");

        Contact contact = new Contact();
        contact.setName("Igor Stefaniak, Karolina Wrzalik");
        contact.setEmail("igorstefaniak07@gmail.com, karolinawrzalik66@gmail.com");
        contact.setUrl("https://igorstefaniak.pl");
        

        Info information = new Info()
                .title("Atomic Store")
                .version("1.0")
                .description("To API udostępnia punkty końcowe umożliwiające zarządzanie sklepem \"Atomic Store\" w krypcie 000.")
                .contact(contact);
        return new OpenAPI().info(information).servers(List.of(server));
    }

    @Bean
    public GroupedOpenApi restrictedApi() {
        return GroupedOpenApi.builder()
                .group("Zastrzeżone metody (wymagające logowania)")
                .pathsToMatch("/api/private/users/**", "/api/private/admin/**")
                .build();
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("Publiczne metody (niewymagające logowania)")
                .pathsToMatch("/api/public/**", "/api/public/cursor/**")
                .build();
    }
}
