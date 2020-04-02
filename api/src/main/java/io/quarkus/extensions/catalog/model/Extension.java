package io.quarkus.extensions.catalog.model;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Immutable;

/**
 * An extension is a Maven dependency that can be added to a Quarkus project
 */
@Immutable
@JsonDeserialize(builder = ExtensionBuilder.class)
public interface Extension {

    @Auxiliary
    String getName();

    GroupArtifact getCoords();

    @Auxiliary
    Map<String, Object> getMetadata();

    @Auxiliary
    List<ExtensionRelease> getReleases();

    @Immutable
    @JsonDeserialize(builder = ExtensionReleaseBuilder.class)
    interface ExtensionRelease {

        String getVersion();

        String getQuarkusVersion();
    }
}