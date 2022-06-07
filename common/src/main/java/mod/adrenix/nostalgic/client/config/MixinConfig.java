package mod.adrenix.nostalgic.client.config;

import mod.adrenix.nostalgic.client.config.tweak.*;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class MixinConfig
{
    /* Configuration Mixin References */

    private static final ClientConfig.Animation ANIMATION = CommonRegistry.getAnimation();
    private static final ClientConfig.EyeCandy CANDY = CommonRegistry.getCandy();
    private static final ClientConfig.Sound SOUND = CommonRegistry.getSound();
    private static final ClientConfig.Swing SWING = CommonRegistry.getSwing();
    private static final ClientConfig CONFIG = CommonRegistry.getRoot();
    private static DefaultConfig.VERSION getVersion(@Nullable ITweak tweak, DefaultConfig.VERSION current)
    {
        return !isModEnabled(tweak) ? DefaultConfig.VERSION.MODERN : current;
    }

    public static boolean isModEnabled(@Nullable ITweak tweak)
    {
        if (tweak != null)
            tweak.setEnabled();
        return CONFIG.isModEnabled;
    }

    /* Swing Speed Mixin Options */

    public static class Swing
    {
        public static int getSpeedFromItem(Item item)
        {
            Map.Entry<String, Integer> entry = CustomSwings.getEntryFromItem(item);

            if (isSpeedGlobal())
                return SWING.global;
            else if (entry != null)
                return entry.getValue();
            else if (item instanceof SwordItem)
                return SWING.sword;
            else if (item instanceof BlockItem)
                return SWING.block;
            else if (item instanceof DiggerItem)
                return SWING.tool;
            return SWING.item;
        }

        public static int getSwingSpeed(AbstractClientPlayer player)
        {
            if (isModEnabled(null))
                return getSpeedFromItem(player.getMainHandItem().getItem());
            return DefaultConfig.Swing.NEW_SPEED;
        }

        public static boolean isOverridingFatigue() { return isModEnabled(null) && SWING.fatigue != DefaultConfig.Swing.GLOBAL; }
        public static boolean isOverridingSpeeds() { return !isModEnabled(SwingTweak.OVERRIDE_SPEEDS) || SWING.overrideSpeeds; }
        public static boolean isOverridingHaste() { return isModEnabled(null) && SWING.haste != DefaultConfig.Swing.GLOBAL; }
        public static boolean isSpeedGlobal() { return SWING.global != DefaultConfig.Swing.GLOBAL; }
        public static int getFatigueSpeed() { return isSpeedGlobal() ? SWING.global : SWING.fatigue; }
        public static int getHasteSpeed() { return isSpeedGlobal() ? SWING.global : SWING.haste; }
        public static int getSwingSpeed() { return getSwingSpeed(Minecraft.getInstance().player); }
        public static int getGlobalSpeed() { return SWING.global; }
    }

    /* Sound Mixin Options */

    public static class Sound
    {
        public static boolean oldAttack() { return isModEnabled(SoundTweak.OLD_ATTACK) && SOUND.oldAttack; }
        public static boolean oldDamage() { return isModEnabled(SoundTweak.OLD_HURT) && SOUND.oldHurt; }
        public static boolean oldFall() { return isModEnabled(SoundTweak.OLD_FALL) && SOUND.oldFall; }
        public static boolean oldStep() { return isModEnabled(SoundTweak.OLD_STEP) && SOUND.oldStep; }
        public static boolean oldXP() { return isModEnabled(SoundTweak.OLD_XP) && SOUND.oldXP; }
    }

    /* Eye Candy Mixin Options */

    public static class Candy
    {
        /* Boolean Tweaks */

        // Title Screen Candy
        public static boolean overrideTitleScreen() { return isModEnabled(CandyTweak.OVERRIDE_TITLE_SCREEN) && CANDY.overrideTitleScreen; }
        public static boolean removeAccessibilityButton() { return isModEnabled(CandyTweak.TITLE_ACCESSIBILITY) && CANDY.removeTitleAccessibilityButton; }
        public static boolean removeTitleModLoaderText() { return isModEnabled(CandyTweak.TITLE_MOD_LOADER_TEXT) && CANDY.removeTitleModLoaderText; }
        public static boolean removeLanguageButton() { return isModEnabled(CandyTweak.TITLE_LANGUAGE) && CANDY.removeTitleLanguageButton; }
        public static boolean titleBottomLeftText() { return isModEnabled(CandyTweak.TITLE_BOTTOM_LEFT_TEXT) && CANDY.titleBottomLeftText; }
        public static boolean oldTitleBackground() { return isModEnabled(CandyTweak.TITLE_BACKGROUND) && CANDY.oldTitleBackground; }
        public static boolean oldLogoOutline() { return isModEnabled(CandyTweak.LOGO_OUTLINE) && CANDY.oldLogoOutline; }
        public static boolean oldAlphaLogo() { return isModEnabled(CandyTweak.ALPHA_LOGO) && CANDY.oldAlphaLogo; }

        // Interface Candy
        public static boolean oldPlainSelectedItemName() { return isModEnabled(CandyTweak.PLAIN_SELECTED_ITEM_NAME) && CANDY.oldPlainSelectedItemName; }
        public static boolean oldNoSelectedItemName() { return isModEnabled(CandyTweak.NO_SELECTED_ITEM_NAME) && CANDY.oldNoSelectedItemName; }
        public static boolean oldDurabilityColors() { return isModEnabled(CandyTweak.DURABILITY_COLORS) && CANDY.oldDurabilityColors; }
        public static boolean oldNoItemTooltips() { return isModEnabled(CandyTweak.NO_ITEM_TOOLTIPS) && CANDY.oldNoItemTooltips; }
        public static boolean oldVersionOverlay() { return isModEnabled(CandyTweak.VERSION_OVERLAY) && CANDY.oldVersionOverlay; }
        public static boolean oldLoadingScreens() { return isModEnabled(CandyTweak.LOADING_SCREENS) && CANDY.oldLoadingScreens; }
        public static boolean oldButtonHover() { return isModEnabled(CandyTweak.BUTTON_HOVER) && CANDY.oldButtonHover; }
        public static boolean oldTooltips() { return !isModEnabled(CandyTweak.TOOLTIP_BOXES) || !CANDY.oldTooltipBoxes; }

        // Item Candy
        public static boolean fixItemModelGaps() { return isModEnabled(CandyTweak.FIX_ITEM_MODEL_GAP) && CANDY.fixItemModelGap; }
        public static boolean oldFloatingItems() { return isModEnabled(CandyTweak.FLAT_ITEMS) && CANDY.old2dItems; }
        public static boolean oldFlatEnchantment() { return isModEnabled(CandyTweak.FLAT_ENCHANTED_ITEMS) && oldFloatingItems() && CANDY.old2dEnchantedItems; }
        public static boolean oldFlatThrowing() { return isModEnabled(CandyTweak.FLAT_THROW_ITEMS) && CANDY.old2dThrownItems; }
        public static boolean oldItemHolding() { return isModEnabled(CandyTweak.ITEM_HOLDING) && CANDY.oldItemHolding; }
        public static boolean oldItemMerging() { return isModEnabled(CandyTweak.ITEM_MERGING) && CANDY.oldItemMerging; }
        public static boolean oldFlatFrames() { return isModEnabled(CandyTweak.FLAT_FRAMES) && CANDY.old2dFrames; }

        // Particle Candy
        public static boolean oldNoCriticalHitParticles() { return isModEnabled(CandyTweak.NO_CRIT_PARTICLES) && CANDY.oldNoCritParticles; }
        public static boolean oldMixedExplosionParticles() { return isModEnabled(CandyTweak.MIXED_EXPLOSION_PARTICLES) && CANDY.oldMixedExplosionParticles; }
        public static boolean oldNoEnchantHitParticles() { return isModEnabled(CandyTweak.NO_MAGIC_HIT_PARTICLES) && CANDY.oldNoMagicHitParticles; }
        public static boolean oldExplosionParticles() { return isModEnabled(CandyTweak.EXPLOSION_PARTICLES) && CANDY.oldExplosionParticles; }
        public static boolean oldNoDamageParticles() { return isModEnabled(CandyTweak.NO_DAMAGE_PARTICLES) && CANDY.oldNoDamageParticles; }
        public static boolean oldOpaqueExperience() { return isModEnabled(CandyTweak.OPAQUE_EXPERIENCE) && CANDY.oldOpaqueExperience; }
        public static boolean oldSweepParticles() { return isModEnabled(CandyTweak.SWEEP) && CANDY.oldSweepParticles; }

        // Lighting Candy
        public static boolean oldSmoothLighting() { return isModEnabled(CandyTweak.SMOOTH_LIGHTING) && CANDY.oldSmoothLighting; }
        public static boolean oldNetherLighting() { return isModEnabled(CandyTweak.NETHER_LIGHTING) && CANDY.oldNetherLighting; }
        public static boolean oldLeavesLighting() { return isModEnabled(CandyTweak.LEAVES_LIGHTING) && CANDY.oldLeavesLighting; }
        public static boolean oldWaterLighting() { return isModEnabled(CandyTweak.WATER_LIGHTING) && CANDY.oldWaterLighting; }
        public static boolean oldLightFlicker() { return isModEnabled(CandyTweak.LIGHT_FLICKER) && CANDY.oldLightFlicker; }
        public static boolean oldLighting() { return isModEnabled(CandyTweak.LIGHTING) && CANDY.oldLighting; }

        // World Candy
        public static boolean oldSunriseSunsetFog() { return isModEnabled(CandyTweak.SUNRISE_SUNSET_FOG) && CANDY.oldSunriseSunsetFog; }
        public static boolean oldBlueVoidOverride() { return isModEnabled(CandyTweak.BLUE_VOID_OVERRIDE) && CANDY.oldBlueVoidOverride; }
        public static boolean oldSunriseAtNorth() { return isModEnabled(CandyTweak.SUNRISE_AT_NORTH) && CANDY.oldSunriseAtNorth; }
        public static boolean oldSquareBorder() { return isModEnabled(CandyTweak.SQUARE_BORDER) && CANDY.oldSquareBorder; }
        public static boolean oldTerrainFog() { return isModEnabled(CandyTweak.TERRAIN_FOG) && CANDY.oldTerrainFog; }
        public static boolean oldHorizonFog() { return isModEnabled(CandyTweak.HORIZON_FOG) && CANDY.oldHorizonFog; }
        public static boolean oldNetherFog() { return isModEnabled(CandyTweak.NETHER_FOG) && CANDY.oldNetherFog; }
        public static boolean oldStars() { return isModEnabled(CandyTweak.STARS) && CANDY.oldStars; }

        /* Version Tweaks */

        public static DefaultConfig.VERSION getButtonLayout() { return getVersion(CandyTweak.TITLE_BUTTON_LAYOUT, CANDY.oldButtonLayout); }
        public static DefaultConfig.VERSION getSkyColor() { return getVersion(CandyTweak.SKY_COLOR, CANDY.oldSkyColor); }
        public static DefaultConfig.VERSION getFogColor() { return getVersion(CandyTweak.FOG_COLOR, CANDY.oldFogColor); }
        public static DefaultConfig.VERSION getBlueVoid() { return getVersion(CandyTweak.BLUE_VOID, CANDY.oldBlueVoid); }

        /* String Tweaks */

        private static String parseColor(String text)
        {
            text = text.replaceAll("%v", SharedConstants.getCurrentVersion().getName());
            text = text.replaceAll("%", "§");
            return text;
        }

        public static String getOverlayText() { return parseColor(CANDY.oldOverlayText); }
        public static String getVersionText() { return parseColor(CANDY.titleVersionText); }

        /* Integer Tweaks */

        public static int getCloudHeight() { return isModEnabled(CandyTweak.CLOUD_HEIGHT) ? CANDY.oldCloudHeight : 192; }
    }

    /* Animation Mixin Options */

    public static class Animation
    {
        public static float getArmSwayIntensity()
        {
            float mirror = shouldMirrorArmSway() ? -1.0F : 1.0F;
            return isModEnabled(null) ? (((float) ANIMATION.armSwayIntensity) * mirror / 100.0F) : 1.0F;
        }

        public static boolean shouldMirrorArmSway() { return isModEnabled(null) && ANIMATION.armSwayMirror; }
        public static boolean oldVerticalBobbing() { return isModEnabled(AnimationTweak.BOB_VERTICAL) && ANIMATION.oldVerticalBobbing; }
        public static boolean oldCollideBobbing() { return isModEnabled(AnimationTweak.COLLIDE_BOB) && ANIMATION.oldCollideBobbing; }
        public static boolean oldToolExplosion() { return isModEnabled(AnimationTweak.TOOL_EXPLODE) && ANIMATION.oldToolExplosion; }
        public static boolean oldSwingDropping() { return isModEnabled(AnimationTweak.SWING_DROP) && ANIMATION.oldSwingDropping; }
        public static boolean oldSkeletonArms() { return isModEnabled(AnimationTweak.SKELETON_ARMS) && ANIMATION.oldSkeletonArms; }
        public static boolean oldItemCooldown() { return isModEnabled(AnimationTweak.COOLDOWN) && ANIMATION.oldItemCooldown; }
        public static boolean oldItemReequip() { return isModEnabled(AnimationTweak.REEQUIP) && ANIMATION.oldItemReequip; }
        public static boolean oldZombieArms() { return isModEnabled(AnimationTweak.ZOMBIE_ARMS) && ANIMATION.oldZombieArms; }
        public static boolean oldSneaking() { return isModEnabled(AnimationTweak.SNEAK_SMOOTH) && ANIMATION.oldSneaking; }
        public static boolean oldArmSway() { return isModEnabled(AnimationTweak.ARM_SWAY) && ANIMATION.oldArmSway; }
        public static boolean oldSwing() { return isModEnabled(AnimationTweak.ITEM_SWING) && ANIMATION.oldSwing; }
    }
}
