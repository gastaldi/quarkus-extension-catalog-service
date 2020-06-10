package io.quarkus.registry.memory;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import io.quarkus.bootstrap.model.AppArtifactCoords;
import io.quarkus.bootstrap.model.AppArtifactKey;
import io.quarkus.dependencies.Extension;
import io.quarkus.registry.ExtensionRegistry;
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
            for (Release release : ext.getReleases()) {
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
                .filter(extension -> extension.getId().equals(id.getKey()))
                .filter(extension -> extension.getReleases().stream()
                        .anyMatch(release -> release.getVersion().equals(id.getVersion())))
                .map(extension -> toQuarkusExtension(extension, id.getVersion()))
                .findFirst();
    }

    @Override
    public Set<Extension> list(String quarkusCore, String keyword) {
        Set<Extension> result = new LinkedHashSet<>();
        final Pattern searchPattern = Pattern.compile(".*" + keyword + ".*", Pattern.CASE_INSENSITIVE);
        for (io.quarkus.registry.model.Extension extension : registry.getExtensions()) {
            extension.getReleases().stream()
                    .filter(release -> quarkusCore.equals(release.getQuarkusCore()))
                    .findFirst().ifPresent(release -> {
                        if (searchPattern.matcher(extension.getName()).matches()) {
                            result.add(toQuarkusExtension(extension, release.getVersion()));
                        }
            });
        }
        return result;
    }

    @Override
    public LookupResult lookup(LookupParameters parameters) {

        return null;
    }

    private Extension toQuarkusExtension(io.quarkus.registry.model.Extension ext, String release) {
        AppArtifactKey id = ext.getId();
        return new Extension()
                .setGroupId(id.getGroupId())
                .setArtifactId(id.getArtifactId())
                .setVersion(release)
                .setName(ext.getName())
                .setDescription(ext.getDescription())
                .setMetadata(ext.getMetadata());
    }


}
