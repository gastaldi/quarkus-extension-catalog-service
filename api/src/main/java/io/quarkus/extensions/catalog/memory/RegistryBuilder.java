package io.quarkus.extensions.catalog.memory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import io.quarkus.bootstrap.model.AppArtifactCoords;
import io.quarkus.bootstrap.model.AppArtifactKey;
import io.quarkus.dependencies.Extension;
import io.quarkus.extensions.catalog.model.ReleaseBuilder;
import io.quarkus.extensions.catalog.model.registry.ArtifactCoords;
import io.quarkus.extensions.catalog.model.registry.ArtifactCoordsBuilder;
import io.quarkus.extensions.catalog.model.registry.ArtifactKey;
import io.quarkus.extensions.catalog.model.registry.ArtifactKeyBuilder;
import io.quarkus.extensions.catalog.model.registry.ExtensionBuilder;
import io.quarkus.extensions.catalog.model.registry.PlatformBuilder;
import io.quarkus.extensions.catalog.model.registry.Registry;
import io.quarkus.extensions.catalog.spi.IndexVisitor;
import io.quarkus.platform.descriptor.QuarkusPlatformDescriptor;

public class RegistryBuilder implements IndexVisitor {

    private final Map<ArtifactKey, io.quarkus.extensions.catalog.model.registry.PlatformBuilder> platforms =
            new LinkedHashMap<>();

    private final Map<ArtifactKey, ExtensionBuilder> extensions = new LinkedHashMap<>();

    io.quarkus.extensions.catalog.model.registry.RegistryBuilder registryBuilder =
            new io.quarkus.extensions.catalog.model.registry.RegistryBuilder();

    @Override
    public void visitPlatform(QuarkusPlatformDescriptor platform) {

        registryBuilder.addAllCategories(platform.getCategories());

        ArtifactKey platformKey = new ArtifactKeyBuilder().groupId(platform.getBomGroupId())
                .artifactId(platform.getBomArtifactId()).build();

        PlatformBuilder platformBuilder = platforms.computeIfAbsent(platformKey, key ->
                new PlatformBuilder().key(key));

        platformBuilder.addReleases(new ReleaseBuilder().version(platform.getBomVersion())
                                            .quarkusCore(platform.getQuarkusVersion())
                                            .build());
        ArtifactCoords platformCoords = new ArtifactCoordsBuilder().key(platformKey)
                .version(platform.getBomVersion()).build();

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
        ArtifactKey extensionKey = new ArtifactKeyBuilder().groupId(extension.getGroupId())
                .artifactId(extension.getArtifactId())
                .build();
        ExtensionBuilder extensionBuilder = extensions.computeIfAbsent(extensionKey, key ->
                new ExtensionBuilder()
                        .key(key)
                        .name(Objects.toString(extension.getName(), extension.getArtifactId()))
                        .description(extension.getDescription())
                        .metadata(extension.getMetadata())
        );
        extensionBuilder.addReleases(new ReleaseBuilder().version(extension.getVersion())
                                             .quarkusCore(quarkusCore)
                                             .build());
        if (platform != null) {
            extensionBuilder.addPlatforms(platform);
        }
    }

    public Registry build() {
        extensions.values().stream().map(ExtensionBuilder::build).forEach(registryBuilder::addExtensions);
        platforms.values().stream().map(PlatformBuilder::build).forEach(registryBuilder::addPlatforms);
        return registryBuilder.build();
    }
}
