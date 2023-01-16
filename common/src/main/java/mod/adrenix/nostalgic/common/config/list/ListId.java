package mod.adrenix.nostalgic.common.config.list;

/**
 * This enumeration holds identifiers for each list screen available. Each screen will either be associated with a
 * tweak, or will be a general list. Different rendering and logic will be used by list screens depending on the
 * list identifier provided.
 *
 * Using identifiers helps with validation and sided list syncing.
 */

public enum ListId
{
    CUSTOM_ITEM_STACKING,
    CUSTOM_FOOD_STACKING,
    CUSTOM_FOOD_HEALTH,
    LEFT_CLICK_SPEEDS,
    RIGHT_CLICK_SPEEDS,
    FULL_BLOCK_OUTLINE,
    IGNORED_ITEM_HOLDING
}
