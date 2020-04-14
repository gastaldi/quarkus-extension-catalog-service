package io.quarkus.extension.catalog.neo4j.nodes;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class QuarkusCore {
    @Id
    public String version;
}
