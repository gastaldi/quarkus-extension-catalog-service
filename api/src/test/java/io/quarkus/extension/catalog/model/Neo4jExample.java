package io.quarkus.extension.catalog.model;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

import io.quarkus.bootstrap.model.AppArtifactKey;
import io.quarkus.extensions.catalog.nodes.Extension;
import io.quarkus.extensions.catalog.nodes.Platform;
import io.quarkus.extensions.catalog.summary.ExtensionCatalog;
import io.quarkus.extensions.catalog.summary.ExtensionRelease;
import io.quarkus.extensions.catalog.summary.ExtensionSummary;
import io.quarkus.extensions.catalog.summary.PlatformRelease;
import io.quarkus.extensions.catalog.summary.PlatformSummary;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.ogm.drivers.bolt.driver.BoltDriver;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.ogm.transaction.Transaction;

public class Neo4jExample {
    public static void main(String[] args) throws Exception {
        ExtensionCatalog catalog = null;
        try (FileInputStream fis = new FileInputStream("/tmp/foo.obj");
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            catalog = (ExtensionCatalog) ois.readObject();
        }
        try (Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "password"))) {
            SessionFactory sessionFactory = new SessionFactory(new BoltDriver(driver), "io.quarkus.extensions.catalog.nodes");
            Session session = sessionFactory.openSession();
            try (Transaction transaction = session.beginTransaction()) {
                for (ExtensionSummary extensionSummary : catalog.getExtensionSummaries()) {
                    for (ExtensionRelease release : extensionSummary.getReleases()) {
                        io.quarkus.extensions.catalog.nodes.QuarkusCore quarkusCore = new io.quarkus.extensions.catalog.nodes.QuarkusCore();
                        quarkusCore.version = release.getQuarkusCores().iterator().next();
                        Extension extension = new Extension(extensionSummary.getGroupId(),
                                                            extensionSummary.getArtifactId(),
                                                            release.getVersion(),
                                                            quarkusCore);
                        session.save(extension);

                        for (PlatformRelease platformRelease : release.getPlatforms()) {
                            PlatformSummary summary = platformRelease.getSummary();
                            AppArtifactKey id = summary.getId();
                            Platform platform = new Platform(id.getGroupId(), id.getArtifactId(), platformRelease.getVersion());
                            platform.add(extension);
                            session.save(platform);
                        }
                    }
                }

                transaction.commit();
            }
            System.out.println("ACABOU");
//            List<Record> list = session.drun("MATCH (ee:Person) RETURN ee").list();
//                System.out.println(list.get(0).get("ee").asNode().asMap());
//            try ( Session session = driver.session() )
//            {
//                String greeting = session.writeTransaction(tx -> {
//                    Result result = tx.run("CREATE (a:Greeting) " +
//                                                    "SET a.message = $message " +
//                                                    "RETURN a.message + ', from node ' + id(a)",
//                                           parameters( "message", "Hello World" ) );
//                    return result.single().get( 0 ).asString();
//                });
//                System.out.println( greeting );
//            }
        }
    }
}
