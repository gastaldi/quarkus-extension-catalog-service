package io.quarkus.extensions.catalog.model;

import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.immutables.value.Value.Immutable;

/**
 * A {@link Catalog} holds a set of extensions
 */
@Immutable
@Value.Style(jdkOnly = true)
@JsonSerialize(as = ImmutableCatalog.class)
@JsonDeserialize(as = ImmutableCatalog.class)
public interface Catalog {
    Set<Extension> getExtensions();
}
