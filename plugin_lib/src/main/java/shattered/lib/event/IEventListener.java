package shattered.lib.event;

public interface IEventListener {

	void invoke(final Event<?> event);
}