package io.quarkus.extensions.catalog.model;

import java.util.List;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.immutables.value.Value.Immutable;

/**
 * An extension is a Maven dependency that can be added to a Quarkus project
 */
@Immutable
@JsonDeserialize(builder = ExtensionBuilder.class)
public interface Extension {

    @JsonProperty("group-id")
    String getGroupId();

    @JsonProperty("artifact-id")
    String getArtifactId();

    @Value.Auxiliary
    List<Release> getReleases();
}