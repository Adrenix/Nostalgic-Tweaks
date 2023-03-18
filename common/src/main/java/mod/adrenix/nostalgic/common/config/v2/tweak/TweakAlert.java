package mod.adrenix.nostalgic.common.config.v2.tweak;

import mod.adrenix.nostalgic.common.config.tweak.TweakType;
import mod.adrenix.nostalgic.common.config.tweak.TweakVersion;

/**
 * Helper class that defines the conditions of when an alert tag should be displayed.
 * This utility is only used by the client.
 */

public abstract class TweakAlert
{
    /**
     * An alert tag will be displayed by window title tweaks if the enable window title tweak is disabled.
     */
    public static boolean isWindowTitleDisabled()
    {
        return !CandyTweak.ENABLE_WINDOW_TITLE.getCacheValue();
    }

    /**
     * Checks if the user has selected the modern shield position while the old inventory tweak is enabled.
     *
     * The inventory slot cannot be in the modern position since it will be too close to the crafting grid and overlap
     * the wider black player box.
     */
    public static boolean isShieldConflict()
    {
        boolean isModernShield = CandyTweak.INVENTORY_SHIELD.getCacheValue() == TweakType.InventoryShield.MODERN;
        boolean isOldInventory = CandyTweak.OLD_INVENTORY.getCacheValue();

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
        boolean isOldColor = CandyTweak.OLD_LIGHT_COLOR.getCacheValue();
        boolean isOldLight = CandyTweak.OLD_LIGHT_RENDERING.getCacheValue();

        return !isOldColor && isOldLight;
    }

    /**
     * Checks if the user has old light rendering disabled and disabled vanilla brightness enabled.
     *
     * Having disabled vanilla brightness enabled without old light rendering enabled will not work since the vanilla
     * lightmap calculations are not impacted by the brightness tweak.
     */
    public static boolean isBrightnessConflict()
    {
        boolean isOldLight = CandyTweak.OLD_LIGHT_RENDERING.getCacheValue();
        boolean isDisabledBrightness = CandyTweak.DISABLE_BRIGHTNESS.getCacheValue();

        return !isOldLight && isDisabledBrightness;
    }

    /**
     * Checks if the user has both the dark void height and blue void override tweaks enabled.
     *
     * Having both enabled will not work since the blue void will always override the dark void. Therefore, the dark
     * void cannot render.
     */
    public static boolean isVoidConflict()
    {
        boolean isDarkVoidHeight = CandyTweak.OLD_DARK_VOID_HEIGHT.getCacheValue();
        boolean isBlueVoidOverride = CandyTweak.OLD_BLUE_VOID_OVERRIDE.getCacheValue();

        return isDarkVoidHeight && isBlueVoidOverride;
    }

    /**
     * Checks if the user has dynamic fog or custom fog tweaks enabled.
     * Having either enabled will not work since dynamic and custom fog will override universal fog.
     */
    public static boolean isUniversalFogConflict()
    {
        TweakVersion.FogColor fogColor = CandyTweak.UNIVERSAL_FOG_COLOR.getCacheValue();
        boolean isFogCustom = CandyTweak.CUSTOM_TERRAIN_FOG.getCacheValue();
        boolean isFogDynamic = CandyTweak.OLD_DYNAMIC_FOG_COLOR.getCacheValue();

        return fogColor != TweakVersion.FogColor.DISABLED && (isFogCustom || isFogDynamic);
    }

    /**
     * Checks if the user has dynamic sky or custom sky tweaks enabled.
     * Having either enabled will not work since dynamic and custom sky will override universal sky.
     */
    public static boolean isUniversalSkyConflict()
    {
        TweakVersion.SkyColor skyColor = CandyTweak.UNIVERSAL_SKY_COLOR.getCacheValue();
        boolean isSkyCustom = CandyTweak.CUSTOM_WORLD_SKY.getCacheValue();
        boolean isSkyDynamic = CandyTweak.OLD_DYNAMIC_SKY_COLOR.getCacheValue();

        return skyColor != TweakVersion.SkyColor.DISABLED && (isSkyCustom || isSkyDynamic);
    }

    /**
     * Checks if the user has custom fog enabled.
     * Having custom fog enabled will override dynamic fog.
     */
    public static boolean isDynamicFogConflict()
    {
        boolean isFogCustom = CandyTweak.CUSTOM_TERRAIN_FOG.getCacheValue();
        boolean isFogDynamic = CandyTweak.OLD_DYNAMIC_FOG_COLOR.getCacheValue();

        return isFogCustom && isFogDynamic;
    }

    /**
     * Checks if the user has custom sky enabled.
     * Having custom sky enabled will override dynamic sky.
     */
    public static boolean isDynamicSkyConflict()
    {
        boolean isSkyCustom = CandyTweak.CUSTOM_WORLD_SKY.getCacheValue();
        boolean isSkyDynamic = CandyTweak.OLD_DYNAMIC_SKY_COLOR.getCacheValue();

        return isSkyCustom && isSkyDynamic;
    }

    /**
     * Checks if the user has tweak disable hunger off.
     * If so, custom food health will not work.
     */
    public static boolean isCustomFoodHealthConflict()
    {
        return !GameplayTweak.DISABLE_HUNGER.getCacheValue();
    }

    /**
     * Checks if the user has the tweak old food stacking off.
     * If so, custom food stacking will not work.
     */
    public static boolean isCustomFoodStackingConflict()
    {
        return !GameplayTweak.OLD_FOOD_STACKING.getCacheValue();
    }
}
