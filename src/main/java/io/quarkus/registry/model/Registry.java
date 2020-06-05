package io.quarkus.registry.model;

import java.util.Set;
import java.util.SortedSet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.quarkus.dependencies.Category;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = RegistryBuilder.class)
public interface Registry {

    @Value.ReverseOrder
    SortedSet<String> getVersions();

    Set<Extension> getExtensions();

    Set<Platform> getPlatforms();

    Set<Category> getCategories();

}