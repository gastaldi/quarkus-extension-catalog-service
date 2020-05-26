package io.quarkus.extensions.catalog.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.immutables.value.Value.Immutable;

/**
 * A {@link Platform} holds a set of extensions
 */
@Immutable
@JsonDeserialize(builder = PlatformBuilder.class)
public interface Platform {

    @JsonProperty("group-id")
    String getGroupId();

    @Value.Default
    @JsonProperty("group-id-json")
    @Value.Auxiliary
    default String getGroupIdJson() {
        return getGroupId();
    }

    @JsonProperty("artifact-id")
    String getArtifactId();

    @Value.Default
    @JsonProperty("artifact-id-json")
    @Value.Auxiliary
    default String getArtifactIdJson() {
        return getArtifactId();
    }

    @Value.Auxiliary
    List<Release> getReleases();
}