package io.quarkus.extensions.catalog.nodes;

import org.neo4j.ogm.id.IdStrategy;

public class CoordinateIdStrategy implements IdStrategy {

    @Override
    public Object generateId(Object entity) {
        Coordinates c = (Coordinates) entity;
        return c.getGroupId() + ":" + c.getArtifactId() + ":" + c.getVersion();
    }
}
