package com.iw.gateway.config;

import org.springdoc.core.AbstractSwaggerUiConfigProperties;
import org.springdoc.core.SwaggerUiConfigProperties;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * spring-doc文档
 * @Author: likey
 */
@Configuration
public class SpringDocConfig {
    protected final SwaggerUiConfigProperties swaggerUiConfigProperties;
    protected final RouteDefinitionLocator routeDefinitionLocator;

    public SpringDocConfig(SwaggerUiConfigProperties swaggerUiConfigProperties, RouteDefinitionLocator routeDefinitionLocator) {
        this.swaggerUiConfigProperties = swaggerUiConfigProperties;
        this.routeDefinitionLocator = routeDefinitionLocator;
    }

    @PostConstruct
    public void autoInitSwaggerUrls() {
        List<RouteDefinition> definitions = routeDefinitionLocator.getRouteDefinitions().collectList().block();

        definitions.stream().forEach(routeDefinition -> {
            AbstractSwaggerUiConfigProperties.SwaggerUrl swaggerUrl = new AbstractSwaggerUiConfigProperties.SwaggerUrl(
                    routeDefinition.getId().replace("ReactiveCompositeDiscoveryClient_", "").toLowerCase(),
                    routeDefinition.getUri().toString().replace("lb://", "").toLowerCase() + "/v3/api-docs",
                    routeDefinition.getId().replace("ReactiveCompositeDiscoveryClient_", "").toLowerCase()
            );
            Set<AbstractSwaggerUiConfigProperties.SwaggerUrl> urls = swaggerUiConfigProperties.getUrls();
            if (urls == null) {
                urls = new LinkedHashSet<>();
                swaggerUiConfigProperties.setUrls(urls);
            }
            urls.add(swaggerUrl);
        });
    }
}
