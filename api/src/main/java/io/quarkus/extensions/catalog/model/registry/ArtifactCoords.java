package io.quarkus.extensions.catalog.model.registry;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ArtifactCoordsBuilder.class)
public interface ArtifactCoords {
    @JsonUnwrapped
    ArtifactKey getKey();

    String getVersion();
}
