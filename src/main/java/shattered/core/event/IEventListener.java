package shattered.core.event;

public interface IEventListener {

	void invoke(final Event<?> event);
}