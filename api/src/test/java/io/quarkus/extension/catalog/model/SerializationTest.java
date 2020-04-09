package io.quarkus.extension.catalog.model;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.quarkus.extensions.catalog.model.Extension;
import io.quarkus.extensions.catalog.model.Platform;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SerializationTest {

    @Test
    public void shouldSerializeExtension() throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Extension extension = mapper.readValue(new File("src/test/resources/repository/extensions/jsf.yaml"), Extension.class);
        assertThat(extension).isNotNull();
    }

    @Test
    public void shouldSerializePlatform() throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Platform platform = mapper.readValue(new File("src/test/resources/repository/platforms/quarkus-bom.yaml"), Platform.class);
        assertThat(platform).isNotNull();
    }

}

