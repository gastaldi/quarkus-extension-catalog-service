package io.quarkus.extensions.catalog.service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.quarkus.extensions.catalog.model.Extension;
import io.quarkus.runtime.Startup;

@ApplicationScoped
@Startup
public class CatalogService {

    private Set<Extension> extensions = new HashSet<>();

    @PostConstruct
    void initialize() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("META-INF/catalog.zip");
                ZipInputStream zp = new ZipInputStream(is, StandardCharsets.UTF_8)) {
            ZipEntry ze = null;
            while ((ze = zp.getNextEntry()) != null) {
                Extension extension = objectMapper.readValue(zp.readAllBytes(), Extension.class);
                extensions.add(extension);
            }
        }
    }

    public Set<Extension> getExtensions() {
        return extensions;
    }
}
