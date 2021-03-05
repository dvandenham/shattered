package shattered;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.jetbrains.annotations.NotNull;
import shattered.core.nbtx.NBTX;
import shattered.core.nbtx.NBTXTagTable;

public final class BootMessageQueue {

	private final ConcurrentLinkedQueue<BootMessage> queue = new ConcurrentLinkedQueue<>();
	private boolean hasWarningOrError = false;

	BootMessageQueue() {
	}

	public void addMessage(@NotNull final String typeIdentifier,
	                       @NotNull final String messageIdentifier,
	                       @NotNull final BootMessageQueue.BootMessage.Severity severity,
	                       @NotNull final String message) {
		this.queue.offer(new BootMessage(typeIdentifier, messageIdentifier, severity, message));
		if (severity.ordinal() >= BootMessage.Severity.WARNING.ordinal()) {
			this.hasWarningOrError = true;
		}
	}

	public BootMessage[] getMessages() {
		return this.queue.toArray(new BootMessage[0]);
	}

	public boolean hasWarningOrError() {
		return this.hasWarningOrError;
	}

	public boolean hasNewMessages() {
		if (this.queue.isEmpty()) {
			return false;
		} else {
			final File file = BootMessageQueue.getStoreFile();
			try {
				if (!file.exists()) {
					return true;
				} else {
					final NBTX store = NBTX.deserializeNBTX(file);
					final NBTXTagTable array = store.getTable("messages");
					if (array == null || array.getKeyCount() != this.queue.size()) {
						return true;
					}
					for (final BootMessage msg : this.queue) {
						if (!array.hasTag(msg.typeIdentifier + "_" + msg.messageIdentifier)) {
							return true;
						}
					}
					return false;
				}
			} catch (final Throwable ignored) {
				return true;
			}
		}
	}

	void writeToDisk() {
		final File file = BootMessageQueue.getStoreFile();
		final NBTX store = new NBTX();
		final NBTXTagTable table = store.newTable("messages");
		this.queue.forEach(msg -> {
			final String data = msg.typeIdentifier + "_" + msg.messageIdentifier;
			table.set(data, data);
		});
		try {
			store.serialize(file);
		} catch (final IOException ignored) {
		}
	}

	private static File getStoreFile() {
		return Shattered.WORKSPACE.getDataFile("bmq.db");
	}

	public static class BootMessage {

		public enum Severity {
			INFO,
			WARNING,
			ERROR
		}

		@NotNull
		private final String typeIdentifier;

		@NotNull
		private final String messageIdentifier;

		@NotNull
		private final Severity severity;

		@NotNull
		private final String message;

		BootMessage(@NotNull final String typeIdentifier,
		            @NotNull final String messageIdentifier,
		            @NotNull final Severity severity,
		            @NotNull final String message) {
			this.typeIdentifier = typeIdentifier;
			this.messageIdentifier = messageIdentifier;
			this.severity = severity;
			this.message = message;
		}

		@NotNull
		public String getTypeIdentifier() {
			return this.typeIdentifier;
		}

		@NotNull
		public String getMessageIdentifier() {
			return this.messageIdentifier;
		}

		@NotNull
		public Severity getSeverity() {
			return this.severity;
		}

		@NotNull
		public String getMessage() {
			return this.message;
		}

		@Override
		public String toString() {
			return "BootMessage{" +
					"typeIdentifier='" + this.typeIdentifier + '\'' +
					", messageIdentifier='" + this.messageIdentifier + '\'' +
					", severity=" + this.severity +
					", message='" + this.message + '\'' +
					'}';
		}
	}
}