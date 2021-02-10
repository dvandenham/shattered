package preboot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.TreeSet;

final class TransformationRegistry {

	private static final TreeSet<ITransformer> TRANSFORMERS = new TreeSet<>((o1, o2) -> o2.priority() - o1.priority());

	static {
		TransformationRegistry.TRANSFORMERS.add(new TransformerEventBusSubscriber());
	}

	public static byte[] transform(final String className, byte[] bytes) {
		boolean hasTransformed = false;
		for (final ITransformer transformer : TransformationRegistry.TRANSFORMERS) {
			try {
				final byte[] result = transformer.transform(className, bytes);
				if (result != null) {
					hasTransformed = true;
					bytes = result;
					if (SysProps.LOG_TRANSFORM) {
						Preboot.LOGGER.debug("Transformed class {} using transformer {}", className, transformer.getClass().getName());
					}
				}
			} catch (final Throwable e) {
				Preboot.LOGGER.fatal("Error while transforming class {} with transformer {}", className, transformer.getClass().getName());
				if (SysProps.LOG_TRANSFORM_ERROR) {
					Preboot.LOGGER.fatal(e);
				}
			}
		}
		if (hasTransformed && SysProps.DUMP_CLASSES) {
			TransformationRegistry.dumpClass(className, bytes);
		}
		return bytes;
	}

	private static void dumpClass(final String className, final byte[] data) {
		final File dir = new File("shattered_class_dump");
		if (dir.mkdir()) {
			try (final FileOutputStream stream = new FileOutputStream(new File(dir, className.replaceAll("\\.", "_") + ".class"))) {
				stream.write(data);
			} catch (final IOException ignored) {
			}
		}
	}
}