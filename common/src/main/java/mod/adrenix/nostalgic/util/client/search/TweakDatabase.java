package mod.adrenix.nostalgic.util.client.search;

import mod.adrenix.nostalgic.tweak.factory.Tweak;
import mod.adrenix.nostalgic.tweak.factory.TweakPool;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The tweak database is prebuilt and cached to improve performance when searching for a query. New instances of this
 * cannot be created. Use {@link #getInstance()} to start a search.
 */
public class TweakDatabase extends Database<Tweak<?>>
{
    private static final TweakDatabase TWEAK_SEARCH = new TweakDatabase();
    private String lastQuery = "";

    private TweakDatabase()
    {
        this.init();
    }

    private void init()
    {
        if (!this.map.isEmpty())
            this.map.clear();

        TweakPool.filter(Tweak::isNotIgnored)
            .forEach(tweak -> this.map.put(tweak.getTranslation().getString().toLowerCase(), tweak));
    }

    /**
     * The tweak database is prebuilt and cached to improve performance when searching for a query. Therefore, this must
     * be used to start a search.
     *
     * @return The singleton instance of {@link TweakDatabase}.
     */
    public static TweakDatabase getInstance()
    {
        return TWEAK_SEARCH;
    }

    @Override
    public Map<String, Tweak<?>> getDatabase()
    {
        Optional<SearchTag> tag = SearchTag.get(this.lastQuery);

        if (tag.isPresent())
            return tag.get().getMap();

        return this.map;
    }

    @Override
    public List<Map.Entry<Tweak<?>, Double>> findEntries(String query, double threshold)
    {
        this.lastQuery = query;
        this.setThreshold(threshold);

        String trimmed = query.toLowerCase().replaceFirst("#\\w+", "").trim();
        List<Map.Entry<Tweak<?>, Double>> results = this.levenshtein().apply(trimmed);

        if (trimmed.isEmpty() || trimmed.isBlank())
            results.sort(Map.Entry.comparingByKey(Tweak::compareTranslationName));

        return results;
    }

    @Override
    public void reset()
    {
        this.init();
        SearchTag.stream().forEach(SearchTag::init);
    }
}
