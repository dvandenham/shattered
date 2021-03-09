package shattered.game;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import shattered.core.nbtx.NBTX;
import shattered.core.nbtx.NBTXTagArray;
import shattered.core.nbtx.NBTXTagTable;
import shattered.lib.FileUtils;
import shattered.lib.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SaveData {

	private static final String META_FILE_NAME = "meta.data";
	private static final int MAX_SAVES = 16;

	@NotNull
	private final File saveDir;
	@NotNull
	private final File metaFile;
	@NotNull
	private final NBTX metadata;

	SaveData(@NotNull final File saveDir) throws IOException, InvalidSaveException {
		this.saveDir = saveDir;
		this.metaFile = new File(saveDir, SaveData.META_FILE_NAME);
		if (!this.metaFile.exists()) {
			throw new InvalidSaveException(InvalidSaveException.InvalidSaveReason.METADATA_MISSING, this.metaFile.getAbsolutePath());
		} else {
			this.metadata = NBTX.deserializeNBTX(this.metaFile);
			if (!this.metadata.hasString("world_id")) {
				throw new InvalidSaveException(InvalidSaveException.InvalidSaveReason.METADATA_CORRUPT, this.metaFile.getAbsolutePath());
			} else if (!this.metadata.hasString("display_name")) {
				throw new InvalidSaveException(InvalidSaveException.InvalidSaveReason.METADATA_CORRUPT, this.metaFile.getAbsolutePath());
			} else if (!this.metadata.hasArray("versions")) {
				throw new InvalidSaveException(InvalidSaveException.InvalidSaveReason.METADATA_CORRUPT, this.metaFile.getAbsolutePath());
			} else if (!this.metadata.hasTable("version_data")) {
				throw new InvalidSaveException(InvalidSaveException.InvalidSaveReason.METADATA_CORRUPT, this.metaFile.getAbsolutePath());
			}
		}
	}

	SaveData(@NotNull final File saveDir, @NotNull final NBTX store) throws IOException {
		this.saveDir = saveDir;
		if (!this.saveDir.mkdirs()) {
			throw new IOException("Cannot create save directory: " + saveDir.getAbsolutePath());
		}
		this.metaFile = new File(saveDir, SaveData.META_FILE_NAME);
		this.metadata = store;
	}

	public void save() throws IOException {
		this.metadata.serialize(this.metaFile);
	}

	public void delete() {
		FileUtils.deleteDirectoryRecursive(this.saveDir);
	}

	@NotNull
	String requestUUID() {
		final NBTXTagTable versions = this.metadata.getTable("version_data");
		assert versions != null;
		String uuid = null;
		while (uuid == null || versions.hasTag(uuid)) {
			uuid = UUID.randomUUID().toString().replaceAll("-", "");
		}
		return uuid;
	}

	@SuppressWarnings("ConstantConditions")
	@Nullable
	String storeUUID(@NotNull final String uuid) {
		final NBTXTagArray array = this.metadata.getArray("versions");
		final NBTXTagArray newArray = this.metadata.newArray("versions");

		newArray.add(uuid);
		for (int i = 0; i < Math.min(SaveData.MAX_SAVES, array.getSize()); ++i) {
			newArray.add(array.getString(i));
		}

		final String oldUuid = array.getSize() == SaveData.MAX_SAVES ? array.getString(array.getSize() - 1) : null;

		final NBTXTagTable versionData = this.metadata.getTable("version_data");
		if (oldUuid != null) {
			versionData.removeKey(oldUuid);
		}
		versionData.set(uuid, System.currentTimeMillis());

		return oldUuid;
	}

	@NotNull
	public ResourceLocation getWorldId() {
		return new ResourceLocation(Objects.requireNonNull(this.metadata.getString("world_id")));
	}

	public void setDisplayName(@NotNull final String displayName) {
		this.metadata.set("display_name", displayName);
	}

	@NotNull
	public String getDisplayName() {
		return Objects.requireNonNull(this.metadata.getString("display_name"));
	}

	@NotNull
	public String[] listVersionsSorted() {
		final NBTXTagArray array = this.metadata.getArray("versions");
		assert array != null;
		final String[] result = new String[array.getSize()];
		for (int i = 0; i < result.length; ++i) {
			result[i] = Objects.requireNonNull(array.getString(i));
		}
		return result;
	}

	@NotNull
	public Map<String, Long> listVersionData() {
		final NBTXTagTable table = this.metadata.getTable("version_data");
		assert table != null;

		final HashMap<String, Long> map = new HashMap<>();
		for (final String key : table.getKeyNames()) {
			map.put(key, table.getLong(key));
		}
		return map;
	}

	@NotNull
	File getSaveDir() {
		return this.saveDir;
	}

	@NotNull
	File getWorldFile(final String uuid) {
		return new File(this.saveDir, uuid + ".dat");
	}

	long getLastModifiedTime() {
		return this.metaFile.lastModified();
	}
}