package io.quarkus.extensions.catalog.model;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.extensions.catalog.model.Extension;
import io.quarkus.extensions.catalog.model.Platform;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SerializationTest {

    @Test
    public void shouldSerializeExtension() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Extension extension = mapper.readValue(new File("src/test/resources/repository/extensions/jsf.json"), Extension.class);
        assertThat(extension).isNotNull();
    }

    @Test
    public void shouldSerializePlatform() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Platform platform = mapper.readValue(new File("src/test/resources/repository/platforms/quarkus-bom.json"), Platform.class);
        assertThat(platform).isNotNull();
    }

}

