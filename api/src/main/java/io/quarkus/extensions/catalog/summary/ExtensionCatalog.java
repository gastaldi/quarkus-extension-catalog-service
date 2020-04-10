package io.quarkus.extensions.catalog.summary;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import io.quarkus.bootstrap.model.AppArtifactKey;

/**
 * Repository of extensions
 */
public class ExtensionCatalog implements Serializable {

	private final Map<AppArtifactKey, ExtensionSummary> extensions = new HashMap<>();
	private final Map<AppArtifactKey, PlatformSummary> platforms = new HashMap<>();
	private final Map<String, QuarkusCore> quarkusCores = new HashMap<>();

	ExtensionCatalog() {
	}

	QuarkusCore getOrCreateQuarkusCore(String version) {
		return quarkusCores.computeIfAbsent(version, QuarkusCore::new);
	}

	ExtensionSummary getOrCreateExtension(AppArtifactKey id) {
		return extensions.computeIfAbsent(id, ExtensionSummary::new);
	}

	PlatformSummary getOrCreatePlatform(AppArtifactKey id) {
		return platforms.computeIfAbsent(id, PlatformSummary::new);
	}

	public Collection<String> getQuarkusCoreVersions() {
		return quarkusCores.keySet();
	}

	public QuarkusCore getQuarkusCore(String version) {
		return quarkusCores.get(version);
	}

	public Collection<QuarkusCore> getQuarkusCores() {
		return quarkusCores.values();
	}

	public Collection<AppArtifactKey> getPlatformIds() {
		return platforms.keySet();
	}

	public PlatformSummary getPlatformSummary(String groupId, String artifactId) {
		return getPlatformSummary(new AppArtifactKey(groupId, artifactId));
	}

	public PlatformSummary getPlatformSummary(AppArtifactKey id) {
		return platforms.get(id);
	}

	public Collection<PlatformSummary> getPlatformSummaries() {
		return platforms.values();
	}

	public Collection<ExtensionSummary> getExtensionSummaries() {
		return extensions.values();
	}
}
