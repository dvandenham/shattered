package shattered.lib.registry;

import java.util.Set;

public final class CyclicDependencyException extends Exception {

	private static final long serialVersionUID = 2150457263735234974L;
	final Set<Set<?>> cycles;

	CyclicDependencyException(final Set<Set<?>> cycles) {
		this.cycles = cycles;
	}
}