package io.quarkus.extensions.catalog.model.registry;

import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.quarkus.dependencies.Category;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = RegistryBuilder.class)
public interface Registry {
    Set<Extension> getExtensions();

    Set<Platform> getPlatforms();

    Set<Category> getCategories();
}