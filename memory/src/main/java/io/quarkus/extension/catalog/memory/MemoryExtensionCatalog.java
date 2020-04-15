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
    private final Map<String, QuarkusPlatformDescriptor> platforms = new TreeMap<>();

    // findById methods
    @Override
    public Optional<Extension> findByExtensionId(String id) {
        return extensionsByCoreVersion.values()
                .stream()
                .flatMap(Collection::stream)
                .filter(ext -> ext.gav().equals(id))
                .findFirst();
    }

    public QuarkusPlatformDescriptor getPlatform(String groupId, String artifactId, String version) {
        return platforms.get(toPlatformKey(groupId,artifactId,version));
    }

    @Override
    public Set<String> getQuarkusCoreVersions() {
        return extensionsByCoreVersion.keySet();
    }

    @Override
    public List<QuarkusPlatformDescriptor> getPlatformsForExtension(Extension extension) {
        return platforms.values().stream()
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
        String platformKey = toPlatformKey(platform.getBomGroupId(), platform.getBomArtifactId(), platform.getBomVersion());
        platforms.put(platformKey, platform);
        extensionsByCoreVersion.computeIfAbsent(quarkusCore, k -> new ArrayList<>()).addAll(platform.getExtensions());
    }

    @Override
    public void visitExtension(Extension descriptor, String quarkusCore) {
        extensionsByCoreVersion.computeIfAbsent(quarkusCore, k -> new ArrayList<>()).add(descriptor);
    }

    private String toPlatformKey(String groupId, String artifactId, String version) {
        return String.format("%s:%s:%s", groupId, artifactId, version);
    }
}