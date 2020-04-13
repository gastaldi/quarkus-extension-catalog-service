package io.quarkus.extensions.catalog.nodes;

public interface Coordinates {
    String getGroupId();
    String getArtifactId();
    String getVersion();
}
