package io.quarkus.extensions.catalog.model.registry;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.quarkus.bootstrap.model.AppArtifactKey;
import io.quarkus.extensions.catalog.model.Release;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = PlatformBuilder.class)
public interface Platform {

    AppArtifactKey getKey();

    @Value.Auxiliary
    Set<Release> getReleases();
}
