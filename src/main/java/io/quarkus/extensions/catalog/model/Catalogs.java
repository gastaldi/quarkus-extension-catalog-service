package io.quarkus.extensions.catalog.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class Catalogs {

    private final Map<String, Catalog> catalogs = new HashMap<>();

    public Catalogs() {
        // Used for testing
    }

    /**
     * Builds a {@link Catalogs} object.
     * @param subPath must exist and be relative to the user home. Defaults to ~/.quarkus/extension-catalogs
     * @param objectMapper the JSON parser
     * @throws IOException if any IO error occurs
     */
    @Inject
    public Catalogs(
            @ConfigProperty(name = "quarkus.catalog.path", defaultValue = ".quarkus/extension-catalogs")
            Path subPath,
            ObjectMapper objectMapper) throws IOException {
        Path catalogPath = Paths.get(System.getProperty("user.home")).resolve(subPath);
        if (!Files.exists(catalogPath)) {
            throw new FileNotFoundException("File not found: " + catalogPath);
        }
        Files.walkFileTree(catalogPath, new SimpleFileVisitor<Path>() {

            ImmutableCatalog.Builder catalogBuilder;
            String id;

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                id = dir.getFileName().toString();
                catalogBuilder = ImmutableCatalog.builder();
                return FileVisitResult.CONTINUE;
            }

            @Override public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".json")) {
                    Extension extension = objectMapper.readValue(file.toFile(), Extension.class);
                    catalogBuilder.addExtensions(extension);
                }
                return super.visitFile(file, attrs);
            }

            @Override public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                put(id, catalogBuilder.build());
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public void put(String id, Catalog catalog) {
        catalogs.put(id, catalog);
    }

    public Set<Extension> getAllExtensions() {
        return catalogs.values().stream()
                .flatMap( f-> f.getExtensions().stream())
                .collect(Collectors.toSet());
    }
}