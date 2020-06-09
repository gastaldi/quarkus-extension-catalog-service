package io.quarkus.registry.memory;

import java.io.IOException;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.bootstrap.model.AppArtifactKey;
import io.quarkus.registry.DefaultArtifactResolver;
import io.quarkus.registry.ExtensionRegistry;
import io.quarkus.registry.LookupParametersBuilder;
import io.quarkus.registry.RepositoryIndexer;
import io.quarkus.registry.catalog.model.Repository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CatalogExtensionRegistryTest {

    static CatalogExtensionRegistry catalog = new CatalogExtensionRegistry();

    @BeforeAll
    static void setUp() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Repository repository = Repository.parse(Paths.get("src/test/resources/repository"), mapper);
        RepositoryIndexer indexer = new RepositoryIndexer(new DefaultArtifactResolver(mapper));
        indexer.index(repository, CatalogExtensionRegistryTest.catalog);
    }

    @Test
    void shouldReturnFourQuarkusCoreVersions() {
        assertThat(catalog.getQuarkusCoreVersions()).containsExactly("1.1.0.CR1", "1.3.1.Final", "1.3.2.Final", "1.4.0.CR1");
    }

    @Test
    void shouldReturnIndividualExtension() {
        assertThat(catalog.findByExtensionId("org.apache.myfaces.core.extensions.quarkus:myfaces-quarkus-runtime:2.3-next-M2")).isNotEmpty();
    }

    @Test
    void shouldLookupPlatformForDependentExtensionInQuarkusFinal() {
        ExtensionRegistry.LookupResult result = catalog.lookup(
                new LookupParametersBuilder().quarkusCore("1.3.1.Final").addExtensions(
                        AppArtifactKey.fromString("io.quarkus:quarkus-resteasy"),
                        AppArtifactKey.fromString("io.quarkus:quarkus-jgit")
                ).build());
        assertThat(result).isNotNull();
        assertThat(result.getPlatforms()).hasSize(1);
        assertThat(result.getPlatforms().get(0).getBomArtifactId()).isEqualTo("quarkus-universe-bom");
        assertThat(result.getExtensionsInPlatforms()).hasSize(2);
        assertThat(result.getIndependentExtensions()).isEmpty();
    }

    @Test
    void shouldLookupPlatformForDependentExtensionInQuarkusCR() {
        ExtensionRegistry.LookupResult result = catalog.lookup(new LookupParametersBuilder().quarkusCore("1.4.0.CR1").addExtensions(
                AppArtifactKey.fromString("io.quarkus:quarkus-resteasy"),
                AppArtifactKey.fromString("io.quarkus:quarkus-jgit")
        ).build());
        assertThat(result).isNotNull();
        assertThat(result.getPlatforms()).hasSize(1);
        assertThat(result.getPlatforms().get(0).getBomArtifactId()).isEqualTo("quarkus-bom");
    }

    @Test
    void shouldLookupNoPlatformForIndependentExtension() {
        ExtensionRegistry.LookupResult result = catalog.lookup(
                new LookupParametersBuilder().quarkusCore("1.3.1.Final").addExtensions(
                        AppArtifactKey.fromString("org.apache.myfaces.core.extensions.quarkus:myfaces-quarkus-runtime")
                ).build());
        assertThat(result).isNotNull();
        assertThat(result.getPlatforms()).isEmpty();
        assertThat(result.getExtensionsInPlatforms()).isEmpty();
        assertThat(result.getIndependentExtensions()).hasSize(1);
        assertThat(result.getIndependentExtensions().get(0))
                .hasFieldOrPropertyWithValue("groupId", "org.apache.myfaces.core.extensions.quarkus")
                .hasFieldOrPropertyWithValue("artifactId", "myfaces-quarkus-runtime")
                .hasFieldOrPropertyWithValue("version", "2.3-next-M2");
    }
}