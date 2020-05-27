package io.quarkus.extensions.catalog.model.registry;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.quarkus.extensions.catalog.model.Release;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ExtensionBuilder.class)
public interface Extension {
    @JsonUnwrapped
    ArtifactKey getId();

    @Value.Auxiliary
    String getName();

    @Value.Auxiliary
    @Nullable
    String getDescription();

    @Value.Auxiliary
    Map<String, Object> getMetadata();

    @Value.Auxiliary
    @Value.ReverseOrder
    SortedSet<Release> getReleases();

    @Value.Auxiliary
    List<ArtifactCoords> getPlatforms();
}
