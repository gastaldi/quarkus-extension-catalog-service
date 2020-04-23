package io.quarkus.extensions.catalog.spi;

import java.io.IOException;

import io.quarkus.extensions.catalog.model.Extension;
import io.quarkus.extensions.catalog.model.Platform;
import io.quarkus.extensions.catalog.model.Release;
import io.quarkus.platform.descriptor.QuarkusPlatformDescriptor;

/**
 * Resolves artifacts from the underlying artifact repositories
 */
public interface ArtifactResolver {
    /**
     * Resolve the latest and greatest version for this platform
     */
    QuarkusPlatformDescriptor resolveLatestPlatform(Platform platform) throws IOException;
    /**
     * Resolve the latest and greatest version for this extension
     */
    io.quarkus.dependencies.Extension resolveLatestExtension(Extension extension) throws IOException;

    /**
     * Resolve this specific platform
     */
    QuarkusPlatformDescriptor resolvePlatform(Platform platform, Release release) throws IOException;

    /**
     * Resolve this specific extension
     */
    io.quarkus.dependencies.Extension resolveExtension(Extension extension, Release release) throws IOException;
}
