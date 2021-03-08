package shattered.lib.registry;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import shattered.Shattered;
import shattered.core.event.EventBusSubscriber;
import shattered.core.event.MessageEvent;
import shattered.core.event.MessageListener;
import shattered.lib.ResourceLocation;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import org.jetbrains.annotations.NotNull;

final class MasterRegistry {

	private static final Object2ObjectArrayMap<ResourceLocation, Registry<?>> REGISTRIES = new Object2ObjectArrayMap<>();
	private static final Object2ObjectArrayMap<ResourceLocation, List<ResourceLocation>> DEPENDENCIES = new Object2ObjectArrayMap<>();

	private MasterRegistry() {
	}

	public static void register(@NotNull final ResourceLocation resource, @NotNull final Registry<?> registry, @NotNull final ResourceLocation[] dependencies) {
		MasterRegistry.REGISTRIES.put(resource, registry);
		if (dependencies.length > 0) {
			MasterRegistry.DEPENDENCIES.put(resource, Arrays.asList(dependencies));
		}
	}

	public static boolean registryExists(@NotNull final ResourceLocation resource) {
		return MasterRegistry.REGISTRIES.containsKey(resource);
	}

	private static void loadRegistries() {
		final Registry<?>[] loadOrder = MasterRegistry.createGraph();
		assert loadOrder != null;
		RegistryLoader.loadRegistries(loadOrder);
	}

	@SuppressWarnings({"UnstableApiUsage", "unchecked"})
	private static Registry<?>[] createGraph() {
		final MutableGraph<ResourceLocation> graph = GraphBuilder.directed().build();

		final AtomicInteger counter = new AtomicInteger(0);
		final Map<ResourceLocation, Integer> orderMapping = MasterRegistry.REGISTRIES.keySet()
				.stream()
				.collect(Collectors.toMap(Function.identity(), ignored -> counter.incrementAndGet()));

		MasterRegistry.REGISTRIES.keySet().forEach(graph::addNode);

		MasterRegistry.DEPENDENCIES.forEach((registry, deps) -> deps.forEach(dep -> graph.putEdge(registry, dep)));

		try {
			final List<ResourceLocation> sorted = MasterRegistry.topoSort(graph, Comparator.comparing(orderMapping::get));
			final Registry<?>[] resultArr = new Registry<?>[sorted.size()];
			for (int i = 0; i < sorted.size(); ++i) {
				resultArr[i] = MasterRegistry.REGISTRIES.get(sorted.get(i));
			}
			return resultArr;
		} catch (final CyclicDependencyException e) {
			final Set<Set<ResourceLocation>> cycles = (Set<Set<ResourceLocation>>) (Set<?>) e.cycles;
			final StringBuilder builder = new StringBuilder("Detected cycles in registry dependency order:\n");
			cycles.forEach(cycle -> {
				builder.append('\t');
				builder.append(String.join(", ", cycle.stream().map(ResourceLocation::toString).toArray(String[]::new)));
			});
			Shattered.crash(builder.toString(), null);
			return null;
		}
	}

	@SuppressWarnings({"UnstableApiUsage", "unchecked"})
	private static <T> List<T> topoSort(final Graph<T> graph, final Comparator<? super T> comparator) throws CyclicDependencyException {
		final Queue<T> queue = comparator == null ? new ArrayDeque<>() : new PriorityQueue<>(comparator);
		final Map<T, Integer> degrees = new HashMap<>();
		final List<T> results = new ArrayList<>();
		for (final T node : graph.nodes()) {
			final int degree = graph.inDegree(node);
			if (degree == 0) {
				queue.add(node);
			} else {
				degrees.put(node, degree);
			}
		}
		while (!queue.isEmpty()) {
			final T current = queue.remove();
			results.add(current);
			for (final T successor : graph.successors(current)) {
				final int updated = degrees.compute(successor, (node, degree) -> Objects.requireNonNull(degree, () -> "Invalid degree present for " + node) - 1);
				if (updated == 0) {
					queue.add(successor);
					degrees.remove(successor);
				}
			}
		}
		if (!degrees.isEmpty()) {
			final Set<Set<T>> components = new StronglyConnectedComponentDetector<>(graph).getComponents();
			components.removeIf(set -> set.size() < 2);
			throw new CyclicDependencyException((Set<Set<?>>) (Set<?>) components);
		}
		return results;
	}

	@EventBusSubscriber(Shattered.SYSTEM_BUS_NAME)
	private static class EventHandler {

		@MessageListener("freeze_registries")
		public static void onRegistryFreeze(final MessageEvent ignored) {
			MasterRegistry.REGISTRIES.values().forEach(registry -> registry.frozen = true);
		}

		@MessageListener("load_registries")
		private static void onLoadRegistries(final MessageEvent ignored) {
			MasterRegistry.loadRegistries();
		}
	}
}