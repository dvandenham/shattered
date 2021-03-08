package shattered.game.world;

import shattered.game.tile.Tile;
import shattered.lib.math.Dimension;
import org.jetbrains.annotations.NotNull;

public final class Structure {

	private final Tile[][] structure;
	private final Dimension worldSize;

	Structure(@NotNull final Tile[][] structure, @NotNull final Dimension worldSize) {
		this.structure = structure;
		this.worldSize = worldSize;
	}

	public Dimension getWorldSize() {
		return this.worldSize;
	}

	public Tile[][] getStructure() {
		final Tile[][] result = new Tile[this.structure.length][];
		for (int i = 0; i < result.length; ++i) {
			result[i] = new Tile[this.structure[i].length];
			System.arraycopy(this.structure[i], 0, result[i], 0, this.structure[i].length);
		}
		return this.structure;
	}
}