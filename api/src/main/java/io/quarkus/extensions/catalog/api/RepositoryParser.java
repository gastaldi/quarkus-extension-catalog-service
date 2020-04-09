package io.quarkus.extensions.catalog.api;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.quarkus.extensions.catalog.model.Extension;
import io.quarkus.extensions.catalog.model.Platform;
import io.quarkus.extensions.catalog.model.Repository;
import io.quarkus.extensions.catalog.model.RepositoryBuilder;

public class RepositoryParser {

    /**
     * Match all files ending with '.yaml'
     */
    private static PathMatcher YAML_FILES = FileSystems.getDefault().getPathMatcher("glob:**/*.yaml");

    public static Repository parse(Path rootPath) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return new RepositoryBuilder()
                .addAllPlatforms(parse(rootPath.resolve("platforms"), Platform.class, mapper))
                .addAllIndividualExtensions(parse(rootPath.resolve("extensions"), Extension.class, mapper))
                .build();
    }

    private static <T> Set<T> parse(Path root, Class<? extends T> type, ObjectMapper objectMapper) {
        final Set<T> result = new HashSet<>();
        try {
            Files.walkFileTree(root, new SimpleFileVisitor<Path>(){
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (YAML_FILES.matches(file)) {
                        result.add(objectMapper.readValue(file.toFile(), type));
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return result;
    }
}
