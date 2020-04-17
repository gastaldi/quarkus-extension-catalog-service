package io.quarkus.extensions.catalog;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import com.fasterxml.jackson.databind.ObjectReader;
import io.quarkus.extensions.catalog.model.Extension;
import io.quarkus.extensions.catalog.model.Platform;
import io.quarkus.extensions.catalog.model.Release;
import io.quarkus.extensions.catalog.spi.ArtifactResolver;
import io.quarkus.platform.descriptor.QuarkusPlatformDescriptor;
import io.quarkus.platform.descriptor.loader.json.impl.QuarkusJsonPlatformDescriptor;

public class DefaultArtifactResolver implements ArtifactResolver {

    private final ObjectReader objectReader;

    public DefaultArtifactResolver(ObjectReader objectReader) {
        this.objectReader = objectReader;
    }

    @Override
    public QuarkusPlatformDescriptor resolvePlatform(Platform platform, Release release) throws IOException {
        // TODO: Use Maven API to resolve JSON?
        URL url = getPlatformJSONURL(platform, release);
        return objectReader.forType(QuarkusJsonPlatformDescriptor.class).readValue(url);
    }

    @Override
    public io.quarkus.dependencies.Extension resolveExtension(Extension extension, Release release) {
        // TODO: Grab the quarkus-extension.yaml from the extension's jar
        io.quarkus.dependencies.Extension ext = new io.quarkus.dependencies.Extension(extension.getGroupId(), extension.getArtifactId(), release.getVersion());
        return ext;
    }

    static URL getPlatformJSONURL(Platform platform, Release release) {
        try {
            return new URL(MessageFormat.format("https://repo1.maven.org/maven2/{0}/{1}/{2}/{1}-{2}.json",
                                                platform.getGroupIdJson().replace('.', '/'),
                                                platform.getArtifactIdJson(),
                                                release.getVersion()));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Error while building JSON URL", e);
        }
    }

}
