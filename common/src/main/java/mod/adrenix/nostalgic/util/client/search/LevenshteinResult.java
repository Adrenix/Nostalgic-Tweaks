package mod.adrenix.nostalgic.util.client.search;

import mod.adrenix.nostalgic.util.client.search.algorithm.NormalizedLevenshtein;
import net.minecraft.Util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class LevenshteinResult<T>
{
    /* Fields */

    private final Map<String, T> map;
    private final double threshold;

    /* Constructor */

    private LevenshteinResult(Map<String, T> map, double threshold)
    {
        this.map = map;
        this.threshold = threshold;
    }

    /* Static */

    /**
     * Prepares a {@link LevenshteinResult} instance to match the content of the given {@link Map} while considering the
     * given threshold. The matching will search for similar keys within the map. The result is backed by the map, so
     * each call to {@link #apply(String)} will reflect changes of the given map.
     *
     * @param map       A {@link Map} of strings to search through and returned values.
     * @param threshold The minimum Levenshtein result [0.0-1.0] required to be put in the result map.
     * @param <V>       The class type of the map values and returned values after searching.
     * @return A new {@link LevenshteinResult} instance to match the content of the given {@link Map} using the given
     * threshold.
     */
    public static <V> LevenshteinResult<V> with(Map<String, V> map, double threshold)
    {
        return new LevenshteinResult<>(map, threshold);
    }

    /* Methods */

    /**
     * Apply a search query request.
     *
     * @param query The string to compare against the cache map.
     * @return A {@link List} of sorted {@link Map} entries based on normalized Levenshtein values.
     */
    public List<Map.Entry<T, Double>> apply(String query)
    {
        HashMap<T, Double> results = new HashMap<>();

        if (query.isEmpty() || query.isBlank())
        {
            this.map.values()
                .stream()
                .map(value -> Map.entry(value, 1.0D))
                .forEach(entry -> results.put(entry.getKey(), entry.getValue()));
        }
        else
        {
            this.map.entrySet().stream().map(entry -> CompletableFuture.supplyAsync(() -> {
                double levenshtein = NormalizedLevenshtein.get(entry.getKey(), query);

                if (levenshtein >= this.threshold)
                    return Map.entry(entry.getValue(), levenshtein);

                return null;
            }, Util.backgroundExecutor())).toList().stream().map(CompletableFuture::join).forEach(entry -> {
                if (entry == null)
                    return;

                results.put(entry.getKey(), entry.getValue());
            });
        }

        return results.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .collect(Collectors.toList());
    }
}
