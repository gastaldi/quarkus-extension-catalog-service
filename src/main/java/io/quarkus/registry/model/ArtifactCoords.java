package io.quarkus.registry.model;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ArtifactCoordsBuilder.class)
public interface ArtifactCoords {
    @JsonUnwrapped
    ArtifactKey getId();

    String getVersion();

    static ArtifactCoordsBuilder builder() {
        return new ArtifactCoordsBuilder();
    }
}
