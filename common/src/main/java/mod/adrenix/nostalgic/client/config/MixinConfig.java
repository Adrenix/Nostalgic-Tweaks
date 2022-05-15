package mod.adrenix.nostalgic.client.config;

import mod.adrenix.nostalgic.client.config.feature.*;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class MixinConfig
{
    /* Configuration Injection References */

    private static final ClientConfig.Animation ANIMATION = CommonRegistry.getAnimation();
    private static final ClientConfig.EyeCandy CANDY = CommonRegistry.getCandy();
    private static final ClientConfig.Sound SOUND = CommonRegistry.getSound();
    private static final ClientConfig.Swing SWING = CommonRegistry.getSwing();
    private static final ClientConfig CONFIG = CommonRegistry.getRoot();
    private static DefaultConfig.VERSION getVersion(@Nullable IFeature feature, DefaultConfig.VERSION current)
    {
        return !isModEnabled(feature) ? DefaultConfig.VERSION.MODERN : current;
    }

    public static boolean isModEnabled(@Nullable IFeature feature)
    {
        if (feature != null)
            feature.setEnabled();
        return CONFIG.isModEnabled;
    }

    /* Swing Speed Injection Helpers */

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
        public static boolean isOverridingSpeeds() { return !isModEnabled(SwingFeature.OVERRIDE_SPEEDS) || SWING.overrideSpeeds; }
        public static boolean isOverridingHaste() { return isModEnabled(null) && SWING.haste != DefaultConfig.Swing.GLOBAL; }
        public static boolean isSpeedGlobal() { return SWING.global != DefaultConfig.Swing.GLOBAL; }
        public static int getFatigueSpeed() { return isSpeedGlobal() ? SWING.global : SWING.fatigue; }
        public static int getHasteSpeed() { return isSpeedGlobal() ? SWING.global : SWING.haste; }
        public static int getSwingSpeed() { return getSwingSpeed(Minecraft.getInstance().player); }
        public static int getGlobalSpeed() { return SWING.global; }
    }

    /* Sound Injection Helpers */

    public static class Sound
    {
        public static boolean oldAttack() { return isModEnabled(SoundFeature.OLD_ATTACK) && SOUND.oldAttack; }
        public static boolean oldDamage() { return isModEnabled(SoundFeature.OLD_HURT) && SOUND.oldHurt; }
        public static boolean oldFall() { return isModEnabled(SoundFeature.OLD_FALL) && SOUND.oldFall; }
        public static boolean oldStep() { return isModEnabled(SoundFeature.OLD_STEP) && SOUND.oldStep; }
        public static boolean oldXP() { return isModEnabled(SoundFeature.OLD_XP) && SOUND.oldXP; }
    }

    /* Eye Candy Injection Helpers */

    public static class Candy
    {
        public static boolean fixItemModelGaps() { return isModEnabled(CandyFeature.FIX_ITEM_MODEL_GAP) && CANDY.fixItemModelGap; }
        public static boolean removeTitleModLoaderText() { return isModEnabled(CandyFeature.TITLE_MOD_LOADER_TEXT) && CANDY.removeTitleModLoaderText; }
        public static boolean oldMixedExplosionParticles() { return isModEnabled(CandyFeature.MIXED_EXPLOSION_PARTICLES) && CANDY.oldMixedExplosionParticles; }
        public static boolean oldNoCriticalHitParticles() { return isModEnabled(CandyFeature.NO_CRIT_PARTICLES) && CANDY.oldNoCritParticles; }
        public static boolean oldNoEnchantHitParticles() { return isModEnabled(CandyFeature.NO_MAGIC_HIT_PARTICLES) && CANDY.oldNoMagicHitParticles; }
        public static boolean oldPlainSelectedItemName() { return isModEnabled(CandyFeature.PLAIN_SELECTED_ITEM_NAME) && CANDY.oldPlainSelectedItemName; }
        public static boolean oldNoSelectedItemName() { return isModEnabled(CandyFeature.NO_SELECTED_ITEM_NAME) && CANDY.oldNoSelectedItemName; }
        public static boolean oldExplosionParticles() { return isModEnabled(CandyFeature.EXPLOSION_PARTICLES) && CANDY.oldExplosionParticles; }
        public static boolean oldNoDamageParticles() { return isModEnabled(CandyFeature.NO_DAMAGE_PARTICLES) && CANDY.oldNoDamageParticles; }
        public static boolean oldOpaqueExperience() { return isModEnabled(CandyFeature.OPAQUE_EXPERIENCE) && CANDY.oldOpaqueExperience; }
        public static boolean oldDurabilityColors() { return isModEnabled(CandyFeature.DURABILITY_COLORS) && CANDY.oldDurabilityColors; }
        public static boolean oldSunriseSunsetFog() { return isModEnabled(CandyFeature.SUNRISE_SUNSET_FOG) && CANDY.oldSunriseSunsetFog; }
        public static boolean oldBlueVoidOverride() { return isModEnabled(CandyFeature.BLUE_VOID_OVERRIDE) && CANDY.oldBlueVoidOverride; }
        public static boolean oldFlatEnchantment() { return isModEnabled(CandyFeature.FLAT_ENCHANTED_ITEMS) && oldFloatingItems() && CANDY.old2dEnchantedItems; }
        public static boolean oldTitleBackground() { return isModEnabled(CandyFeature.TITLE_BACKGROUND) && CANDY.oldTitleBackground; }
        public static boolean oldNetherLighting() { return isModEnabled(CandyFeature.NETHER_LIGHTING) && CANDY.oldNetherLighting; }
        public static boolean oldSunriseAtNorth() { return isModEnabled(CandyFeature.SUNRISE_AT_NORTH) && CANDY.oldSunriseAtNorth; }
        public static boolean oldVersionOverlay() { return isModEnabled(CandyFeature.VERSION_OVERLAY) && CANDY.oldVersionOverlay; }
        public static boolean oldLoadingScreens() { return isModEnabled(CandyFeature.LOADING_SCREENS) && CANDY.oldLoadingScreens; }
        public static boolean oldSweepParticles() { return isModEnabled(CandyFeature.SWEEP) && CANDY.oldSweepParticles; }
        public static boolean oldFloatingItems() { return isModEnabled(CandyFeature.FLAT_ITEMS) && CANDY.old2dItems; }
        public static boolean oldFlatThrowing() { return isModEnabled(CandyFeature.FLAT_THROWN_ITEMS) && CANDY.old2dThrownItems; }
        public static boolean oldLightFlicker() { return isModEnabled(CandyFeature.LIGHT_FLICKER) && CANDY.oldLightFlicker; }
        public static boolean oldSquareBorder() { return isModEnabled(CandyFeature.SQUARE_BORDER) && CANDY.oldSquareBorder; }
        public static boolean oldLogoOutline() { return isModEnabled(CandyFeature.LOGO_OUTLINE) && CANDY.oldLogoOutline; }
        public static boolean oldButtonHover() { return isModEnabled(CandyFeature.BUTTON_HOVER) && CANDY.oldButtonHover; }
        public static boolean oldItemHolding() { return isModEnabled(CandyFeature.ITEM_HOLDING) && CANDY.oldItemHolding; }
        public static boolean oldItemMerging() { return isModEnabled(CandyFeature.ITEM_MERGING) && CANDY.oldItemMerging; }
        public static boolean oldTitleScreen() { return isModEnabled(CandyFeature.TITLE_SCREEN) && CANDY.oldTitleScreen; }
        public static boolean oldFlatFrames() { return isModEnabled(CandyFeature.FLAT_FRAMES) && CANDY.old2dFrames; }
        public static boolean oldTerrainFog() { return isModEnabled(CandyFeature.TERRAIN_FOG) && CANDY.oldTerrainFog; }
        public static boolean oldHorizonFog() { return isModEnabled(CandyFeature.HORIZON_FOG) && CANDY.oldHorizonFog; }
        public static boolean oldAlphaLogo() { return isModEnabled(CandyFeature.ALPHA_LOGO) && CANDY.oldAlphaLogo; }
        public static boolean oldNetherFog() { return isModEnabled(CandyFeature.NETHER_FOG) && CANDY.oldNetherFog; }
        public static boolean oldTooltips() { return !isModEnabled(CandyFeature.TOOLTIP_BOXES) || !CANDY.oldTooltipBoxes; }
        public static boolean oldLighting() { return isModEnabled(CandyFeature.LIGHTING) && CANDY.oldLighting; }

        public static DefaultConfig.VERSION getSkyColor() { return getVersion(CandyFeature.SKY_COLOR, CANDY.oldSkyColor); }
        public static DefaultConfig.VERSION getFogColor() { return getVersion(CandyFeature.FOG_COLOR, CANDY.oldFogColor); }
        public static DefaultConfig.VERSION getBlueVoid() { return getVersion(CandyFeature.BLUE_VOID, CANDY.oldBlueVoid); }

        private static String parseColor(String text)
        {
            text = text.replaceAll("%v", SharedConstants.getCurrentVersion().getName());
            text = text.replaceAll("%", "§");
            return text;
        }

        public static String getOverlayText() { return parseColor(CANDY.oldOverlayText); }
        public static String getVersionText() { return parseColor(CANDY.titleVersionText); }

        public static int getCloudHeight() { return isModEnabled(CandyFeature.CLOUD_HEIGHT) ? CANDY.oldCloudHeight : 192; }
    }

    /* Animation Injection Helpers */

    public static class Animation
    {
        public static float getArmSwayIntensity()
        {
            float mirror = shouldMirrorArmSway() ? -1.0F : 1.0F;
            return isModEnabled(null) ? (((float) ANIMATION.armSwayIntensity) * mirror / 100.0F) : 1.0F;
        }

        public static boolean shouldMirrorArmSway() { return isModEnabled(null) && ANIMATION.armSwayMirror; }
        public static boolean oldVerticalBobbing() { return isModEnabled(AnimationFeature.BOB_VERTICAL) && ANIMATION.oldVerticalBobbing; }
        public static boolean oldCollideBobbing() { return isModEnabled(AnimationFeature.COLLIDE_BOB) && ANIMATION.oldCollideBobbing; }
        public static boolean oldToolExplosion() { return isModEnabled(AnimationFeature.TOOL_EXPLODE) && ANIMATION.oldToolExplosion; }
        public static boolean oldSwingDropping() { return isModEnabled(AnimationFeature.SWING_DROP) && ANIMATION.oldSwingDropping; }
        public static boolean oldSkeletonArms() { return isModEnabled(AnimationFeature.SKELETON_ARMS) && ANIMATION.oldSkeletonArms; }
        public static boolean oldItemCooldown() { return isModEnabled(AnimationFeature.COOLDOWN) && ANIMATION.oldItemCooldown; }
        public static boolean oldItemReequip() { return isModEnabled(AnimationFeature.REEQUIP) && ANIMATION.oldItemReequip; }
        public static boolean oldZombieArms() { return isModEnabled(AnimationFeature.ZOMBIE_ARMS) && ANIMATION.oldZombieArms; }
        public static boolean oldSneaking() { return isModEnabled(AnimationFeature.SNEAK_SMOOTH) && ANIMATION.oldSneaking; }
        public static boolean oldArmSway() { return isModEnabled(AnimationFeature.ARM_SWAY) && ANIMATION.oldArmSway; }
        public static boolean oldSwing() { return isModEnabled(AnimationFeature.ITEM_SWING) && ANIMATION.oldSwing; }
    }
}
