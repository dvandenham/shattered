package shattered.game;

import org.jetbrains.annotations.NotNull;
import shattered.core.nbtx.NBTX;

public interface ISerializable {

	@NotNull
	NBTX serialize(@NotNull NBTX store);

	void deserialize(@NotNull NBTX store);
}