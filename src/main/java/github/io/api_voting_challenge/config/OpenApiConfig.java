package github.io.api_voting_challenge.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Voting Challenge 2.0")
                        .version("1.0.0")
                        .description("This RESTful API was developed to manage and facilitate the agenda voting process efficiently and transparently. The solution allows for the registration of new agendas, the opening of timed voting sessions, the receipt of 'Yes' or 'No' votes from individual users, and the final tallying of results.")
                        .termsOfService("http://swagger.io/terms/")
                        .contact(new Contact()
                                .name("Josias Barreto")
                                .url("https://github.com/josiasbarreto-dev")
                                .email("sr.josiasbarreto@gmail.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html"))
                );
    }
}