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

    List<QuarkusPlatformDescriptor> getPlatformsForExtension(Extension extension);

    List<Extension> getExtensionsByCoreVersion(String version);

    // findById methods
    Optional<Extension> findByExtensionId(String id);

    /**
     * The extensions and platforms to be added to the build descriptor
     * @param extensions the extensions to be resolved
     * @return a {@link LookupResult} with the
     */
    LookupResult lookup(String quarkusCore, Collection<AppArtifactKey> extensions);

    @Value.Immutable
    interface LookupResult {
        /**
         * @return Platforms (BOMs) to be added to the build descriptor
         */
        Collection<QuarkusPlatformDescriptor> getPlatforms();

        /**
         * @return Extensions that are included in the platforms returned in {@link #getPlatforms()},
         * therefore setting the version is not required.
         */
        Collection<Extension> getExtensionsInPlatforms();

        /**
         * @return Extensions that do not exist in any platform
         */
        Collection<Extension> getIndependentExtensions();
    }
}
