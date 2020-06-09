package io.quarkus.registry.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.quarkus.bootstrap.model.AppArtifactKey;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = PlatformBuilder.class)
public interface Platform {

    @JsonUnwrapped
    @JsonIgnoreProperties(value = {"type", "classifier", "key"})
    AppArtifactKey getId();

    @Value.Auxiliary
    Set<Release> getReleases();
}
