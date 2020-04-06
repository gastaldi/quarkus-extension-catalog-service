package io.quarkus.extensions.catalog.model;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value.Immutable;

/**
 * A {@link Platform} holds a set of extensions
 */
@Immutable
@JsonDeserialize(builder = PlatformBuilder.class)
public interface Platform {

    String getGroupId();

    String getArtifactId();

    List<Release> getReleases();
}