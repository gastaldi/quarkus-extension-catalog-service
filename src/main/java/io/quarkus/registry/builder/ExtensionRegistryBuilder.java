package io.quarkus.registry.builder;

import java.io.IOException;

import io.quarkus.platform.descriptor.QuarkusPlatformDescriptor;
import io.quarkus.registry.ExtensionRegistry;
import io.quarkus.registry.catalog.model.Extension;
import io.quarkus.registry.catalog.model.Platform;
import io.quarkus.registry.catalog.spi.ArtifactResolver;
import io.quarkus.registry.impl.DefaultExtensionRegistry;
import io.quarkus.registry.model.Registry;
import io.quarkus.registry.model.Release;

/**
 * Builds an {@link ExtensionRegistry} given the platforms and extensions resolved by the
 * {@link ArtifactResolver}
 */
public class ExtensionRegistryBuilder {

    private final ArtifactResolver artifactResolver;
    private final RegistryModelBuilder registryModelBuilder = new RegistryModelBuilder();

    public ExtensionRegistryBuilder(ArtifactResolver artifactResolver) {
        this.artifactResolver = artifactResolver;
    }

    public ExtensionRegistryBuilder addPlatform(String groupId, String artifactId, String version) throws IOException {
        Platform platform = Platform.builder().groupId(groupId).artifactId(artifactId).build();
        Release release = Release.builder().version(version).build();
        QuarkusPlatformDescriptor descriptor = artifactResolver.resolvePlatform(platform, release);
        registryModelBuilder.visitPlatform(descriptor);
        return this;
    }

    public ExtensionRegistryBuilder addExtension(String groupId, String artifactId, String version, String quarkusCore) throws IOException {
        Extension extension = Extension.builder().groupId(groupId).artifactId(artifactId).build();
        Release release = Release.builder().version(version).build();
        io.quarkus.dependencies.Extension ext = artifactResolver.resolveExtension(extension, release);
        registryModelBuilder.visitExtension(ext, quarkusCore);
        return this;
    }

    public ExtensionRegistry build() {
        Registry registry = registryModelBuilder.build();
        return new DefaultExtensionRegistry(registry);
    }

}