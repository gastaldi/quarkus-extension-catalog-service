package io.quarkus.extensions.catalog.spi;

import java.io.IOException;

import io.quarkus.extensions.catalog.model.Extension;
import io.quarkus.extensions.catalog.model.Platform;
import io.quarkus.extensions.catalog.model.Release;
import io.quarkus.platform.descriptor.QuarkusPlatformDescriptor;

public interface ArtifactResolver {
    QuarkusPlatformDescriptor resolvePlatform(Platform platform, Release release) throws IOException;
    io.quarkus.dependencies.Extension resolveExtension(Extension extension, Release release) throws IOException;
}
