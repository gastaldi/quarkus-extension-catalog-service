package io.quarkus.extensions.catalog.model;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Immutable;

/**
 * An extension is a Maven dependency that can be added to a Quarkus project
 */
@Immutable
@JsonDeserialize(as = ImmutableExtension.class)
@JsonSerialize(as = ImmutableExtension.class)
public interface Extension {

    @Auxiliary
    String getName();

    Coordinates getCoords();

    @Nullable
    @Auxiliary
    Map<String, Object> getMetadata();

    @Auxiliary
    List<ExtensionRelease> getReleases();

    @Immutable
    @JsonSerialize(as = ImmutableCoordinates.class)
    @JsonDeserialize(as = ImmutableCoordinates.class)
    public interface Coordinates {
        String getGroupId();
        String getArtifactId();
        String getVersion();
    }

    @Immutable
    @JsonSerialize(as = ImmutableExtensionRelease.class)
    @JsonDeserialize(as = ImmutableExtensionRelease.class)
    public interface ExtensionRelease {
        String getVersion();
    }
}