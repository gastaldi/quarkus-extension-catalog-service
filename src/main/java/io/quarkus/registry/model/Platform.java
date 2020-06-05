package io.quarkus.registry.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = PlatformBuilder.class)
public interface Platform {

    @JsonUnwrapped
    ArtifactKey getId();

    @Value.Auxiliary
    Set<Release> getReleases();
}
