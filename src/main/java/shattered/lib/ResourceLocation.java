package shattered.lib;

import java.io.Serializable;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public final class ResourceLocation implements Serializable {

	private static final long serialVersionUID = -7886724620380363691L;
	private static final String DEFAULT_NAMESPACE = "shattered";
	private static final String DEFAULT_VARIANT = "default";

	@NotNull
	private final String namespace;
	@NotNull
	private final String resource;
	@NotNull
	private final String variant;

	public ResourceLocation(@NotNull final String namespace, @NotNull final String resource, @NotNull final String variant) {
		if (StringUtils.isAlphaString(namespace)) {
			this.namespace = namespace;
		} else {
			throw new InvalidResourceLocationException("Namespace can only contain characters from range [a-z]");
		}
		if (StringUtils.isResourceString(resource)) {
			this.resource = resource;
		} else {
			throw new InvalidResourceLocationException("Resource can only contain characters from range [a-z0-9] and '.' '/' '-' '_'");
		}
		if (StringUtils.isAlphaString(variant)) {
			this.variant = variant;
		} else {
			throw new InvalidResourceLocationException("Variant can only contain characters from range [a-z]");
		}
	}

	public ResourceLocation(@NotNull final String namespace, @NotNull final String resource) {
		this(namespace, resource, ResourceLocation.DEFAULT_VARIANT);
	}

	public ResourceLocation(@NotNull final String resource) {
		final String[] parts = resource.split(":", 2);
		final String namespace = parts.length > 1 ? parts[0] : ResourceLocation.DEFAULT_NAMESPACE;
		if (!StringUtils.isAlphaString(namespace)) {
			throw new InvalidResourceLocationException("Namespace can only contain characters from range [a-z]");
		}
		this.namespace = namespace;
		final String[] parts2 = parts[parts.length > 1 ? 1 : 0].split("#", 2);
		if (!StringUtils.isResourceString(parts2[0])) {
			throw new InvalidResourceLocationException("Resource can only contain characters from range [a-z0-9] and '.' '/' '-' '_'");
		}
		this.resource = parts2[0];
		this.variant = parts2.length > 1 ? parts2[1] : ResourceLocation.DEFAULT_VARIANT;
		if (!StringUtils.isAlphaString(this.variant)) {
			throw new InvalidResourceLocationException("Variant can only contain characters from range [a-z]");
		}
	}

	@NotNull
	public ResourceLocation toVariant(@NotNull final String variant) {
		return new ResourceLocation(this.namespace, this.resource, variant);
	}

	@NotNull
	public String getNamespace() {
		return this.namespace;
	}

	@NotNull
	public String getResource() {
		return this.resource;
	}

	@NotNull
	public String getVariant() {
		return this.variant;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.namespace, this.resource, this.variant);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || this.getClass() != o.getClass()) {
			return false;
		}
		final ResourceLocation other = (ResourceLocation) o;
		return this.toString().equals(other.toString());
	}

	@Override
	public String toString() {
		return this.namespace + ":" + this.resource + "#" + this.variant;
	}
}