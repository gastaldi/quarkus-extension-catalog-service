package io.quarkus.registry;

import java.io.IOException;
import java.net.URL;

import io.quarkus.registry.model.Registry;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegistryReaderTest {

    @Test
    void shouldFailOnEmptyURLS() {
        assertThrows(IllegalStateException.class, new RegistryReader()::create);
    }

    @Test
    void shouldReadRegistry() throws IOException {
        URL url = getClass().getClassLoader().getResource("registry/registry.json");
        Registry registry = new RegistryReader().addURL(url).create();
        assertThat(registry.getVersions()).isNotEmpty();
    }
}