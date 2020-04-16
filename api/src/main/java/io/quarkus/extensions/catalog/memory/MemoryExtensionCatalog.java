package io.quarkus.extensions.catalog.memory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import io.quarkus.dependencies.Extension;
import io.quarkus.extensions.catalog.ExtensionCatalog;
import io.quarkus.extensions.catalog.LookupResultBuilder;
import io.quarkus.extensions.catalog.spi.IndexVisitor;
import io.quarkus.platform.descriptor.QuarkusPlatformDescriptor;

import static java.util.stream.Collectors.toList;

/**
 * Stores the indexed extension catalog in memory
 */
public class MemoryExtensionCatalog implements ExtensionCatalog, IndexVisitor {

    private final Map<String, Set<Extension>> extensionsByCoreVersion = new TreeMap<>();
    private final Set<QuarkusPlatformDescriptor> platforms = new LinkedHashSet<>();

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
        return Collections.unmodifiableSet(extensionsByCoreVersion.keySet());
    }

    @Override
    public Set<Extension> getExtensionsByCoreVersion(String version) {
        return Collections.unmodifiableSet(extensionsByCoreVersion.get(version));
    }

    @Override
    public LookupResult lookup(String quarkusCore, Collection<String> extensions) {
        LookupResultBuilder builder = new LookupResultBuilder();
        List<Extension> extensionList = extensionsByCoreVersion.get(quarkusCore)
                .stream()
                .filter(ext -> extensions.contains(ext.managementKey()))
                .collect(toList());
        Map<Extension, QuarkusPlatformDescriptor> extensionPlatformMap = new HashMap<>(extensions.size());
        // For each extension, find the corresponding Platform
        for (Extension extension : extensionList) {
            for (QuarkusPlatformDescriptor platform : platforms) {
                if (platform.getExtensions().contains(extension)) {
                    extensionPlatformMap.put(extension, platform);
                    break;
                }
            }
        }
        // Remove extensions containing platforms
        extensionList.removeAll(extensionPlatformMap.keySet());
        return builder.addAllExtensionsInPlatforms(extensionPlatformMap.keySet())
                .addAllPlatforms(new HashSet<>(extensionPlatformMap.values()))
                .addAllIndependentExtensions(extensionList)
                .build();
    }

    @Override
    public void visitPlatform(QuarkusPlatformDescriptor platform) {
        String quarkusCore = platform.getQuarkusVersion();
        platforms.add(platform);

        extensionsByCoreVersion.computeIfAbsent(quarkusCore, k -> new HashSet<>()).addAll(platform.getExtensions());
    }

    @Override
    public void visitExtension(Extension descriptor, String quarkusCore) {
        extensionsByCoreVersion.computeIfAbsent(quarkusCore, k -> new HashSet<>()).add(descriptor);
    }
}