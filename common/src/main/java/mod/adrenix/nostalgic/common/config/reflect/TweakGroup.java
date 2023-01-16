package mod.adrenix.nostalgic.common.config.reflect;

import mod.adrenix.nostalgic.NostalgicTweaks;

/**
 * This enumeration stores the major configuration groups for the mod. Tweaks can be further grouped into categories,
 * subcategories, and embedded subcategories. These group containers are defined in the client config metadata
 * annotations.
 *
 * Some group types are not clickable in the config screen group tab bar. For instance, the <code>ROOT</code> and
 * the <code>GUI</code> values are displayed in the <b>General</b> tab group.
 *
 * Any additional enumerations added to this file will require updates in:
 * - CommonReflect {@link mod.adrenix.nostalgic.common.config.reflect.CommonReflect} (Required)
 * - ServerReflect {@link mod.adrenix.nostalgic.server.config.reflect.ServerReflect} (Required)
 */

public enum TweakGroup
{
    /* Values */

    ROOT { @Override public String getLangKey() { return AUTO_CONFIG; } },
    SOUND { @Override public String getLangKey() { return AUTO_CONFIG + ".sound"; } },
    CANDY { @Override public String getLangKey() { return AUTO_CONFIG + ".eyeCandy"; } },
    GAMEPLAY { @Override public String getLangKey() { return AUTO_CONFIG + ".gameplay"; } },
    ANIMATION { @Override public String getLangKey() { return AUTO_CONFIG + ".animation"; } },
    SWING { @Override public String getLangKey() { return AUTO_CONFIG + ".swing"; } },
    GUI { @Override public String getLangKey() { return AUTO_CONFIG + ".gui"; } };

    /* Enumeration Interfacing */

    /**
     * Get the language file key for this group type.
     * @return A language file key.
     */
    public abstract String getLangKey();

    /* Static Fields */

    public static final String AUTO_CONFIG = "text.autoconfig." + NostalgicTweaks.MOD_ID + ".option";

    /* Static Methods */

    /**
     * Check if a group type is a manual group that does not have tweaks automatically assigned to it.
     * @param group A group type value to check.
     * @return Whether the given group type should be considered a manually constructed group.
     */
    public static boolean isManual(TweakGroup group) { return group == ROOT || group == GUI; }
}
