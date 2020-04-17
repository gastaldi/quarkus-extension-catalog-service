package io.quarkus.extensions.catalog;

import java.io.IOException;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.extensions.catalog.model.Extension;
import io.quarkus.extensions.catalog.model.ExtensionBuilder;
import io.quarkus.extensions.catalog.model.Platform;
import io.quarkus.extensions.catalog.model.PlatformBuilder;
import io.quarkus.extensions.catalog.model.Release;
import io.quarkus.extensions.catalog.model.ReleaseBuilder;
import io.quarkus.platform.descriptor.QuarkusPlatformDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultArtifactResolverTest {

    private DefaultArtifactResolver resolver;

    @BeforeEach
    void setUp() {
        ObjectMapper mapper = new ObjectMapper();
        resolver = new DefaultArtifactResolver(mapper);
    }

    @Test
    void shouldFormatURL() {
        Release release = new ReleaseBuilder().version("1.3.1.Final").build();
        Platform platform = new PlatformBuilder()
                .groupId("io.quarkus")
                .artifactId("quarkus-universe-bom")
                .addReleases(release).build();
        URL url = DefaultArtifactResolver.getPlatformJSONURL(platform, release);
        assertThat(url).hasPath("/maven2/io/quarkus/quarkus-universe-bom/1.3.1.Final/quarkus-universe-bom-1.3.1.Final.json");
    }
    
    @Test
    void shouldResolvePlatform() throws IOException {
        Release release = new ReleaseBuilder().version("1.3.1.Final").build();
        Platform platform = new PlatformBuilder()
                .groupId("io.quarkus")
                .artifactId("quarkus-universe-bom")
                .addReleases(release).build();
        QuarkusPlatformDescriptor descriptor = resolver.resolvePlatform(platform, release);
        assertThat(descriptor).isNotNull();
    }

    @Test
    void shouldResolveExtension() throws IOException {
        Release release = new ReleaseBuilder().version("1.3.1.Final").build();
        Extension extension = new ExtensionBuilder()
                .groupId("io.quarkus")
                .artifactId("quarkus-jgit")
                .addReleases(release)
                .build();
        io.quarkus.dependencies.Extension ext = resolver.resolveExtension(extension, release);
        assertThat(ext).isNotNull();
        assertThat(ext.getArtifactId()).isEqualTo("quarkus-jgit");
    }
}