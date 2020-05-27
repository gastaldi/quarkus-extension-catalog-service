package io.quarkus.extensions.catalog.app;

import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.extensions.catalog.model.registry.Registry;

@ApplicationScoped
public class RegistryService {

    @Inject
    ObjectMapper mapper;

    public Registry getRegistry() {
        return null;
    }

    private URI getRegistryURL() {
        return URI.create("");
    }

}
