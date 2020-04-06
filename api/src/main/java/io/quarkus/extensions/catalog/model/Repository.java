package io.quarkus.extensions.catalog.model;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value.Immutable;

@Immutable
@JsonDeserialize(builder = RepositoryBuilder.class)
public interface Repository {
    List<Extension> getIndividualExtensions();
    List<Platform> getPlatforms();
}
