package mod.adrenix.nostalgic.client.config.annotation;

import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.common.config.tweak.CandyTweak;
import mod.adrenix.nostalgic.common.config.tweak.GameplayTweak;
import mod.adrenix.nostalgic.common.config.tweak.TweakType;
import mod.adrenix.nostalgic.common.config.tweak.TweakVersion;

/**
 * Helper class that defines the conditions of when an alert tag should be displayed.
 * Used in the tweak alert enumeration definition defined in {@link TweakGui}.
 *
 * This utility is to be used only on the client.
 */

public abstract class AlertCondition
{
    /**
     * An alert tag will be displayed by window title tweaks if the enable window title tweak is disabled.
     */
    public static boolean isWindowTitleDisabled()
    {
        return !((boolean) TweakClientCache.get(CandyTweak.ENABLE_WINDOW_TITLE).getValue());
    }

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
     * Checks if the user has old light rendering disabled and disabled vanilla brightness enabled.
     *
     * Having disabled vanilla brightness enabled without old light rendering enabled will not work since the vanilla
     * lightmap calculations are not impacted by the brightness tweak.
     */
    public static boolean isBrightnessConflict()
    {
        boolean isOldLight = (boolean) TweakClientCache.get(CandyTweak.LIGHT_RENDERING).getValue();
        boolean isDisabledBrightness = (boolean) TweakClientCache.get(CandyTweak.DISABLE_BRIGHTNESS).getValue();

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
        boolean isDarkVoidHeight = (boolean) TweakClientCache.get(CandyTweak.DARK_VOID_HEIGHT).getValue();
        boolean isBlueVoidOverride = (boolean) TweakClientCache.get(CandyTweak.BLUE_VOID_OVERRIDE).getValue();

        return isDarkVoidHeight && isBlueVoidOverride;
    }

    /**
     * Checks if the user has dynamic fog or custom fog tweaks enabled.
     * Having either enabled will not work since dynamic and custom fog will override universal fog.
     */
    public static boolean isUniversalFogConflict()
    {
        TweakVersion.FogColor fogColor = (TweakVersion.FogColor) TweakClientCache.get(CandyTweak.UNIVERSAL_FOG_COLOR).getValue();
        boolean isFogCustom = (boolean) TweakClientCache.get(CandyTweak.CUSTOM_TERRAIN_FOG).getValue();
        boolean isFogDynamic = (boolean) TweakClientCache.get(CandyTweak.DYNAMIC_FOG_COLOR).getValue();

        return fogColor != TweakVersion.FogColor.DISABLED && (isFogCustom || isFogDynamic);
    }

    /**
     * Checks if the user has dynamic sky or custom sky tweaks enabled.
     * Having either enabled will not work since dynamic and custom sky will override universal sky.
     */
    public static boolean isUniversalSkyConflict()
    {
        TweakVersion.SkyColor skyColor = (TweakVersion.SkyColor) TweakClientCache.get(CandyTweak.UNIVERSAL_SKY_COLOR).getValue();
        boolean isSkyCustom = (boolean) TweakClientCache.get(CandyTweak.CUSTOM_WORLD_SKY).getValue();
        boolean isSkyDynamic = (boolean) TweakClientCache.get(CandyTweak.DYNAMIC_SKY_COLOR).getValue();

        return skyColor != TweakVersion.SkyColor.DISABLED && (isSkyCustom || isSkyDynamic);
    }

    /**
     * Checks if the user has custom fog enabled.
     * Having custom fog enabled will override dynamic fog.
     */
    public static boolean isDynamicFogConflict()
    {
        boolean isFogCustom = (boolean) TweakClientCache.get(CandyTweak.CUSTOM_TERRAIN_FOG).getValue();
        boolean isFogDynamic = (boolean) TweakClientCache.get(CandyTweak.DYNAMIC_FOG_COLOR).getValue();

        return isFogCustom && isFogDynamic;
    }

    /**
     * Checks if the user has custom sky enabled.
     * Having custom sky enabled will override dynamic sky.
     */
    public static boolean isDynamicSkyConflict()
    {
        boolean isSkyCustom = (boolean) TweakClientCache.get(CandyTweak.CUSTOM_WORLD_SKY).getValue();
        boolean isSkyDynamic = (boolean) TweakClientCache.get(CandyTweak.DYNAMIC_SKY_COLOR).getValue();

        return isSkyCustom && isSkyDynamic;
    }

    /**
     * Checks if the user has tweak disable hunger off.
     * If so, custom food health will not work.
     */
    public static boolean isCustomFoodHealthConflict()
    {
        return !((boolean) TweakClientCache.get(GameplayTweak.HUNGER).getValue());
    }

    /**
     * Checks if the user has the tweak old food stacking off.
     * If so, custom food stacking will not work.
     */
    public static boolean isCustomFoodStackingConflict()
    {
        return !((boolean) TweakClientCache.get(GameplayTweak.FOOD_STACKING).getValue());
    }
}
