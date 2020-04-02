package io.quarkus.extension.catalog.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.extensions.catalog.model.Extension;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SerializationTest {

    @Test
    public void shouldSerializeExtension() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Extension extension = mapper.readValue(getClass().getResource("extension-sample.json"), Extension.class);
        assertThat(extension).isNotNull();
        assertThat(extension.getName()).isEqualTo("JavaServer Faces - MyFaces");
        assertThat(extension.getReleases()).hasSize(2);
    }
}

