package io.quarkus.extensions.catalog.model;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value.Immutable;

@Immutable
@JsonDeserialize(builder = RepositoryBuilder.class)
public abstract class Repository {
    public abstract List<Extension> getIndividualExtensions();
    public abstract List<Platform> getPlatforms();

    /**
     * Match all files ending with '.yaml'
     */
    public static Repository parse(Path rootPath, ObjectMapper mapper) {
        return new RepositoryBuilder()
                .addAllPlatforms(parse(rootPath.resolve("platforms"), Platform.class, mapper))
                .addAllIndividualExtensions(parse(rootPath.resolve("extensions"), Extension.class, mapper))
                .build();
    }

    private static <T> Set<T> parse(Path root, Class<? extends T> type, ObjectMapper objectMapper) {
        final Set<T> result = new HashSet<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(root, "**/*.json")) {
            for (Path path : stream) {
                result.add(objectMapper.readValue(path.toFile(), type));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return result;
    }

}
