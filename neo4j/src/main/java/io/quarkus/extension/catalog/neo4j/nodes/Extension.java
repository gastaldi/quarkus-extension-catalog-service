package io.quarkus.extension.catalog.neo4j.nodes;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Extension implements Coordinates {

    @Id
    @GeneratedValue(strategy = CoordinateIdStrategy.class)
    private String id;

    private String groupId;
    private String artifactId;
    private String version;

    @Relationship("RUNS_ON")
    public QuarkusCore quarkusCore;

//    @Relationship("DEPENDS_ON")
//    Set<Extension> dependencies = new HashSet<>();
//
//    public void dependsOn(Extension extension) {
//        dependencies.add(extension);
//    }

    @Relationship(type = "CONTAINS", direction = Relationship.INCOMING)
    public List<Platform> platforms = new ArrayList<>();

    public Extension() {
    }

    public Extension(String groupId, String artifactId, String version, QuarkusCore quarkusCore) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.quarkusCore = quarkusCore;
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

    public QuarkusCore getQuarkusCore() {
        return quarkusCore;
    }

    public void addPlatform(Platform platform) {
        platforms.add(platform);
    }
}
