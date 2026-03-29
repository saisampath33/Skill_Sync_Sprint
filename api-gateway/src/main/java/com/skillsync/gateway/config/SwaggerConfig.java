package com.skillsync.gateway.config;

import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Configuration
public class SwaggerConfig {

    /**
     * Dynamically populates the Swagger UI dropdown with microservices 
     * discovered via Eureka and defined in Gateway routes.
     */
    @Bean
    public List<AbstractSwaggerUiConfigProperties.SwaggerUrl> swaggerUrls(
            RouteDefinitionLocator locator,
            SwaggerUiConfigProperties swaggerUiConfigProperties) {
        
        Set<AbstractSwaggerUiConfigProperties.SwaggerUrl> urls = new HashSet<>();
        
        locator.getRouteDefinitions()
                .filter(routeDefinition -> routeDefinition.getId().endsWith("-service"))
                .subscribe(routeDefinition -> {
                    String name = routeDefinition.getId().replace("-service", "").toUpperCase();
                    String url = "/" + routeDefinition.getId() + "/v3/api-docs";
                    
                    AbstractSwaggerUiConfigProperties.SwaggerUrl swaggerUrl = 
                            new AbstractSwaggerUiConfigProperties.SwaggerUrl(name, url, name);
                    urls.add(swaggerUrl);
                });
        
        swaggerUiConfigProperties.setUrls(urls);
        return List.copyOf(urls);
    }
}
