package io.quarkus.extensions.catalog.model.registry;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ArtifactKeyBuilder.class)
public interface ArtifactKey {
    @JsonProperty("group-id")
    String getGroupId();
    @JsonProperty("artifact-id")
    String getArtifactId();
}
