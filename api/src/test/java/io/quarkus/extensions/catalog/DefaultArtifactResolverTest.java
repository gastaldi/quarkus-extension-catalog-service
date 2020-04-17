package io.quarkus.extensions.catalog;

import java.io.IOException;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import io.quarkus.extensions.catalog.model.Platform;
import io.quarkus.extensions.catalog.model.PlatformBuilder;
import io.quarkus.extensions.catalog.model.Release;
import io.quarkus.extensions.catalog.model.ReleaseBuilder;
import io.quarkus.platform.descriptor.QuarkusPlatformDescriptor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultArtifactResolverTest {
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
    void shouldParseIntoDescriptor() throws IOException {
        Release release = new ReleaseBuilder().version("1.3.1.Final").build();
        Platform platform = new PlatformBuilder()
                .groupId("io.quarkus")
                .artifactId("quarkus-universe-bom")
                .addReleases(release).build();
        ObjectMapper mapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);
        DefaultArtifactResolver resolver = new DefaultArtifactResolver(mapper.reader());
        QuarkusPlatformDescriptor descriptor = resolver.resolvePlatform(platform, release);
        assertThat(descriptor).isNotNull();
    }

}