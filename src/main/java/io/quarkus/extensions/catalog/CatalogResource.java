package io.quarkus.extensions.catalog;

import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.quarkus.extensions.catalog.model.Extension;
import io.quarkus.extensions.catalog.service.CatalogService;

@Path("/catalog")
public class CatalogResource {

    @Inject
    CatalogService catalogService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/all")
    public Set<Extension> all() {
        return catalogService.getExtensions();
    }
}