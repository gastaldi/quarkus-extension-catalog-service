package io.quarkus.extension.catalog.memory;

import java.io.IOException;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.extensions.catalog.Indexer;
import io.quarkus.extensions.catalog.model.Repository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MemoryExtensionCatalogTest {

    static MemoryExtensionCatalog catalog = new MemoryExtensionCatalog();

    @BeforeAll
    static void setUp() throws IOException {
        Repository repository = Repository.parse(Paths.get("../playground/repository"), new ObjectMapper());
        Indexer indexer = new Indexer();
        indexer.index(repository, catalog);
    }

    @Test
    void shouldReturnTwoQuarkusCoreVersions() {
        assertThat(catalog.getQuarkusCoreVersions()).containsExactly("1.3.1.Final", "1.3.2.Final");
    }

    @Test
    void shouldReturnIndividualExtension() {
        assertThat(catalog.findByExtensionId("org.apache.myfaces.core.extensions.quarkus:myfaces-quarkus-runtime:2.4-next")).isNotEmpty();
    }
}