package io.quarkus.extension.catalog.neo4j.nodes;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Platform implements Coordinates {

    @Id
    @GeneratedValue(strategy = CoordinateIdStrategy.class)
    private String id;

    private String groupId;

    private String artifactId;

    private String version;

    @Relationship("RUNS_ON")
    private QuarkusCore quarkusCore;

    @Relationship("CONTAINS")
    private Set<Extension> extensions = new HashSet<>();

    public Platform() {
    }

    public Platform(String groupId, String artifactId, String version, QuarkusCore core) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.quarkusCore = core;
    }

    public void addExtension(Extension extension) {
        extension.addPlatform(this);
        extensions.add(extension);
    }

    @Override
    public String getGroupId() {
        return groupId;
    }

    @Override
    public String getArtifactId() {
        return artifactId;
    }

    @Override
    public String getVersion() {
        return version;
    }


}