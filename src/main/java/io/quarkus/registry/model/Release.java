package io.quarkus.registry.model;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ReleaseBuilder.class)
public interface Release extends Comparable<Release> {

    String getVersion();

    @Nullable
    @JsonProperty("quarkus-core")
    @Value.Auxiliary
    String getQuarkusCore();

    @JsonProperty("repository-url")
    @Value.Auxiliary
    @Nullable
    String getRepositoryURL();

    @Override
    default int compareTo(Release o) {
        //TODO: Compare using SemVer rules
        return getVersion().compareTo(o.getVersion());
    }
}
