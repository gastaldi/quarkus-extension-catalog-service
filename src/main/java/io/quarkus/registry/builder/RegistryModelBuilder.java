package io.quarkus.registry.builder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import io.quarkus.dependencies.Extension;
import io.quarkus.platform.descriptor.QuarkusPlatformDescriptor;
import io.quarkus.registry.catalog.spi.IndexVisitor;
import io.quarkus.registry.model.ArtifactCoords;
import io.quarkus.registry.model.ArtifactKey;
import io.quarkus.registry.model.ExtensionBuilder;
import io.quarkus.registry.model.ExtensionReleaseBuilder;
import io.quarkus.registry.model.Platform;
import io.quarkus.registry.model.PlatformBuilder;
import io.quarkus.registry.model.Registry;
import io.quarkus.registry.model.RegistryBuilder;
import io.quarkus.registry.model.Release;
import org.immutables.value.Value;

public class RegistryModelBuilder implements IndexVisitor {

    private final Map<ArtifactKey, PlatformBuilder> platforms = new LinkedHashMap<>();

    private final Map<ArtifactKey, ExtensionBuilder> extensions = new LinkedHashMap<>();

    private final Map<ArtifactCoordsTuple, ExtensionReleaseBuilder> releases = new LinkedHashMap<>();

    RegistryBuilder registryBuilder = new RegistryBuilder();

    @Override
    public void visitPlatform(QuarkusPlatformDescriptor platform) {
        registryBuilder.addVersions(platform.getQuarkusVersion());
        registryBuilder.addAllCategories(platform.getCategories());

        ArtifactKey platformKey = ArtifactKey.builder()
                .groupId(platform.getBomGroupId())
                .artifactId(platform.getBomArtifactId())
                .build();
        PlatformBuilder platformBuilder = platforms.computeIfAbsent(platformKey, key -> Platform.builder().id(key));

        platformBuilder.addReleases(Release.builder().version(platform.getBomVersion())
                                            .quarkusCore(platform.getQuarkusVersion())
                                            .build());

        ArtifactCoords platformCoords = ArtifactCoords.builder()
                .id(platformKey)
                .version(platform.getBomVersion())
                .build();
        for (Extension extension : platform.getExtensions()) {
            visitExtension(extension, platform.getQuarkusVersion(), platformCoords);
        }
    }

    @Override
    public void visitExtension(Extension extension, String quarkusCore) {
        visitExtension(extension, quarkusCore, null);
    }

    private void visitExtension(Extension extension, String quarkusCore, ArtifactCoords platform) {
        // Ignore unlisted extensions
        if (extension.isUnlisted()) {
            return;
        }
        registryBuilder.addVersions(quarkusCore);
        ArtifactKey extensionKey = ArtifactKey.builder()
                .groupId(extension.getGroupId())
                .artifactId(extension.getArtifactId())
                .build();
        extensions.computeIfAbsent(extensionKey, key ->
                io.quarkus.registry.model.Extension.builder()
                        .id(extensionKey)
                        .name(Objects.toString(extension.getName(), extension.getArtifactId()))
                        .description(extension.getDescription())
                        .metadata(extension.getMetadata())
        );
        ArtifactCoords coords = ArtifactCoords.builder()
                .id(extensionKey)
                .version(extension.getVersion())
                .build();
        ArtifactCoordsTuple key = ArtifactCoordsTuple.builder().coords(coords).quarkusVersion(quarkusCore).build();
        ExtensionReleaseBuilder releaseBuilder = releases.computeIfAbsent(key,
                                                                          appArtifactCoords ->
                                                                                  io.quarkus.registry.model.Extension.ExtensionRelease.builder()
                                                                                          .release(Release.builder()
                                                                                                           .version(appArtifactCoords.getCoords().getVersion())
                                                                                                           .quarkusCore(appArtifactCoords.getQuarkusVersion())
                                                                                                           .build()));
        if (platform != null) {
            releaseBuilder.addPlatforms(platform);
        }
    }

    public Registry build() {
        for (Map.Entry<ArtifactCoordsTuple, ExtensionReleaseBuilder> entry : releases.entrySet()) {
            ArtifactCoordsTuple tuple = entry.getKey();
            ExtensionReleaseBuilder extensionReleaseBuilder = entry.getValue();
            ArtifactKey key = tuple.getCoords().getId();
            ExtensionBuilder extensionBuilder = extensions.get(key);
            extensionBuilder.addReleases(extensionReleaseBuilder.build());
        }
        extensions.values().stream().map(ExtensionBuilder::build).forEach(registryBuilder::addExtensions);
        platforms.values().stream().map(PlatformBuilder::build).forEach(registryBuilder::addPlatforms);
        return registryBuilder.build();
    }

    @Value.Immutable
    public interface ArtifactCoordsTuple {

        ArtifactCoords getCoords();

        String getQuarkusVersion();

        static ArtifactCoordsTupleBuilder builder() {
            return new ArtifactCoordsTupleBuilder();
        }
    }

}
