package io.quarkus.extensions.catalog.model.registry;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.quarkus.extensions.catalog.model.Release;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = PlatformBuilder.class)
public interface Platform {

    @JsonUnwrapped
    ArtifactKey getId();

    @Value.Auxiliary
    Set<Release> getReleases();
}
