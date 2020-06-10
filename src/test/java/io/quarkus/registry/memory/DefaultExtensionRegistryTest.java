package io.quarkus.registry.memory;

import java.io.IOException;
import java.nio.file.Paths;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import io.quarkus.registry.DefaultArtifactResolver;
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
}