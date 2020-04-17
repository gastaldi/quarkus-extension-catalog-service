package io.quarkus.extensions.catalog;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import io.quarkus.extensions.catalog.model.Extension;
import io.quarkus.extensions.catalog.model.Platform;
import io.quarkus.extensions.catalog.model.Release;
import io.quarkus.extensions.catalog.model.Repository;
import io.quarkus.extensions.catalog.spi.ArtifactResolver;
import io.quarkus.extensions.catalog.spi.IndexVisitor;
import io.quarkus.platform.descriptor.QuarkusPlatformDescriptor;

/**
 * Indexes a repository
 */
public class RepositoryIndexer {

    private final ObjectReader objectReader;

    private final ArtifactResolver artifactResolver;

    public RepositoryIndexer(ObjectMapper mapper) {
        this(mapper, null);
    }

    @SuppressWarnings("deprecation")
    public RepositoryIndexer(ObjectMapper mapper, ArtifactResolver artifactResolver) {
        this.objectReader = mapper.reader()
                .withFeatures(JsonParser.Feature.ALLOW_COMMENTS, JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS)
                .with(mapper.getDeserializationConfig().with(PropertyNamingStrategy.KEBAB_CASE));
        this.artifactResolver = (artifactResolver == null) ?
                new DefaultArtifactResolver(objectReader) :
                artifactResolver;
    }

    public void index(Repository repository, IndexVisitor visitor) throws IOException {
        // Index Platforms
        for (Platform platform : repository.getPlatforms()) {
            for (Release release : platform.getReleases()) {
                QuarkusPlatformDescriptor descriptor = artifactResolver.resolvePlatform(platform, release);
                visitor.visitPlatform(descriptor);
            }
        }

        // Index extensions
        for (Extension extension : repository.getIndividualExtensions()) {
            for (Release release : extension.getReleases()) {
                io.quarkus.dependencies.Extension ext = artifactResolver.resolveExtension(extension, release);
                visitor.visitExtension(ext, release.getQuarkusCore());
            }
        }
    }
}