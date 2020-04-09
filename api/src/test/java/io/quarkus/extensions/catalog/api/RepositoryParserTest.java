package io.quarkus.extensions.catalog.api;

import java.nio.file.Path;
import java.nio.file.Paths;

import io.quarkus.extensions.catalog.model.Repository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RepositoryParserTest {

    @Test
    public void shouldParseRepository(){
        Path repositoryPath = Paths.get("src/test/resources/repository");
        assertThat(repositoryPath).exists();
        Repository repository = RepositoryParser.parse(repositoryPath);
        assertThat(repository).isNotNull();
        assertThat(repository.getPlatforms()).hasSize(1);
        assertThat(repository.getIndividualExtensions()).hasSize(1);
    }



}