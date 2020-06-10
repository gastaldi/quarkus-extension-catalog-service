package io.quarkus.registry.memory;

import java.io.Serializable;
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

import io.quarkus.bootstrap.model.AppArtifactCoords;
import io.quarkus.bootstrap.model.AppArtifactKey;
import io.quarkus.dependencies.Extension;
import io.quarkus.platform.descriptor.QuarkusPlatformDescriptor;
import io.quarkus.registry.ExtensionRegistry;
import io.quarkus.registry.LookupResultBuilder;
import io.quarkus.registry.catalog.spi.IndexVisitor;

import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

/**
 * Builds a Registry while extension catalog is being indexed.
 * This may be costly if indexed every time.
 *
 * @see DefaultExtensionRegistry
 */
public class CatalogExtensionRegistry implements ExtensionRegistry, IndexVisitor, Serializable {

    private final Map<String, Set<Extension>> extensionsByCoreVersion = new TreeMap<>();

    private final Set<QuarkusPlatformDescriptor> platforms = new LinkedHashSet<>();

    @Override
    public Set<String> getQuarkusCoreVersions() {
        return extensionsByCoreVersion.keySet().stream().sorted(reverseOrder()).collect(toCollection(LinkedHashSet::new));
    }

    // findById methods
    @Override
    public Optional<Extension> findByExtensionId(AppArtifactCoords id) {
        String key = String.format("%s:%s:%s", id.getGroupId(), id.getArtifactId(), id.getVersion());
        return extensionsByCoreVersion.values()
                .stream()
                .flatMap(Collection::stream)
                .filter(ext -> ext.gav().equals(key))
                .findFirst();
    }

    @Override
    public Set<Extension> getExtensionsByCoreVersion(String version) {
        Set<Extension> set = extensionsByCoreVersion.get(version);
        return set == null ? Collections.emptySet() : Collections.unmodifiableSet(set);
    }

    @Override
    public LookupResult lookup(LookupParameters parameters) {
        String quarkusCore = parameters.getQuarkusCore();
        Collection<AppArtifactKey> extensions = parameters.getExtensions();
        LookupResultBuilder builder = new LookupResultBuilder();
        List<Extension> extensionList = extensionsByCoreVersion.getOrDefault(quarkusCore, Collections.emptySet())
                .stream()
                .filter(ext -> extensions.contains(AppArtifactKey.fromString(ext.managementKey())))
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
                .addAllPlatforms(extensionPlatformMap.values().stream().map(descriptor -> new AppArtifactCoords(descriptor.getBomGroupId(), descriptor.getBomArtifactId(), descriptor.getBomVersion())).collect(toList()))
                .addAllIndependentExtensions(extensionList)
                .build();
    }

    @Override
    public Set<Extension> list(String quarkusCore, String query) {
        return Collections.emptySet();
    }

    @Override
    public void visitPlatform(QuarkusPlatformDescriptor platform) {
        String quarkusCore = platform.getQuarkusVersion();
        platforms.add(platform);

        Set<Extension> extensions = extensionsByCoreVersion.computeIfAbsent(quarkusCore, k -> new HashSet<>());
        platform.getExtensions().stream().filter(extension -> !extension.isUnlisted()).forEach(extensions::add);
    }

    @Override
    public void visitExtension(Extension descriptor, String quarkusCore) {
        if (!descriptor.isUnlisted()) {
            extensionsByCoreVersion.computeIfAbsent(quarkusCore, k -> new HashSet<>()).add(descriptor);
        }
    }
}