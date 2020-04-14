package io.quarkus.extensions.catalog.spi;

import io.quarkus.dependencies.Extension;
import io.quarkus.platform.descriptor.QuarkusPlatformDescriptor;

public interface IndexVisitor {

    void visitPlatform(QuarkusPlatformDescriptor descriptor);
    void visitExtension(Extension descriptor, String quarkusCore);
}
