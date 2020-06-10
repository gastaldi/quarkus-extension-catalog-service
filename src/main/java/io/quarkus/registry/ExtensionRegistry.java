package io.quarkus.registry;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import io.quarkus.bootstrap.model.AppArtifactCoords;
import io.quarkus.bootstrap.model.AppArtifactKey;
import io.quarkus.dependencies.Extension;
import io.quarkus.platform.descriptor.QuarkusPlatformDescriptor;
import org.immutables.value.Value;

public interface ExtensionRegistry {

    // Query methods
    Set<String> getQuarkusCoreVersions();

    Set<Extension> getExtensionsByCoreVersion(String version);

    // findById methods
    Optional<Extension> findByExtensionId(AppArtifactCoords id);

    /**
     * Return the set of extensions for a given query
     *
     * @param quarkusCore the quarkus core this extension supports
     * @param keyword the keyword to search
     * @return a set of {@link Extension} objects or an empty set if not found
     */
    Set<Extension> list(String quarkusCore, String keyword);

    /**
     * The extensions and platforms to be added to the build descriptor
     * @param lookupParameters the parameters to be used when searching for extensions
     * @return a {@link LookupResult}
     */
    LookupResult lookup(LookupParameters lookupParameters);

    @Value.Immutable
    interface LookupParameters {

        @Nullable
        String getQuarkusCore();

        List<AppArtifactKey> getExtensions();
    }

    @Value.Immutable
    interface LookupResult {
        /**
         * @return Platforms (BOMs) to be added to the build descriptor
         */
        Set<AppArtifactCoords> getPlatforms();

        /**
         * @return Extensions that are included in the platforms returned in {@link #getPlatforms()},
         * therefore setting the version is not required.
         */
        Set<Extension> getExtensionsInPlatforms();

        /**
         * @return Extensions that do not exist in any platform, the version MUST be set in the build descriptor
         */
        Set<Extension> getIndependentExtensions();
    }
}