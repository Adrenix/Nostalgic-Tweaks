package mod.adrenix.nostalgic.client.config.annotation;

import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.tweak.CandyTweak;
import mod.adrenix.nostalgic.common.config.tweak.TweakType;

/**
 * Helper class that defines the conditions of when an alert tag should be displayed.
 * Used in the tweak alert enumeration definition defined in {@link TweakGui}.
 *
 * This utility is to be used only on the client.
 */

public abstract class AlertCondition
{
    /**
     * Checks if the user has selected the modern shield position while the old inventory tweak is enabled.
     *
     * The inventory slot cannot be in the modern position since it will be too close to the crafting grid and overlap
     * the wider black player box.
     */
    public static boolean isShieldConflict()
    {
        boolean isModernShield = TweakClientCache.get(CandyTweak.INVENTORY_SHIELD).getValue() == TweakType.InventoryShield.MODERN;
        boolean isOldInventory = (boolean) TweakClientCache.get(CandyTweak.OLD_INVENTORY).getValue();

        return isModernShield && isOldInventory;
    }

    /**
     * Checks if the user has disabled old light colors while keeping old light rendering enabled.
     *
     * Having the modern light color map with old light rendering will result in wrong colors since skylight is replaced
     * with block light. For example, the nighttime blue color will look orange instead.
     */
    public static boolean isLightConflict()
    {
        boolean isOldColor = (boolean) TweakClientCache.get(CandyTweak.LIGHT_COLOR).getValue();
        boolean isOldLight = (boolean) TweakClientCache.get(CandyTweak.LIGHT_RENDERING).getValue();

        return !isOldColor && isOldLight;
    }

    /**
     * Checks if the user has both the dark void height and blue void override tweaks enabled.
     *
     * Having both enabled will not work since the blue void will always override the dark void. Therefore, the dark
     * void cannot render.
     */
    public static boolean isVoidConflict()
    {
        boolean isDarkVoidHeight = (boolean) TweakClientCache.get(CandyTweak.DARK_VOID_HEIGHT).getValue();
        boolean isBlueVoidOverride = (boolean) TweakClientCache.get(CandyTweak.BLUE_VOID_OVERRIDE).getValue();

        return isDarkVoidHeight && isBlueVoidOverride;
    }
}
