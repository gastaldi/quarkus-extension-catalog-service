package io.quarkus.extensions.catalog.memory;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.quarkus.bootstrap.model.AppArtifactKey;
import io.quarkus.dependencies.Extension;
import io.quarkus.extensions.catalog.ExtensionRegistry;
import io.quarkus.extensions.catalog.LookupResultBuilder;
import io.quarkus.extensions.catalog.spi.IndexVisitor;
import io.quarkus.platform.descriptor.QuarkusPlatformDescriptor;
import io.quarkus.platform.descriptor.loader.json.impl.QuarkusJsonPlatformDescriptor;

import static java.util.stream.Collectors.toList;

/**
 * Stores the indexed extension catalog in memory
 */
public class MemoryExtensionRegistry implements ExtensionRegistry, IndexVisitor, Serializable {

    @JsonProperty("extensions")
    private final Map<String, Set<Extension>> extensionsByCoreVersion = new TreeMap<>();

    @JsonProperty("platforms")
    @JsonDeserialize(contentAs = QuarkusJsonPlatformDescriptor.class)
    private final Set<QuarkusPlatformDescriptor> platforms = new LinkedHashSet<>();

    @Override
    @JsonIgnore
    public Set<String> getQuarkusCoreVersions() {
        return Collections.unmodifiableSet(extensionsByCoreVersion.keySet());
    }

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