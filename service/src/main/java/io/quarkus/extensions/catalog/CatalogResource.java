package io.quarkus.extensions.catalog;

import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.quarkus.extensions.catalog.model.Extension;

@Path("/catalog")
public class CatalogResource {

    /**
     * Retrieves the metadata and inserts into the Neo4J database
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/index")
    public void indexExtension(@FormParam("groupId") String groupId,
                      @FormParam("artifactId") String artifactId,
                      @FormParam("version") String version) {

    }


}