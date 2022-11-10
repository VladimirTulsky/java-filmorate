package ru.yandex.practicum.filmorate.config;

import springfox.documentation.builders.RequestHandlerSelectors;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.spi.DocumentationType;
import org.springframework.context.annotation.Bean;

/**
 * @author Vladimir Tulskii
 */

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("ru.yandex.practicum.filmorate.controller"))
                .paths(PathSelectors.any())
                .build();
    }
}