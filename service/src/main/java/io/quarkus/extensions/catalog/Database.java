package io.quarkus.extensions.catalog;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.neo4j.driver.Driver;

@ApplicationScoped
public class Database {

    @Inject
    Driver driver;


}
