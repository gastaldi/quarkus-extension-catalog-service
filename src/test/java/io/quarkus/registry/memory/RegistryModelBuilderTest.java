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

class RegistryModelBuilderTest {

    static Registry registry;
    @BeforeAll
    static void setUp() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Repository repository = Repository.parse(Paths.get("src/test/resources/repository"), mapper);
        RepositoryIndexer indexer = new RepositoryIndexer(new DefaultArtifactResolver(mapper));
        RegistryModelBuilder builder = new RegistryModelBuilder();
        indexer.index(repository, builder);
        registry = builder.build();
    }

    @Test
    void build() throws Exception {
        assertThat(registry.getCategories()).isNotEmpty();
        assertThat(registry.getExtensions()).isNotEmpty();
        assertThat(registry.getPlatforms()).isNotEmpty();
//        ObjectMapper mapper = new ObjectMapper()
//                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
//                .setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);
//        mapper.writeValue(System.out, registry);
    }
}