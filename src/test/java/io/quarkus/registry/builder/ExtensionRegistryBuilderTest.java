package io.quarkus.registry.builder;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.registry.ExtensionRegistry;
import io.quarkus.registry.impl.DefaultArtifactResolver;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ExtensionRegistryBuilderTest {

    @Test
    void shouldResolveCamelDependencies() throws IOException {
        DefaultArtifactResolver resolver = new DefaultArtifactResolver(new ObjectMapper());
        ExtensionRegistry registry = new ExtensionRegistryBuilder(resolver)
                .addPlatform("io.quarkus","quarkus-universe-bom","1.5.1.Final")
                .build();

        Assertions.assertThat(registry.list("1.5.1.Final", "camel")).isNotEmpty();
    }

}