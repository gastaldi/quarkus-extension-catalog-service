package io.quarkus.extensions.catalog.memory;

import java.io.IOException;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.extensions.catalog.DefaultArtifactResolver;
import io.quarkus.extensions.catalog.RepositoryIndexer;
import io.quarkus.extensions.catalog.model.Repository;
import io.quarkus.extensions.catalog.model.registry.Registry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegistryBuilderTest {

    static Registry registry;
    @BeforeAll
    static void setUp() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Repository repository = Repository.parse(Paths.get("src/test/resources/repository"), mapper);
        RepositoryIndexer indexer = new RepositoryIndexer(new DefaultArtifactResolver(mapper));
        RegistryBuilder builder = new RegistryBuilder();
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