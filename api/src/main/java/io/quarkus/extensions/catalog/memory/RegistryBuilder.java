package io.quarkus.extensions.catalog.memory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import io.quarkus.bootstrap.model.AppArtifactCoords;
import io.quarkus.bootstrap.model.AppArtifactKey;
import io.quarkus.dependencies.Extension;
import io.quarkus.extensions.catalog.model.ReleaseBuilder;
import io.quarkus.extensions.catalog.model.registry.ExtensionBuilder;
import io.quarkus.extensions.catalog.model.registry.PlatformBuilder;
import io.quarkus.extensions.catalog.model.registry.Registry;
import io.quarkus.extensions.catalog.spi.IndexVisitor;
import io.quarkus.platform.descriptor.QuarkusPlatformDescriptor;

public class RegistryBuilder implements IndexVisitor {

    private final Map<AppArtifactKey, io.quarkus.extensions.catalog.model.registry.PlatformBuilder> platforms =
            new LinkedHashMap<>();

    private final Map<AppArtifactKey, ExtensionBuilder> extensions = new LinkedHashMap<>();

    io.quarkus.extensions.catalog.model.registry.RegistryBuilder registryBuilder =
            new io.quarkus.extensions.catalog.model.registry.RegistryBuilder();

    @Override
    public void visitPlatform(QuarkusPlatformDescriptor platform) {
        AppArtifactKey platformKey = new AppArtifactKey(platform.getBomGroupId(), platform.getBomArtifactId());
        PlatformBuilder platformBuilder = platforms.computeIfAbsent(platformKey, key ->
                new PlatformBuilder()
                        .key(key));
        platformBuilder.addReleases(new ReleaseBuilder().version(platform.getBomVersion())
                                            .quarkusCore(platform.getQuarkusVersion())
                                            .build());
        AppArtifactCoords platformCoords = new AppArtifactCoords(platform.getBomGroupId(),
                                                                 platform.getBomArtifactId(),
                                                                 platform.getBomVersion());
        for (Extension extension : platform.getExtensions()) {
            visitExtension(extension, platform.getQuarkusVersion(), platformCoords);
        }
        registryBuilder.addAllCategories(platform.getCategories());
    }

    @Override
    public void visitExtension(Extension extension, String quarkusCore) {
        visitExtension(extension, quarkusCore, null);
    }

    private void visitExtension(Extension extension, String quarkusCore, AppArtifactCoords platform) {
        // Ignore unlisted extensions
        if (extension.isUnlisted()) {
            return;
        }
        AppArtifactKey extensionKey = new AppArtifactKey(extension.getGroupId(), extension.getArtifactId());
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
