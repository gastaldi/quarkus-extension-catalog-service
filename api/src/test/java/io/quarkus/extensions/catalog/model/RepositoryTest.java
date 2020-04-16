package io.quarkus.extensions.catalog.model;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RepositoryTest {

    @Test
    void shouldParseRepository() throws Exception {
        Repository repository = Repository.parse(Paths.get("src/test/resources/repository"), new ObjectMapper());
        assertThat(repository).isNotNull();
        assertThat(repository.getPlatforms()).isNotEmpty();
        assertThat(repository.getIndividualExtensions()).isNotEmpty();
    }
}