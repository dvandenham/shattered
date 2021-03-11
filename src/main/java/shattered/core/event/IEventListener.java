package shattered.core.event;

public interface IEventListener {

	abstract void invoke(final Event<?> event);

	default void test() {

	}
}