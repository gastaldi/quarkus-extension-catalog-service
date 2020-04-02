package io.quarkus.extensions.catalog.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value.Immutable;

@Immutable
@JsonDeserialize(builder = GroupArtifactBuilder.class+)
public interface GroupArtifact {
    String getGroupId();
    String getArtifactId();
}