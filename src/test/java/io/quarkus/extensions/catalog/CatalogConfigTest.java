package io.quarkus.extensions.catalog;

import javax.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class CatalogConfigTest {

    @Inject
    CatalogConfig config;

    @Test
    public void configShouldBeParsed() {
        assertThat(config.config).isNotEmpty();
    }
}
