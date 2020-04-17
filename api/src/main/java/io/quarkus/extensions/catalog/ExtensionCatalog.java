package io.quarkus.extensions.catalog;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import io.quarkus.bootstrap.model.AppArtifactKey;
import io.quarkus.dependencies.Extension;
import io.quarkus.platform.descriptor.QuarkusPlatformDescriptor;
import org.immutables.value.Value;

public interface ExtensionCatalog {

    // Query methods
    Set<String> getQuarkusCoreVersions();

    Set<Extension> getExtensionsByCoreVersion(String version);

    // findById methods
    Optional<Extension> findByExtensionId(String id);

    /**
     * The extensions and platforms to be added to the build descriptor
     * @param extensions the extensions GA (groupId + ":" + artifactId) to be resolved
     * @return a {@link LookupResult}
     */
    LookupResult lookup(String quarkusCore, Collection<AppArtifactKey> extensions);

    @Value.Immutable
    interface LookupResult {
        /**
         * @return Platforms (BOMs) to be added to the build descriptor
         */
        List<QuarkusPlatformDescriptor> getPlatforms();

        /**
         * @return Extensions that are included in the platforms returned in {@link #getPlatforms()},
         * therefore setting the version is not required.
         */
        List<Extension> getExtensionsInPlatforms();

        /**
         * @return Extensions that do not exist in any platform, the version MUST be set in the build descriptor
         */
        List<Extension> getIndependentExtensions();
    }
}
