package io.quarkus.registry.memory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import io.quarkus.bootstrap.model.AppArtifactCoords;
import io.quarkus.bootstrap.model.AppArtifactKey;
import io.quarkus.dependencies.Extension;
import io.quarkus.platform.descriptor.QuarkusPlatformDescriptor;
import io.quarkus.registry.catalog.spi.IndexVisitor;
import io.quarkus.registry.model.ExtensionBuilder;
import io.quarkus.registry.model.PlatformBuilder;
import io.quarkus.registry.model.Registry;
import io.quarkus.registry.model.RegistryBuilder;
import io.quarkus.registry.model.ReleaseBuilder;

public class RegistryModelBuilder implements IndexVisitor {

    private final Map<AppArtifactKey, PlatformBuilder> platforms = new LinkedHashMap<>();
    private final Map<AppArtifactKey, ExtensionBuilder> extensions = new LinkedHashMap<>();

    RegistryBuilder registryBuilder = new RegistryBuilder();

    @Override
    public void visitPlatform(QuarkusPlatformDescriptor platform) {
        registryBuilder.addVersions(platform.getQuarkusVersion());
        registryBuilder.addAllCategories(platform.getCategories());

        AppArtifactKey platformKey = new AppArtifactKey(platform.getBomGroupId(), platform.getBomArtifactId());

        PlatformBuilder platformBuilder = platforms.computeIfAbsent(platformKey, key ->
                new PlatformBuilder().id(key));

        platformBuilder.addReleases(new ReleaseBuilder().version(platform.getBomVersion())
                                            .quarkusCore(platform.getQuarkusVersion())
                                            .build());

        AppArtifactCoords platformCoords = new AppArtifactCoords(platform.getBomGroupId(),
                                                                 platform.getBomArtifactId(),
                                                                 platform.getBomVersion());
        for (Extension extension : platform.getExtensions()) {
            visitExtension(extension, platform.getQuarkusVersion(), platformCoords);
        }
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
        registryBuilder.addVersions(quarkusCore);
        AppArtifactKey extensionKey = new AppArtifactKey(extension.getGroupId(), extension.getArtifactId());
        ExtensionBuilder extensionBuilder = extensions.computeIfAbsent(extensionKey, key ->
                new ExtensionBuilder()
                        .id(key)
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
