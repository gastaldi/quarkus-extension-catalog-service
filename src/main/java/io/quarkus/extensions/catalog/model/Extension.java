package io.quarkus.extensions.catalog.model;

import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * An extension is a Maven dependency that can be added to a Quarkus project
 */
@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableExtension.class)
@JsonSerialize(as = ImmutableExtension.class)
public interface Extension {

    String getName();

    Coordinates getCoords();

    @Value.Default
    default Map<String, Object> getMetadata() {
        return Collections.emptyMap();
    }

    @Value.Immutable
    @JsonSerialize(as=ImmutableCoordinates.class)
    @JsonDeserialize(as=ImmutableCoordinates.class)
    public interface Coordinates {
        String getGroupId();
        String getArtifactId();
        String getVersion();
    }
}