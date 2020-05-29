package io.quarkus.extensions.catalog.app;

import java.util.Objects;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.annotations.GZIP;

@Path("/")
public class RegistryResource {

    private static final String REGISTRY_VERSION_HEADER = "X-Registry-Version";

    @Inject
    RegistryService registryService;

    @Inject
    @ConfigProperty(name="registry.version", defaultValue = "")
    String registryVersion;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response registry(@Context HttpHeaders httpHeaders) {
        if (Objects.equals(registryVersion,httpHeaders.getHeaderString(REGISTRY_VERSION_HEADER))) {
            return Response.notModified().build();
        }
        if (registryService.getRegistry() == null) {
            return Response.serverError().entity("No registry found").build();
        }
        return Response.ok(registryService.getRegistry())
                .header(REGISTRY_VERSION_HEADER, registryVersion)
                .build();
    }
}