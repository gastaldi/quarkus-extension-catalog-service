package io.quarkus.registry.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.quarkus.platform.descriptor.QuarkusPlatformDescriptor;
import io.quarkus.platform.descriptor.loader.json.impl.QuarkusJsonPlatformDescriptor;
import io.quarkus.registry.catalog.model.Extension;
import io.quarkus.registry.catalog.model.Platform;
import io.quarkus.registry.catalog.spi.ArtifactResolver;
import io.quarkus.registry.model.Release;

public class DefaultArtifactResolver implements ArtifactResolver {

    private final ObjectReader jsonReader;
    private final ObjectReader yamlReader;
    private static final String MAVEN_CENTRAL = "https://repo1.maven.org/maven2/";

    public DefaultArtifactResolver(ObjectMapper mapper) {
        this.jsonReader = mapper.reader()
                .with(mapper.getDeserializationConfig().with(PropertyNamingStrategy.KEBAB_CASE));
        this.yamlReader = mapper.reader()
                .with(new YAMLFactory())
                .with(mapper.getDeserializationConfig().with(PropertyNamingStrategy.KEBAB_CASE));
    }

    @Override
    public QuarkusPlatformDescriptor resolvePlatform(Platform platform, Release release) throws IOException {
        // TODO: Use Maven API to resolve the JSON?
        URL url = getPlatformJSONURL(platform, release);
        return jsonReader.forType(QuarkusJsonPlatformDescriptor.class).readValue(url);
    }

    @Override
    public io.quarkus.dependencies.Extension resolveExtension(Extension extension, Release release) throws IOException {
        URL extensionJarURL = getExtensionJarURL(extension, release);
        try {
            return yamlReader.forType(io.quarkus.dependencies.Extension.class).readValue(extensionJarURL);
        } catch (FileNotFoundException e) {
            // META-INF/quarkus-extension.yaml does not exist in JAR
            return new io.quarkus.dependencies.Extension(extension.getGroupId(), extension.getArtifactId(), release.getVersion());
        }
    }

    static URL getPlatformJSONURL(Platform platform, Release release) {
        try {
            return new URL(MessageFormat.format("{0}{1}/{2}/{3}/{2}-{3}.json",
                                                Objects.toString(release.getRepositoryURL(), MAVEN_CENTRAL),
                                                platform.getGroupIdJson().replace('.', '/'),
                                                platform.getArtifactIdJson(),
                                                release.getVersion()));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Error while building JSON URL", e);
        }
    }

    static URL getExtensionJarURL(Extension extension, Release release) {
        try {
            return new URL(MessageFormat.format("jar:{0}{1}/{2}/{3}/{2}-{3}.jar!/META-INF/quarkus-extension.yaml",
                                                Objects.toString(release.getRepositoryURL(), MAVEN_CENTRAL),
                                                extension.getGroupId().replace('.', '/'),
                                                extension.getArtifactId(),
                                                release.getVersion()));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Error while building JSON URL", e);
        }
    }
}
