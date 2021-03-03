package shattered.lib.asset;

import shattered.core.event.Event;

public class ResourceReloadEvent extends Event<Void> {

	ResourceReloadEvent() {
	}

	public static class Post extends ResourceReloadEvent {
		
		Post() {
		}
	}
}