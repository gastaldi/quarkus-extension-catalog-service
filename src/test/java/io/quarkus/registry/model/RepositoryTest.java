package io.quarkus.registry.model;

import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.registry.catalog.model.Repository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RepositoryTest {

    @Test
    void shouldParseRepository() throws Exception {
        Repository repository = Repository.parse(Paths.get("src/test/resources/repository"), new ObjectMapper());
        assertThat(repository).isNotNull();
        assertThat(repository.getPlatforms()).isNotEmpty();
        assertThat(repository.getIndividualExtensions()).isNotEmpty();
    }
}