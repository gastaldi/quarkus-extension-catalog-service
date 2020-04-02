package io.quarkus.extensions.catalog.model;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.quarkus.extensions.catalog.model.Extension.ExtensionRelease;
import org.immutables.value.Value.Immutable;

/**
 * A {@link Platform} holds a set of extensions
 */
@Immutable
public interface Platform {

    List<PlatformRelease> getReleases();

    GroupArtifact getCoords();

    @Immutable
    interface PlatformRelease {

        String getVersion();

        Set<ExtensionRelease> getExtensions();
    }
}