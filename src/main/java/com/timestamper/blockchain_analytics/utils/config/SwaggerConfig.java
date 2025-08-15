package com.timestamper.blockchain_analytics.utils.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Crypto Transaction API")
                        .description("Crypto Transaction 관리 API")
                        .version("1.0"))
                .addTagsItem(new Tag().name("Crypto Transaction API").description("Crypto Transaction 관리 API"));
    }
}
