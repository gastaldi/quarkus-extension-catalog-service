package io.quarkus.extensions.catalog.model;

import java.nio.file.Path;
import java.util.Set;

import org.immutables.value.Value.Immutable;

@Immutable
public interface Registry {

    Path getRegistryPath();

    Set<Extension> getExtensions();
}
