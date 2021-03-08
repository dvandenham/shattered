package shattered.lib.registry;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import com.google.common.graph.Graph;

@SuppressWarnings("UnstableApiUsage")
final class StronglyConnectedComponentDetector<T> {

	private final Graph<T> graph;
	private final Map<T, Integer> ids;
	private final T[] elements;
	private final int[] depthFirstIndices;
	private final int[] lows;
	private final int[] stack;
	private int top;
	private final BitSet onStackTable;
	private final Set<Set<T>> components;

	@SuppressWarnings("unchecked")
	StronglyConnectedComponentDetector(final Graph<T> graph) {
		this.graph = graph;
		//Setup
		this.components = new HashSet<>();
		final AtomicInteger currentIndex = new AtomicInteger(0);
		this.ids = new HashMap<>();
		final Set<T> nodes = this.graph.nodes();
		this.elements = (T[]) new Object[nodes.size()];
		nodes.forEach(node -> {
			this.ids.put(node, currentIndex.get());
			this.elements[currentIndex.getAndIncrement()] = node;
		});
		this.depthFirstIndices = new int[this.elements.length];
		this.lows = new int[this.elements.length];
		this.stack = new int[this.elements.length];
		this.onStackTable = new BitSet(this.elements.length);
		this.top = -1;
		//Calculate
		for (int i = 0; i < this.elements.length; i++) {
			if (this.depthFirstIndices[i] == 0) {
				this.depthFirstSearch(i, 1);
			}
		}
	}

	public Set<Set<T>> getComponents() {
		return this.components;
	}

	private void depthFirstSearch(final int currentIndex, final int depth) {
		this.depthFirstIndices[currentIndex] = depth;
		this.lows[currentIndex] = depth;
		++this.top;
		this.stack[this.top] = currentIndex;
		this.onStackTable.set(currentIndex);
		for (final T successor : this.graph.successors(this.elements[currentIndex])) {
			final int index = this.ids.get(successor);
			if (this.depthFirstIndices[index] != 0) {
				if (this.lows[currentIndex] > this.depthFirstIndices[index]) {
					this.lows[currentIndex] = this.depthFirstIndices[index];
				}
			} else {
				this.depthFirstSearch(index, depth + 1);
				if (this.lows[currentIndex] > this.lows[index]) {
					this.lows[currentIndex] = this.lows[index];
				}
			}
		}
		if (this.depthFirstIndices[currentIndex] == this.lows[currentIndex]) {
			final Set<T> component = new HashSet<>();
			while (this.top >= 0) {
				final int stackIndex = this.stack[this.top];
				component.add(this.elements[stackIndex]);
				this.onStackTable.clear(stackIndex);
				--this.top;
				if (stackIndex == currentIndex) {
					break;
				}
			}
			this.components.add(component);
		}
	}
}