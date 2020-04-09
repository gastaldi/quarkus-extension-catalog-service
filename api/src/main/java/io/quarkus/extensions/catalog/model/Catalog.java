package io.quarkus.extensions.catalog.model;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value.Immutable;

@Immutable
@JsonDeserialize(as = CatalogBuilder.class)
public interface Catalog {
    List<Repository> getRepositories();}
