package io.quarkus.extensions.catalog.nodes;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class QuarkusCore {
    @Id
    public String version;
}
