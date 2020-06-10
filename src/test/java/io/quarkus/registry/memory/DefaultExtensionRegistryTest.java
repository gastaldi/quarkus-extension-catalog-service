package io.quarkus.registry.memory;

import java.io.IOException;
import java.nio.file.Paths;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import io.quarkus.bootstrap.model.AppArtifactCoords;
import io.quarkus.bootstrap.model.AppArtifactKey;
import io.quarkus.registry.DefaultArtifactResolver;
import io.quarkus.registry.ExtensionRegistry;
import io.quarkus.registry.LookupParametersBuilder;
import io.quarkus.registry.RepositoryIndexer;
import io.quarkus.registry.catalog.model.Repository;
import io.quarkus.registry.model.Registry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DefaultExtensionRegistryTest {

    static DefaultExtensionRegistry extensionRegistry;

    @BeforeAll
    static void setUp() throws IOException {
        ObjectMapper mapper = new ObjectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
        Repository repository = Repository.parse(Paths.get("src/test/resources/repository"), mapper);
        RegistryModelBuilder registryModelBuilder = new RegistryModelBuilder();
        RepositoryIndexer indexer = new RepositoryIndexer(new DefaultArtifactResolver(mapper));
        indexer.index(repository, registryModelBuilder);
        Registry registry = registryModelBuilder.build();
        extensionRegistry = new DefaultExtensionRegistry(registry);
    }

    @Test
    void shouldReturnFourQuarkusCoreVersions() {
        assertThat(extensionRegistry.getQuarkusCoreVersions()).containsExactly("1.4.0.CR1", "1.3.2.Final", "1.3.1.Final", "1.1.0.CR1");
    }

    @Test
    void shouldReturnIndividualExtension() {
        assertThat(extensionRegistry.findByExtensionId(AppArtifactCoords.fromString("org.apache.myfaces.core.extensions.quarkus:myfaces-quarkus-runtime:2.3-next-M2"))).isNotEmpty();
    }

    @Test
    void shouldLookupPlatformForDependentExtensionInQuarkusFinal() {
        ExtensionRegistry.LookupResult result = extensionRegistry.lookup(
                new LookupParametersBuilder().quarkusCore("1.3.1.Final").addExtensions(
                        AppArtifactKey.fromString("io.quarkus:quarkus-resteasy"),
                        AppArtifactKey.fromString("io.quarkus:quarkus-jgit")
                ).build());
        assertThat(result).isNotNull();
        assertThat(result.getPlatforms()).hasSize(2);
        assertThat(result.getPlatforms())
                .extracting(AppArtifactCoords::getArtifactId)
                .contains("quarkus-universe-bom","quarkus-bom");
        assertThat(result.getExtensionsInPlatforms()).hasSize(2);
        assertThat(result.getIndependentExtensions()).isEmpty();
    }

    @Test
    void shouldLookupPlatformForDependentExtensionInQuarkusCR() {
        ExtensionRegistry.LookupResult result = extensionRegistry.lookup(new LookupParametersBuilder().quarkusCore("1.4.0.CR1").addExtensions(
                AppArtifactKey.fromString("io.quarkus:quarkus-resteasy"),
                AppArtifactKey.fromString("io.quarkus:quarkus-jgit")
        ).build());
        assertThat(result).isNotNull();
        assertThat(result.getPlatforms()).hasSize(1);
        assertThat(result.getPlatforms().iterator().next().getArtifactId()).isEqualTo("quarkus-bom");
    }

    @Test
    void shouldLookupNoPlatformForIndependentExtension() {
        ExtensionRegistry.LookupResult result = extensionRegistry.lookup(
                new LookupParametersBuilder().quarkusCore("1.3.1.Final").addExtensions(
                        AppArtifactKey.fromString("org.apache.myfaces.core.extensions.quarkus:myfaces-quarkus-runtime")
                ).build());
        assertThat(result).isNotNull();
        assertThat(result.getPlatforms()).isEmpty();
        assertThat(result.getExtensionsInPlatforms()).isEmpty();
        assertThat(result.getIndependentExtensions()).hasSize(1);
        assertThat(result.getIndependentExtensions().iterator().next())
                .hasFieldOrPropertyWithValue("groupId", "org.apache.myfaces.core.extensions.quarkus")
                .hasFieldOrPropertyWithValue("artifactId", "myfaces-quarkus-runtime")
                .hasFieldOrPropertyWithValue("version", "2.3-next-M2");
    }

    @Test
    void shouldLookupSinglePlatform() {
        ExtensionRegistry.LookupResult result = extensionRegistry.lookup(
                new LookupParametersBuilder().quarkusCore("1.3.1.Final").addExtensions(
                        AppArtifactKey.fromString("org.apache.camel.quarkus:camel-quarkus-xstream")
                ).build());
        assertThat(result.getPlatforms()).hasSize(1);

    }

}