package io.quarkus.extensions.catalog;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.dependencies.Extension;
import io.quarkus.extensions.catalog.model.Platform;
import io.quarkus.extensions.catalog.model.PlatformBuilder;
import io.quarkus.extensions.catalog.model.Release;
import io.quarkus.extensions.catalog.model.ReleaseBuilder;
import io.quarkus.extensions.catalog.model.Repository;
import io.quarkus.extensions.catalog.spi.IndexVisitor;
import io.quarkus.platform.descriptor.QuarkusPlatformDescriptor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RepositoryIndexerTest {
    @Test
    void shouldFormatURL() {
        Release release = new ReleaseBuilder().version("1.3.1.Final").build();
        Platform platform = new PlatformBuilder()
                .groupId("io.quarkus")
                .artifactId("quarkus-universe-bom")
                .addReleases(release).build();
        URL url = RepositoryIndexer.getPlatformJSONURL(platform, release);
        assertThat(url).hasPath("/maven2/io/quarkus/quarkus-universe-bom/1.3.1.Final/quarkus-universe-bom-1.3.1.Final.json");
    }


    @Test
    void shouldParseIntoDescriptor() throws IOException {
        Release release = new ReleaseBuilder().version("1.3.1.Final").build();
        Platform platform = new PlatformBuilder()
                .groupId("io.quarkus")
                .artifactId("quarkus-universe-bom")
                .addReleases(release).build();
        RepositoryIndexer indexer = new RepositoryIndexer(new ObjectMapper());
        QuarkusPlatformDescriptor descriptor = indexer.readPlatformDescriptor(platform, release);
        assertThat(descriptor).isNotNull();
    }

    @Test
    void shouldVisitParsedElements() throws Exception {
        Path rootPath = Paths.get("src/test/resources/repository");
        assertThat(rootPath).exists();
        Repository repository = Repository.parse(rootPath, new ObjectMapper());
        RepositoryIndexer indexer = new RepositoryIndexer(new ObjectMapper());
        IndexVisitor mock = mock(IndexVisitor.class);
        indexer.index(repository, mock);
        verify(mock, atLeast(4)).visitPlatform(any(QuarkusPlatformDescriptor.class));
        verify(mock, atLeast(2)).visitExtension(any(Extension.class), anyString());
    }
}