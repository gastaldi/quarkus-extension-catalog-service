package io.quarkus.extensions.catalog.model;

import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Immutable;

/**
 * A {@link Platform} holds a set of extensions
 */
@Immutable
@JsonSerialize(as = ImmutablePlatform.class)
@JsonDeserialize(as = ImmutablePlatform.class)
public interface Platform {

    Set<Extension> getExtensions();
}
