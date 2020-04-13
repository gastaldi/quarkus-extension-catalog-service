package io.quarkus.extensions.catalog.service;

import io.quarkus.dependencies.Extension;
import io.quarkus.platform.descriptor.QuarkusPlatformDescriptor;

public interface IndexVisitor {

    void visitPlatform(QuarkusPlatformDescriptor descriptor);
    void visitExtension(Extension descriptor, String quarkusCore);
}
