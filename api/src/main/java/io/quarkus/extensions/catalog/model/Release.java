package io.quarkus.extensions.catalog.model;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value.Immutable;

@Immutable
@JsonDeserialize(builder = ReleaseBuilder.class)
public interface Release {
    String getVersion();

    @Nullable
    @JsonProperty("quarkus-core")
    String getQuarkusCore();
}