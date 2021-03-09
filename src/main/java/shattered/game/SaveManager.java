package shattered.game;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeSet;
import shattered.Shattered;
import shattered.core.nbtx.NBTX;
import shattered.game.world.World;
import shattered.lib.ResourceLocation;
import shattered.lib.registry.NotRegisteredException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SaveManager {

	@NotNull
	private final GameManager gameManager;
	@NotNull
	private final File rootDir;

	SaveManager(@NotNull final GameManager gameManager, @NotNull final File rootDir) {
		this.gameManager = gameManager;
		this.rootDir = rootDir;
	}

	public void serializeWorld(@NotNull final World world) throws IOException, InvalidSaveException {
		Shattered.LOGGER.info("Serializing world to disk: {}", world.getResource());
		final SaveData save = this.loadOrCreateSave(this.getWorldSaveDir(world.getResource()), world);
		final String newUuid = save.requestUUID();

		final NBTX worldStore = world.serialize(new NBTX());
		worldStore.serialize(save.getWorldFile(newUuid));

		final String oldUuid = save.storeUUID(newUuid);
		if (oldUuid != null) {
			final File oldWorldFile = save.getWorldFile(oldUuid);
			if (!oldWorldFile.delete()) {
				oldWorldFile.deleteOnExit();
			}
		}
	}

	@Nullable
	public World deserializeWorld(@NotNull final SaveData save, @NotNull final String uuid) throws IOException {
		final World world = this.gameManager.createWorld(save.getWorldId());
		if (world == null) {
			throw new NotRegisteredException("World", save.getWorldId());
		}
		final File worldFile = save.getWorldFile(uuid);
		if (!worldFile.exists()) {
			return null;
		}
		final NBTX store = NBTX.deserializeNBTX(worldFile);
		world.deserialize(store);
		return world;
	}

	@NotNull
	public SaveData[] listSaves() {
		final File[] saveDirs = this.rootDir.listFiles();
		if (saveDirs == null) {
			return new SaveData[0];
		} else {
			final TreeSet<SaveData> set = new TreeSet<>(Comparator.comparingLong(SaveData::getLastModifiedTime));
			Arrays.stream(saveDirs).filter(File::isDirectory).forEach(saveDir -> {
				try {
					final SaveData save = new SaveData(saveDir);
					set.add(save);
				} catch (final InvalidSaveException e) {
					//TODO handle this
					e.printStackTrace();
				}
			});
			return set.toArray(new SaveData[0]);
		}
	}

	private SaveData loadOrCreateSave(@NotNull final File saveDir, @NotNull final World world) throws IOException, InvalidSaveException {
		if (saveDir.exists()) {
			return new SaveData(saveDir);
		} else {
			final NBTX store = new NBTX();
			store.newArray("versions");
			store.newTable("version_data");
			store.set("world_id", world.getResource().toString());
			final SaveData result = new SaveData(saveDir, store);
			result.setDisplayName(world.getResource().getNamespace() + ":" + world.getResource().getResource());
			result.save();
			return result;
		}
	}

	@NotNull
	private File getWorldSaveDir(@NotNull final ResourceLocation resource) {
		return new File(this.rootDir, resource.toString().replace(':', '_').replace('#', '_'));
	}
}