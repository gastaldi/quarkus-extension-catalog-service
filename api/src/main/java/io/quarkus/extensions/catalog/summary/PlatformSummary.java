package io.quarkus.extensions.catalog.summary;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import io.quarkus.bootstrap.model.AppArtifactKey;

/**
 * Brief platform summary
 */
public class PlatformSummary implements Serializable {

	private final AppArtifactKey id;
	private final Map<String, PlatformRelease> releases = new HashMap<>();
	private final Set<String> quarkusCores = new HashSet<>();

	PlatformSummary(AppArtifactKey id) {
		this.id = Objects.requireNonNull(id);
	}

	public AppArtifactKey getId() {
		return id;
	}

	public Collection<String> getQuarkusCores() {
		return quarkusCores;
	}

	PlatformRelease getOrCreateRelease(String version, QuarkusCore quarkusCore) {
		quarkusCores.add(quarkusCore.getVersion());
		return releases.computeIfAbsent(version, v -> new PlatformRelease(this, v, quarkusCore));
	}
}
