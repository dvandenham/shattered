package shattered.lib;

public final class InvalidResourceLocationException extends RuntimeException {

	private static final long serialVersionUID = -2669165013718171979L;

	InvalidResourceLocationException(final String reason) {
		super(reason);
	}
}