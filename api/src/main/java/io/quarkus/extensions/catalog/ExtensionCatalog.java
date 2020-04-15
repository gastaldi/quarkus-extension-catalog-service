package io.quarkus.extensions.catalog;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import io.quarkus.dependencies.Extension;
import io.quarkus.platform.descriptor.QuarkusPlatformDescriptor;

public interface ExtensionCatalog {
    // Query methods
    Set<String> getQuarkusCoreVersions();
    List<QuarkusPlatformDescriptor> getPlatformsForExtension(Extension extension);
    List<Extension> getExtensionsByCoreVersion(String version);

    // findById methods
    Optional<Extension> findByExtensionId(String id);
}
