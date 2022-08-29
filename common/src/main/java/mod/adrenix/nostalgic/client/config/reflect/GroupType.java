package mod.adrenix.nostalgic.client.config.reflect;

import mod.adrenix.nostalgic.NostalgicTweaks;

/**
 * Any additional enumerations added to this file will require updates in:
 * - ConfigReflect (Required)
 */

public enum GroupType
{
    ROOT { @Override public String getLangKey() { return AUTO_CONFIG; } },
    SOUND { @Override public String getLangKey() { return AUTO_CONFIG + ".sound"; } },
    CANDY { @Override public String getLangKey() { return AUTO_CONFIG + ".eyeCandy"; } },
    ANIMATION { @Override public String getLangKey() { return AUTO_CONFIG + ".animation"; } },
    SWING { @Override public String getLangKey() { return AUTO_CONFIG + ".swing"; } },
    GUI { @Override public String getLangKey() { return AUTO_CONFIG + ".gui"; } };

    public abstract String getLangKey();
    public static final String AUTO_CONFIG = "text.autoconfig." + NostalgicTweaks.MOD_ID + ".option";
    public static boolean isManual(GroupType group) { return group == ROOT || group == GUI; }
}
