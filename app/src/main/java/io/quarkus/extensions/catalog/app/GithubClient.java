package io.quarkus.extensions.catalog.app;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import io.quarkus.extensions.catalog.model.registry.Registry;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(baseUri = "https://gastaldi.github.io")
public interface GithubClient {

    @GET
    @Path("/quarkus-extension-catalog/registry.json")
    Registry readRegistry();
}
