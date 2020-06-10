package io.quarkus.registry.model;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.quarkus.bootstrap.model.AppArtifactCoords;
import io.quarkus.bootstrap.model.AppArtifactKey;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ExtensionBuilder.class)
public interface Extension {
    @JsonUnwrapped
    @JsonIgnoreProperties(value = {"type", "classifier", "key"})
    AppArtifactKey getId();

    @Value.Auxiliary
    String getName();

    @Value.Auxiliary
    @Nullable
    String getDescription();

    @Value.Auxiliary
    Map<String, Object> getMetadata();

    @Value.Auxiliary
    @Value.ReverseOrder
    SortedSet<ExtensionRelease> getReleases();

    static ExtensionBuilder builder() {
        return new ExtensionBuilder();
    }

    @Value.Immutable
    @JsonDeserialize(builder = ExtensionReleaseBuilder.class)
    interface ExtensionRelease extends Comparable<ExtensionRelease>{

        @JsonUnwrapped
        Release getRelease();

        @Value.Auxiliary
        @JsonIgnoreProperties(value = {"type", "classifier", "key"})
        Set<AppArtifactCoords> getPlatforms();

        static ExtensionReleaseBuilder builder() {
            return new ExtensionReleaseBuilder();
        }

        @Override
        default int compareTo(ExtensionRelease o) {
            return getRelease().compareTo(o.getRelease());
        }
    }
}