package io.quarkus.registry.model;

import java.util.List;
import java.util.SortedSet;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ExtensionBuilder.class)
public interface Extension {
    @JsonUnwrapped
    ArtifactKey getId();

    @JsonUnwrapped
    io.quarkus.dependencies.Extension getExtension();

    @Value.Auxiliary
    @Value.ReverseOrder
    SortedSet<Release> getReleases();

    @Value.Auxiliary
    List<ArtifactCoords> getPlatforms();
}
