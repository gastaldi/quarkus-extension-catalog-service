package io.quarkus.extensions.catalog.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import io.quarkus.extensions.catalog.model.registry.Registry;
import io.quarkus.runtime.Startup;

@ApplicationScoped
@Startup
public class RegistryService {

    @Inject
    ObjectMapper mapper;

    Registry registry;

    @PostConstruct
    void initializeRegistry() {
        InputStream registryJson = Thread.currentThread().getContextClassLoader().getResourceAsStream("registry.json");
        if (registryJson != null) {
            try {
                mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
                        .setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);
                this.registry = mapper.readValue(registryJson, Registry.class);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    public Registry getRegistry() {
        return registry;
    }
}
