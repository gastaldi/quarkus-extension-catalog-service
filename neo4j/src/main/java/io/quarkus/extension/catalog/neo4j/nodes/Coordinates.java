package io.quarkus.extension.catalog.neo4j.nodes;

public interface Coordinates {
    String getGroupId();
    String getArtifactId();
    String getVersion();
}
