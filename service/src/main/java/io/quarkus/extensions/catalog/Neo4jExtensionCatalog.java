package io.quarkus.extensions.catalog;

import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.dependencies.Extension;
import io.quarkus.extensions.catalog.model.Release;
import io.quarkus.extensions.catalog.spi.IndexVisitor;
import io.quarkus.platform.descriptor.QuarkusPlatformDescriptor;

@ApplicationScoped
public class Neo4jExtensionCatalog implements ExtensionCatalog, IndexVisitor {

    @Override
    public Set<Extension> getExtensions() {
        return null;
    }

    @Override
    public void visitPlatform(QuarkusPlatformDescriptor descriptor) {

    }

    @Override
    public void visitExtension(Extension descriptor, String quarkusCore) {

    }
}
