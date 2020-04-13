package io.quarkus.ecosystem.maven;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.List;

import io.quarkus.extensions.catalog.model.Repository;
import io.quarkus.extensions.catalog.summary.ExtensionCatalog;
import io.quarkus.extensions.catalog.summary.ExtensionCatalogBuilder;
import io.quarkus.extensions.catalog.summary.ExtensionRelease;
import io.quarkus.extensions.catalog.summary.ExtensionsRepositoryException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;
import io.quarkus.bootstrap.resolver.AppModelResolverException;
import io.quarkus.bootstrap.resolver.maven.MavenArtifactResolver;
import org.neo4j.driver.GraphDatabase;


/**
 * Parses an extensions repository spec yaml file, builds the corresponding object model
 * and dumps the summary.
 */
@Mojo(name = "generate-extensions-repo", requiresProject = false)
public class BasicMojo extends AbstractMojo {

    @Component
    private RepositorySystem repoSystem;

    @Parameter(defaultValue = "${repositorySystemSession}", readonly = true)
    private RepositorySystemSession repoSession;

    @Parameter(defaultValue = "${project.remoteProjectRepositories}", readonly = true, required = true)
    private List<RemoteRepository> repos;

    @Parameter(property = "repositoryPath", defaultValue = "${basedir}")
    private File repositoryPath;


	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
	    final ExtensionCatalog repo = buildExtensionsRepo();

		try {
			logSummary(repo);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    private void logSummary(ExtensionCatalog repo) throws IOException {
		try (FileOutputStream fos = new FileOutputStream("/tmp/foo.obj");
			 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			oos.writeObject(repo);
			oos.flush();
		}
		if (true)
			return;
    	log("");
		log("REPOSITORY SUMMARY");

		log("  QUARKUS CORE VERSIONS");
		repo.getQuarkusCores().forEach(v -> {
			log("    " + v);
			log("      Available platforms: ");
			v.getPlatforms().forEach(p -> {
				log("        " + p.getSummary().getId() + ":" + p.getVersion() + "; ");
			});
		});

		log("  PLATFORMS");
		repo.getPlatformSummaries().forEach(p -> {
			log("    " + p.getId());
			log("      Available for Quarkus Core:");
			p.getQuarkusCores().forEach(v -> log("        " + v));
		});

		log("  EXTENSIONS");
		repo.getExtensionSummaries().forEach(e -> {
			log("    " + e.getId());
			log("      Available versions:");
			e.getReleases().forEach(r -> {
				log("        " + r.getVersion());
				log("          Compatible with Quarkus Core:");
				r.getQuarkusCores().forEach(s -> log("            " + s));
				log("          Appears in platforms:");
				r.getPlatforms().forEach(p -> {
					log("            " + p.getSummary().getId() + ":" + p.getVersion());
				});
			});
		});

		log("  NON-PLATFORM EXTENSIONS");
		repo.getExtensionSummaries().forEach(s -> {
			boolean loggedSummary = false;
			for(ExtensionRelease r : s.getReleases()) {
				if(r.getPlatforms().isEmpty()) {
					if(!loggedSummary) {
						loggedSummary = true;
						log("    " + s.getId());
					}
					log("      " + r.getVersion());
					log("        Compatible with Quarkus Core:");
					r.getQuarkusCores().forEach(v -> log("          " + v));
				}
			}
		});
    }

    private void log(String line) {
    	System.out.println(line);
    }
	private ExtensionCatalog buildExtensionsRepo() throws MojoExecutionException {
		try {
			Repository repository = Repository.parse(repositoryPath.toPath());
			return ExtensionCatalogBuilder.getInstance(getMavenResolver()).build(Collections.singletonList(repository));
		} catch (ExtensionsRepositoryException e) {
			throw new MojoExecutionException("Failed to build Quarkus extensions repo from " + repositoryPath, e);
		}
	}


	private MavenArtifactResolver getMavenResolver() throws MojoExecutionException {
		try {
			return MavenArtifactResolver.builder().setRepositorySystem(repoSystem)
					.setRepositorySystemSession(repoSession)
					.setRemoteRepositories(repos)
					.build();
		} catch (AppModelResolverException e) {
			throw new MojoExecutionException("Failed to initialize Maven artifact resolver", e);
		}
	}
}
