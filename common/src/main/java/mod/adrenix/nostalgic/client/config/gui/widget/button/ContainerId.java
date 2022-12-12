package mod.adrenix.nostalgic.client.config.gui.widget.button;

/**
 * Unique identifiers for container buttons. Useful if the expansion state of a container button is needed somewhere
 * without access to a container button instance.
 *
 * Since the expansion tracker map is static, each container button needs a unique identifier. Having a group
 * identification enumeration list helps alleviate this problem.
 */

public enum ContainerId
{
    BINDINGS_CONFIG,
    DEFAULT_SCREEN_CONFIG,
    GENERAL_CONFIG,
    NOTIFY_CONFIG,
    OVERRIDE_CONFIG,
    SEARCH_TAGS_CONFIG,
    SHORTCUTS_CONFIG,
    TITLE_TAGS_CONFIG,
    TREE_CONFIG,
    ROW_CONFIG,
    LIST_HELP,
    DEFAULT_ITEMS,
    SAVED_ITEMS,
    ALL_ITEMS
}
