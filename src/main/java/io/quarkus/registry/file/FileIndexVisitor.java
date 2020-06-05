package io.quarkus.registry.file;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicIntegerArray;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.dependencies.Extension;
import io.quarkus.registry.catalog.spi.IndexVisitor;
import io.quarkus.platform.descriptor.QuarkusPlatformDescriptor;

/***
 * Dumps the platforms and extensions in a directory structure
 */
public class FileIndexVisitor implements IndexVisitor {

    private static final int EXTENSION_INDEX = 0;
    private static final int PLATFORM_INDEX = 1;

    private final Path root;
    private final ObjectMapper objectMapper;
    private final AtomicIntegerArray counter = new AtomicIntegerArray(2);

    public FileIndexVisitor(Path root, ObjectMapper objectMapper) {
        this.root = root;
        this.objectMapper = objectMapper;
    }

    @Override
    public void visitPlatform(QuarkusPlatformDescriptor platform) {
        String fileName = String.format("%03d-%s-%s.json",
                                        counter.addAndGet(PLATFORM_INDEX, 1),
                                        platform.getBomArtifactId(),
                                        platform.getBomVersion());
        Path path = root.resolve("platforms").resolve(fileName);
        try {
            Files.createDirectories(path.getParent());
            objectMapper.writeValue(path.toFile(), platform);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void visitExtension(Extension extension, String quarkusCore) {
        String fileName = String.format("%03d-%s-%s.json",
                                        counter.addAndGet(EXTENSION_INDEX, 1),
                                        extension.getArtifactId(),
                                        extension.getVersion());
        Path path = root.resolve("extensions").resolve(fileName);
        try {
            Files.createDirectories(path.getParent());
            objectMapper.writeValue(path.toFile(), extension);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}