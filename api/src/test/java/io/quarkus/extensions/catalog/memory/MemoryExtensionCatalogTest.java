package io.quarkus.extensions.catalog.memory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.bootstrap.model.AppArtifactKey;
import io.quarkus.extensions.catalog.ExtensionCatalog;
import io.quarkus.extensions.catalog.Indexer;
import io.quarkus.extensions.catalog.memory.MemoryExtensionCatalog;
import io.quarkus.extensions.catalog.model.Repository;
import io.quarkus.platform.descriptor.QuarkusPlatformDescriptor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MemoryExtensionCatalogTest {

    static MemoryExtensionCatalog catalog = new MemoryExtensionCatalog();

    @BeforeAll
    static void setUp() throws IOException {
        Repository repository = Repository.parse(Paths.get("src/test/resources/repository"), new ObjectMapper());
        Indexer indexer = new Indexer(new ObjectMapper());
        indexer.index(repository, catalog);
    }

    @Test
    void shouldReturnTwoQuarkusCoreVersions() {
        assertThat(catalog.getQuarkusCoreVersions()).containsExactly("1.3.1.Final", "1.3.2.Final", "1.4.0.CR1");
    }

    @Test
    void shouldReturnIndividualExtension() {
        assertThat(catalog.findByExtensionId("org.apache.myfaces.core.extensions.quarkus:myfaces-quarkus-runtime:2.4-next")).isNotEmpty();
    }

    @Test
    void shouldLookupPlatformForDependentExtensionInQuarkusFinal() {
        ExtensionCatalog.LookupResult result = catalog.lookup("1.3.1.Final", Arrays.asList(
                AppArtifactKey.fromString("io.quarkus:quarkus-resteasy"),
                AppArtifactKey.fromString("io.quarkus:quarkus-jgit")
        ));
        assertThat(result).isNotNull();
        assertThat(result.getPlatforms()).hasSize(1);
        assertThat(result.getPlatforms().get(0).getBomArtifactId()).isEqualTo("quarkus-universe-bom");
        assertThat(result.getExtensionsInPlatforms()).hasSize(2);
        assertThat(result.getIndependentExtensions()).isEmpty();
    }

    @Test
    void shouldLookupPlatformForDependentExtensionInQuarkusCR() {
        ExtensionCatalog.LookupResult result = catalog.lookup("1.4.0.CR1", Arrays.asList(
                AppArtifactKey.fromString("io.quarkus:quarkus-resteasy"),
                AppArtifactKey.fromString("io.quarkus:quarkus-jgit")
        ));
        assertThat(result).isNotNull();
        assertThat(result.getPlatforms()).hasSize(1);
        assertThat(result.getPlatforms().get(0).getBomArtifactId()).isEqualTo("quarkus-bom");
    }

    @Test
    void shouldLookupNoPlatformForIndependentExtension() {
        ExtensionCatalog.LookupResult result = catalog.lookup("1.3.1.Final", Arrays.asList(
                AppArtifactKey.fromString("org.apache.myfaces.core.extensions.quarkus:myfaces-quarkus-runtime")
        ));
        assertThat(result).isNotNull();
        assertThat(result.getPlatforms()).isEmpty();
        assertThat(result.getExtensionsInPlatforms()).isEmpty();
        assertThat(result.getIndependentExtensions()).hasSize(1);
        assertThat(result.getIndependentExtensions().get(0))
                .hasFieldOrPropertyWithValue("groupId", "org.apache.myfaces.core.extensions.quarkus")
                .hasFieldOrPropertyWithValue("artifactId", "myfaces-quarkus-runtime")
                .hasFieldOrPropertyWithValue("version", "2.3-next");
    }

}