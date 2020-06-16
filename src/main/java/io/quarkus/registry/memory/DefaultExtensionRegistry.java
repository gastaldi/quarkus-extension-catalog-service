package io.quarkus.registry.memory;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import io.quarkus.bootstrap.model.AppArtifactCoords;
import io.quarkus.bootstrap.model.AppArtifactKey;
import io.quarkus.dependencies.Extension;
import io.quarkus.registry.ExtensionRegistry;
import io.quarkus.registry.LookupResultBuilder;
import io.quarkus.registry.model.ArtifactCoords;
import io.quarkus.registry.model.ArtifactKey;
import io.quarkus.registry.model.Extension.ExtensionRelease;
import io.quarkus.registry.model.Registry;
import io.quarkus.registry.model.Release;

/**
 * This {@link io.quarkus.registry.ExtensionRegistry} implementation uses in-memory {@link io.quarkus.registry.model.Registry}
 * objects to query the data.
 */
public class DefaultExtensionRegistry implements ExtensionRegistry {

    private final Registry registry;

    public DefaultExtensionRegistry(Registry registry) {
        this.registry = Objects.requireNonNull(registry, "Registry cannot be null");
    }

    @Override
    public Set<String> getQuarkusCoreVersions() {
        return registry.getVersions();
    }

    @Override
    public Set<Extension> getExtensionsByCoreVersion(String version) {
        Set<Extension> result = new LinkedHashSet<>();
        for (io.quarkus.registry.model.Extension ext : registry.getExtensions()) {
            for (ExtensionRelease extensionRelease : ext.getReleases()) {
                Release release = extensionRelease.getRelease();
                if (version.equals(release.getQuarkusCore())) {
                    result.add(toQuarkusExtension(ext, release.getVersion()));
                }
            }
        }
        return result;
    }

    @Override
    public Optional<Extension> findByExtensionId(AppArtifactCoords id) {
        return registry.getExtensions().stream()
                .filter(extension -> equals(extension.getId(), id.getKey()))
                .filter(extension -> extension.getReleases().stream()
                        .anyMatch(extensionRelease -> extensionRelease.getRelease().getVersion().equals(id.getVersion())))
                .map(extension -> toQuarkusExtension(extension, id.getVersion()))
                .findFirst();
    }

    @Override
    public Set<Extension> list(String quarkusCore, String keyword) {
        Set<Extension> result = new LinkedHashSet<>();
        final Pattern searchPattern = Pattern.compile(".*" + keyword + ".*", Pattern.CASE_INSENSITIVE);
        for (io.quarkus.registry.model.Extension extension : registry.getExtensions()) {
            for (ExtensionRelease extensionRelease : extension.getReleases()) {
                if (quarkusCore.equals(extensionRelease.getRelease().getQuarkusCore())) {
                    if (searchPattern.matcher(extension.getName()).matches()) {
                        result.add(toQuarkusExtension(extension, extensionRelease.getRelease().getVersion()));
                    }
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public LookupResult lookup(LookupParameters parameters) {
        String quarkusCore = parameters.getQuarkusCore();
        List<AppArtifactKey> extensions = parameters.getExtensions();
        LookupResultBuilder builder = new LookupResultBuilder();
        for (AppArtifactKey extensionKey : extensions) {
            for (io.quarkus.registry.model.Extension extension : registry.getExtensions()) {
                if (equals(extension.getId(), extensionKey)) {
                    for (ExtensionRelease extensionRelease : extension.getReleases()) {
                        Release release = extensionRelease.getRelease();
                        if (Objects.equals(release.getQuarkusCore(), quarkusCore)) {
                            Set<ArtifactCoords> platforms = extensionRelease.getPlatforms();
                            Extension quarkusExtension = toQuarkusExtension(extension, release.getVersion());
                            if (platforms.isEmpty()) {
                                builder.addIndependentExtensions(quarkusExtension);
                            } else {
                                builder.addExtensionsInPlatforms(quarkusExtension);
                                platforms.stream()
                                        .map(coords ->
                                                     new AppArtifactCoords(
                                                             coords.getId().getGroupId(),
                                                             coords.getId().getArtifactId(),
                                                             coords.getVersion()))
                                        .forEach(builder::addPlatforms);
                            }
                            break;
                        }
                    }
                }
            }
        }
        return builder.build();
    }

    private Extension toQuarkusExtension(io.quarkus.registry.model.Extension ext, String release) {
        ArtifactKey id = ext.getId();
        return new Extension()
                .setGroupId(id.getGroupId())
                .setArtifactId(id.getArtifactId())
                .setVersion(release)
                .setName(ext.getName())
                .setDescription(ext.getDescription())
                .setMetadata(ext.getMetadata());
    }

    private boolean equals(ArtifactKey key1, AppArtifactKey key2) {
        return Objects.equals(key1.getGroupId(), key2.getGroupId())
                && Objects.equals(key1.getArtifactId(), key2.getArtifactId());
    }


}
