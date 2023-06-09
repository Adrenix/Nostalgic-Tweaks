package mod.adrenix.nostalgic.common.config.list;

/**
 * This enumeration is opposite to the {@link ListFilter} enumeration. The values in this class instruct list screens
 * to only include certain items, regardless of filtering requests.
 */

public enum ListInclude
{
    ALL,
    ONLY_TOOLS,
    ONLY_ITEMS,
    ONLY_BLOCKS,
    ONLY_EDIBLE,
    NO_TOOLS,
    NO_ITEMS,
    NO_BLOCKS
}
