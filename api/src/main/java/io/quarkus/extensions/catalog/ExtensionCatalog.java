package io.quarkus.extensions.catalog;

import java.util.Set;

import io.quarkus.dependencies.Extension;

public interface ExtensionCatalog {

    Set<Extension> getExtensions();
}
