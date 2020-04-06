package io.quarkus.extensions.catalog.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value.Immutable;

@Immutable
@JsonDeserialize(builder = ReleaseBuilder.class)
public interface Release {
    String getVersion();
    String getQuarkusVersion();
}
