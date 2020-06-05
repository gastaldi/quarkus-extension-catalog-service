package io.quarkus.registry.memory;

import java.util.LinkedHashMap;
import java.util.Map;

import io.quarkus.dependencies.Extension;
import io.quarkus.platform.descriptor.QuarkusPlatformDescriptor;
import io.quarkus.registry.model.ArtifactCoords;
import io.quarkus.registry.model.ArtifactCoordsBuilder;
import io.quarkus.registry.model.ArtifactKey;
import io.quarkus.registry.model.ArtifactKeyBuilder;
import io.quarkus.registry.model.ExtensionBuilder;
import io.quarkus.registry.model.PlatformBuilder;
import io.quarkus.registry.model.Registry;
import io.quarkus.registry.model.RegistryBuilder;
import io.quarkus.registry.model.ReleaseBuilder;
import io.quarkus.registry.catalog.spi.IndexVisitor;

public class RegistryModelBuilder implements IndexVisitor {

    private final Map<ArtifactKey, PlatformBuilder> platforms = new LinkedHashMap<>();
    private final Map<ArtifactKey, ExtensionBuilder> extensions = new LinkedHashMap<>();

    RegistryBuilder registryBuilder = new RegistryBuilder();

    @Override
    public void visitPlatform(QuarkusPlatformDescriptor platform) {
        registryBuilder.addVersions(platform.getQuarkusVersion());
        registryBuilder.addAllCategories(platform.getCategories());

        ArtifactKey platformKey = new ArtifactKeyBuilder().groupArtifactId(platform.getBomGroupId() + ":" + platform.getBomArtifactId())
                .build();

        PlatformBuilder platformBuilder = platforms.computeIfAbsent(platformKey, key ->
                new PlatformBuilder().id(key));

        platformBuilder.addReleases(new ReleaseBuilder().version(platform.getBomVersion())
                                            .quarkusCore(platform.getQuarkusVersion())
                                            .build());
        ArtifactCoords platformCoords = new ArtifactCoordsBuilder().id(platformKey)
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
        registryBuilder.addVersions(quarkusCore);
        ArtifactKey extensionKey = new ArtifactKeyBuilder().groupArtifactId(extension.getGroupId() + ":" + extension.getArtifactId())
                .build();
        ExtensionBuilder extensionBuilder = extensions.computeIfAbsent(extensionKey, key ->
                new ExtensionBuilder()
                        .id(key)
                        .extension(extension)
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