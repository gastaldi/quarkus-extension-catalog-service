package io.quarkus.extensions.catalog.memory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.bootstrap.model.AppArtifactKey;
import io.quarkus.extensions.catalog.DefaultArtifactResolver;
import io.quarkus.extensions.catalog.ExtensionRegistry;
import io.quarkus.extensions.catalog.LookupParametersBuilder;
import io.quarkus.extensions.catalog.RepositoryIndexer;
import io.quarkus.extensions.catalog.model.Repository;
import org.assertj.core.util.Files;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MemoryExtensionRegistryTest {

    static MemoryExtensionRegistry catalog = new MemoryExtensionRegistry();

    @BeforeAll
    static void setUp() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Repository repository = Repository.parse(Paths.get("src/test/resources/repository"), mapper);
        RepositoryIndexer indexer = new RepositoryIndexer(new DefaultArtifactResolver(mapper));
        indexer.index(repository, catalog);
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

    @Test
    public void shouldSerializeAndDeserialize() throws Exception {
        File file = Files.newTemporaryFile();
        catalog.serialize(file.toPath());
        assertThat(file).isNotEmpty();
        MemoryExtensionRegistry restored = MemoryExtensionRegistry.deserialize(file.toPath());
        assertThat(restored.getQuarkusCoreVersions()).isEqualTo(catalog.getQuarkusCoreVersions());
    }
}