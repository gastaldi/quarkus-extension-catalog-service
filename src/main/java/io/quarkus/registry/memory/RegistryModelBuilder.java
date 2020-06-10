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
import io.quarkus.registry.model.ExtensionReleaseBuilder;
import io.quarkus.registry.model.PlatformBuilder;
import io.quarkus.registry.model.Registry;
import io.quarkus.registry.model.RegistryBuilder;
import io.quarkus.registry.model.Release;
import io.quarkus.registry.model.ReleaseBuilder;

public class RegistryModelBuilder implements IndexVisitor {

    private final Map<AppArtifactKey, PlatformBuilder> platforms = new LinkedHashMap<>();

    private final Map<AppArtifactKey, ExtensionBuilder> extensions = new LinkedHashMap<>();

    private final Map<AppArtifactCoordsQuarkusVersion, ExtensionReleaseBuilder> releases = new LinkedHashMap<>();

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
        extensions.computeIfAbsent(extensionKey, key ->
                new ExtensionBuilder()
                        .id(key)
                        .name(Objects.toString(extension.getName(), extension.getArtifactId()))
                        .description(extension.getDescription())
                        .metadata(extension.getMetadata())
        );
        AppArtifactCoords coords = new AppArtifactCoords(extension.getGroupId(), extension.getArtifactId(), extension.getVersion());
        ExtensionReleaseBuilder releaseBuilder = releases.computeIfAbsent(new AppArtifactCoordsQuarkusVersion(coords, quarkusCore),
                                                                          appArtifactCoords ->
                                                                                  io.quarkus.registry.model.Extension.ExtensionRelease.builder()
                                                                                          .release(Release.builder()
                                                                                                           .version(appArtifactCoords.coords.getVersion())
                                                                                                           .quarkusCore(appArtifactCoords.quarkusVersion)
                                                                                                               .build()));
        if (platform != null) {
            releaseBuilder.addPlatforms(platform);
        }
    }

    public Registry build() {
        for (Map.Entry<AppArtifactCoordsQuarkusVersion, ExtensionReleaseBuilder> entry : releases.entrySet()) {
            AppArtifactCoordsQuarkusVersion coordsQuarkusVersion = entry.getKey();
            ExtensionReleaseBuilder extensionReleaseBuilder = entry.getValue();
            AppArtifactKey key = new AppArtifactKey(coordsQuarkusVersion.coords.getGroupId(), coordsQuarkusVersion.coords.getArtifactId());
            ExtensionBuilder extensionBuilder = extensions.get(key);
            extensionBuilder.addReleases(extensionReleaseBuilder.build());
        }
        extensions.values().stream().map(ExtensionBuilder::build).forEach(registryBuilder::addExtensions);
        platforms.values().stream().map(PlatformBuilder::build).forEach(registryBuilder::addPlatforms);
        return registryBuilder.build();
    }


    private class AppArtifactCoordsQuarkusVersion {
        final AppArtifactCoords coords;
        final String quarkusVersion;

        private AppArtifactCoordsQuarkusVersion(AppArtifactCoords coords, String quarkusVersion) {
            this.coords = coords;
            this.quarkusVersion = quarkusVersion;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AppArtifactCoordsQuarkusVersion)) return false;
            AppArtifactCoordsQuarkusVersion that = (AppArtifactCoordsQuarkusVersion) o;
            return coords.equals(that.coords) &&
                    quarkusVersion.equals(that.quarkusVersion);
        }

        @Override
        public String toString() {
            return "AppArtifactCoordsQuarkusVersion{" +
                    "coords=" + coords +
                    ", quarkusVersion='" + quarkusVersion + '\'' +
                    '}';
        }

        @Override
        public int hashCode() {
            return Objects.hash(coords, quarkusVersion);
        }
    }
}
