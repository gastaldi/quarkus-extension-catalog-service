package io.quarkus.extension.catalog.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import io.quarkus.dependencies.Extension;
import io.quarkus.extensions.catalog.ExtensionCatalog;
import io.quarkus.extensions.catalog.spi.IndexVisitor;
import io.quarkus.platform.descriptor.QuarkusPlatformDescriptor;

import static java.util.stream.Collectors.toList;

public class MemoryExtensionCatalog implements ExtensionCatalog, IndexVisitor {

    private final Map<String, List<Extension>> extensionsByCoreVersion = new TreeMap<>();
    private final List<QuarkusPlatformDescriptor> platforms = new ArrayList<>();

    // findById methods
    @Override
    public Optional<Extension> findByExtensionId(String id) {
        return extensionsByCoreVersion.values()
                .stream()
                .flatMap(Collection::stream)
                .filter(ext -> ext.gav().equals(id))
                .findFirst();
    }

    @Override
    public Set<String> getQuarkusCoreVersions() {
        return extensionsByCoreVersion.keySet();
    }

    @Override
    public List<QuarkusPlatformDescriptor> getPlatformsForExtension(Extension extension) {
        return platforms.stream()
                .filter(pl -> pl.getExtensions().contains(extension))
                .collect(toList());
    }

    @Override
    public List<Extension> getExtensionsByCoreVersion(String version) {
        return extensionsByCoreVersion.get(version);
    }

    @Override
    public void visitPlatform(QuarkusPlatformDescriptor platform) {
        String quarkusCore = platform.getQuarkusVersion();
        platforms.add(platform);
        extensionsByCoreVersion.computeIfAbsent(quarkusCore, k -> new ArrayList<>()).addAll(platform.getExtensions());
    }

    @Override
    public void visitExtension(Extension descriptor, String quarkusCore) {
        extensionsByCoreVersion.computeIfAbsent(quarkusCore, k -> new ArrayList<>()).add(descriptor);
    }
}