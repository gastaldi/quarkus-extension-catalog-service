package io.quarkus.registry.model;

import org.immutables.value.Value;

/**
 * Used in {@link io.quarkus.registry.memory.RegistryModelBuilder}
 */
@Value.Immutable
public interface ArtifactCoordsTuple {

    ArtifactCoords getCoords();

    String getQuarkusVersion();

    static ArtifactCoordsTupleBuilder builder() {
        return new ArtifactCoordsTupleBuilder();
    }
}
