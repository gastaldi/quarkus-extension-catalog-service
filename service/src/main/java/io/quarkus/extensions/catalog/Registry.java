package io.quarkus.extensions.catalog;

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
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.extensions.catalog.model.Extension;
import io.quarkus.extensions.catalog.model.ImmutablePlatform;
import io.quarkus.extensions.catalog.model.Platform;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logmanager.Logger;

@ApplicationScoped
public class Registry {

    private final Map<String, Platform> catalogs = new HashMap<>();
    private static final Logger logger = Logger.getLogger(Registry.class.getName());

    public Registry() {
        // Used for testing
    }

    /**
     * Builds a {@link Registry} object
     *
     * @param subPath must exist and be relative to the user home. Defaults to ~/.quarkus/extension-catalogs
     * @param converter the converter to use
     * @throws IOException
     */
    public Registry(Path subPath, Function<Path, Extension> converter) throws IOException {
        Path catalogPath = Paths.get(System.getProperty("user.home")).resolve(subPath);
        if (!Files.exists(catalogPath)) {
            throw new FileNotFoundException("File not found: " + catalogPath);
        }
        Files.walkFileTree(catalogPath, new SimpleFileVisitor<Path>() {

            ImmutablePlatform.Builder platformBuilder;
            String id;

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                id = dir.getFileName().toString();
                platformBuilder = ImmutablePlatform.builder();
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Extension extension = converter.apply(file);
                if (extension != null) {
                    platformBuilder.addExtensions(extension);
                }
                return super.visitFile(file, attrs);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                put(id, platformBuilder.build());
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Builds a {@link Registry} object. This is what CDI uses to create this object
     * @param subPath must exist and be relative to the user home. Defaults to ~/.quarkus/extension-catalogs
     * @param objectMapper the JSON parser
     * @throws IOException if any IO error occurs
     */
    @Inject
    public Registry(
            @ConfigProperty(name = "quarkus.catalog.path", defaultValue = ".quarkus/extension-catalogs")
                    Path subPath,
            ObjectMapper objectMapper) throws IOException {
        this(subPath, path -> {
                    if (path.toString().endsWith(".json")) {
                        try {
                            return objectMapper.readValue(path.toFile(), Extension.class);
                        } catch (IOException e) {
                            logger.log(Level.SEVERE, "Error while reading " + path, e);
                            return null;
                        }
                    }
                    return null;
        });
    }

    void put(String id, Platform catalog) {
        catalogs.put(id, catalog);
    }

    public Set<Extension> getAllExtensions() {
        return catalogs.values().stream()
                .flatMap( f-> f.getExtensions().stream())
                .collect(Collectors.toSet());
    }
}