package io.quarkus.extensions.catalog.app;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.quarkus.extensions.catalog.model.registry.Registry;

@Path("/api/registry")
public class RegistryResource {

    @Inject
    RegistryService registryService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Registry registry() {
        return registryService.getRegistry();
    }
}