package mod.adrenix.nostalgic.client.config;

import com.google.common.collect.Maps;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.annotation.TweakEntry;
import mod.adrenix.nostalgic.client.config.tweak.*;
import mod.adrenix.nostalgic.client.config.gui.screen.SettingsScreen;
import mod.adrenix.nostalgic.client.config.reflect.StatusType;

import java.util.Map;

@Config(name = NostalgicTweaks.MOD_ID)
public class ClientConfig implements ConfigData
{
    @TweakEntry.Gui.Ignore public static final int MIN = 0;
    @TweakEntry.Gui.Ignore public static final int MAX = 16;
    @TweakEntry.Gui.Ignore public static final String ROOT_KEY = "isModEnabled";

    @TweakEntry.Gui.Client
    @TweakEntry.Gui.NoTooltip
    @TweakEntry.Gui.EntryStatus(status = StatusType.OKAY)
    public boolean isModEnabled = true;

    @TweakEntry.Gui.Ignore
    public Sound sound = new Sound();
    public static class Sound
    {
        @TweakEntry.Gui.Server
        @TweakEntry.Gui.EntryStatus
        public boolean oldAttack = DefaultConfig.Sound.OLD_ATTACK;
        static { SoundTweak.OLD_ATTACK.setKey("oldAttack"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        public boolean oldHurt = DefaultConfig.Sound.OLD_HURT;
        static { SoundTweak.OLD_HURT.setKey("oldHurt"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        public boolean oldFall = DefaultConfig.Sound.OLD_FALL;
        static { SoundTweak.OLD_FALL.setKey("oldFall"); }

        @TweakEntry.Gui.Server
        @TweakEntry.Gui.EntryStatus
        public boolean oldStep = DefaultConfig.Sound.OLD_STEP;
        static { SoundTweak.OLD_STEP.setKey("oldStep"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.IgnoreDisable
        public boolean oldXP = DefaultConfig.Sound.OLD_XP;
        static { SoundTweak.OLD_XP.setKey("oldXP"); }
    }

    @TweakEntry.Gui.Ignore
    public EyeCandy eyeCandy = new EyeCandy();
    public static class EyeCandy
    {
        /**
         * Title Screen Candy
         */

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus(status = StatusType.FAIL)
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.TITLE_CANDY)
        @TweakEntry.Gui.Placement(pos = TweakEntry.Gui.Position.TOP, order = 1)
        public boolean overrideTitleScreen = DefaultConfig.Candy.OVERRIDE_TITLE_SCREEN;
        static { CandyTweak.OVERRIDE_TITLE_SCREEN.setKey("overrideTitleScreen"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.TITLE_CANDY)
        public boolean oldAlphaLogo = DefaultConfig.Candy.OLD_ALPHA_LOGO;
        static { CandyTweak.ALPHA_LOGO.setKey("oldAlphaLogo"); }

        @TweakEntry.Gui.New
        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.TITLE_CANDY)
        public TweakVersion.GENERIC oldButtonLayout = DefaultConfig.Candy.TITLE_BUTTON_LAYOUT;
        static { CandyTweak.TITLE_BUTTON_LAYOUT.setKey("oldButtonLayout"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.TITLE_CANDY)
        public boolean oldLogoOutline = DefaultConfig.Candy.OLD_LOGO_OUTLINE;
        static { CandyTweak.LOGO_OUTLINE.setKey("oldLogoOutline"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.TITLE_CANDY)
        public boolean oldTitleBackground = DefaultConfig.Candy.OLD_TITLE_BACKGROUND;
        static { CandyTweak.TITLE_BACKGROUND.setKey("oldTitleBackground"); }

        @TweakEntry.Gui.New
        @TweakEntry.Gui.Client
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.TITLE_CANDY)
        public boolean uncapTitleFPS = DefaultConfig.Candy.UNCAP_TITLE_FPS;
        static { CandyTweak.UNCAP_TITLE_FPS.setKey("uncapTitleFPS"); }

        @TweakEntry.Gui.New
        @TweakEntry.Gui.Client
        @TweakEntry.Gui.IgnoreDisable
        @TweakEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.TITLE_CANDY)
        @TweakEntry.Gui.Placement(pos = TweakEntry.Gui.Position.BOTTOM, order = 1)
        public boolean removeTitleAccessibilityButton = DefaultConfig.Candy.REMOVE_TITLE_ACCESSIBILITY;
        static { CandyTweak.TITLE_ACCESSIBILITY.setKey("removeTitleAccessibilityButton"); }

        @TweakEntry.Gui.New
        @TweakEntry.Gui.Client
        @TweakEntry.Gui.IgnoreDisable
        @TweakEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.TITLE_CANDY)
        @TweakEntry.Gui.Placement(pos = TweakEntry.Gui.Position.BOTTOM, order = 2)
        public boolean removeTitleLanguageButton = DefaultConfig.Candy.REMOVE_TITLE_LANGUAGE;
        static { CandyTweak.TITLE_LANGUAGE.setKey("removeTitleLanguageButton"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.TITLE_CANDY)
        @TweakEntry.Gui.Placement(pos = TweakEntry.Gui.Position.BOTTOM, order = 3)
        public boolean removeTitleModLoaderText = DefaultConfig.Candy.REMOVE_TITLE_MOD_LOADER_TEXT;
        static { CandyTweak.TITLE_MOD_LOADER_TEXT.setKey("removeTitleModLoaderText"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.IgnoreDisable
        @TweakEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.TITLE_CANDY)
        @TweakEntry.Gui.Placement(pos = TweakEntry.Gui.Position.BOTTOM, order = 4)
        public boolean titleBottomLeftText = DefaultConfig.Candy.TITLE_BOTTOM_LEFT_TEXT;
        static { CandyTweak.TITLE_BOTTOM_LEFT_TEXT.setKey("titleBottomLeftText"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.TITLE_CANDY)
        @TweakEntry.Gui.Placement(pos = TweakEntry.Gui.Position.BOTTOM, order = 5)
        public String titleVersionText = DefaultConfig.Candy.TITLE_VERSION_TEXT;

        /**
         * Interface Candy
         */

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.INTERFACE_CANDY)
        public String oldOverlayText = DefaultConfig.Candy.OLD_OVERLAY_TEXT;

        @TweakEntry.Gui.New
        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @TweakEntry.Gui.Placement(pos = TweakEntry.Gui.Position.TOP, order = 1)
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.INTERFACE_CANDY)
        public TweakVersion.OVERLAY oldLoadingOverlay = DefaultConfig.Candy.OLD_LOADING_OVERLAY;
        static { CandyTweak.LOADING_OVERLAY.setKey("oldLoadingOverlay"); }

        @TweakEntry.Gui.New
        @TweakEntry.Gui.Client
        @TweakEntry.Gui.IgnoreDisable
        @TweakEntry.Gui.Placement(pos = TweakEntry.Gui.Position.TOP, order = 2)
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.INTERFACE_CANDY)
        public boolean removeLoadingBar = DefaultConfig.Candy.REMOVE_LOADING_BAR;
        static { CandyTweak.REMOVE_LOADING_BAR.setKey("removeLoadingBar"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.INTERFACE_CANDY)
        public boolean oldVersionOverlay = DefaultConfig.Candy.OLD_VERSION_OVERLAY;
        static { CandyTweak.VERSION_OVERLAY.setKey("oldVersionOverlay"); }

        @TweakEntry.Gui.New
        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.INTERFACE_CANDY)
        public boolean oldChatInput = DefaultConfig.Candy.OLD_CHAT_INPUT;
        static { CandyTweak.CHAT_INPUT.setKey("oldChatInput"); }

        @TweakEntry.Gui.New
        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.INTERFACE_CANDY)
        public boolean oldChatBox = DefaultConfig.Candy.OLD_CHAT_BOX;
        static { CandyTweak.CHAT_BOX.setKey("oldChatBox"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus(status = StatusType.FAIL)
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.INTERFACE_CANDY)
        public boolean oldButtonHover = DefaultConfig.Candy.OLD_BUTTON_HOVER;
        static { CandyTweak.BUTTON_HOVER.setKey("oldButtonHover"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus(status = StatusType.FAIL)
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.INTERFACE_CANDY)
        public boolean oldTooltipBoxes = DefaultConfig.Candy.OLD_TOOLTIP_BOXES;
        static { CandyTweak.TOOLTIP_BOXES.setKey("oldTooltipBoxes"); }

        @TweakEntry.Gui.New
        @TweakEntry.Gui.Client
        @TweakEntry.Gui.IgnoreDisable
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.INTERFACE_CANDY)
        public boolean oldNoItemTooltips = DefaultConfig.Candy.OLD_NO_ITEM_TOOLTIPS;
        static { CandyTweak.NO_ITEM_TOOLTIPS.setKey("oldNoItemTooltips"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.INTERFACE_CANDY)
        public boolean oldDurabilityColors = DefaultConfig.Candy.OLD_DURABILITY_COLORS;
        static { CandyTweak.DURABILITY_COLORS.setKey("oldDurabilityColors"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus(status = StatusType.FAIL)
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.INTERFACE_CANDY)
        public boolean oldLoadingScreens = DefaultConfig.Candy.OLD_LOADING_SCREENS;
        static { CandyTweak.LOADING_SCREENS.setKey("oldLoadingScreens"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.INTERFACE_CANDY)
        public boolean oldNoSelectedItemName = DefaultConfig.Candy.OLD_NO_SELECTED_ITEM_NAME;
        static { CandyTweak.NO_SELECTED_ITEM_NAME.setKey("oldNoSelectedItemName"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.IgnoreDisable
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.INTERFACE_CANDY)
        public boolean oldPlainSelectedItemName = DefaultConfig.Candy.OLD_PLAIN_SELECTED_ITEM_NAME;
        static { CandyTweak.PLAIN_SELECTED_ITEM_NAME.setKey("oldPlainSelectedItemName"); }

        /**
         * Item Candy
         */

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.ITEM_CANDY)
        public boolean oldItemHolding = DefaultConfig.Candy.OLD_ITEM_HOLDING;
        static { CandyTweak.ITEM_HOLDING.setKey("oldItemHolding"); }

        @TweakEntry.Gui.Server
        @TweakEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.ITEM_CANDY)
        public boolean oldItemMerging = DefaultConfig.Candy.OLD_ITEM_MERGING;
        static { CandyTweak.ITEM_MERGING.setKey("oldItemMerging"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.ITEM_CANDY)
        public boolean old2dItems = DefaultConfig.Candy.OLD_2D_ITEMS;
        static { CandyTweak.FLAT_ITEMS.setKey("old2dItems"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.ITEM_CANDY)
        public boolean old2dFrames = DefaultConfig.Candy.OLD_2D_FRAMES;
        static { CandyTweak.FLAT_FRAMES.setKey("old2dFrames"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.ITEM_CANDY)
        public boolean old2dThrownItems = DefaultConfig.Candy.OLD_2D_THROWN_ITEMS;
        static { CandyTweak.FLAT_THROW_ITEMS.setKey("old2dThrownItems"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.IgnoreDisable
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.ITEM_CANDY)
        public boolean old2dEnchantedItems = DefaultConfig.Candy.OLD_2D_ENCHANTED_ITEMS;
        static { CandyTweak.FLAT_ENCHANTED_ITEMS.setKey("old2dEnchantedItems"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.Reload
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.ITEM_CANDY)
        public boolean fixItemModelGap = DefaultConfig.Candy.FIX_ITEM_MODEL_GAP;
        static { CandyTweak.FIX_ITEM_MODEL_GAP.setKey("fixItemModelGap"); }

        /**
         * Particle Candy
         */

        @TweakEntry.Gui.Server
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.PARTICLE_CANDY)
        public boolean oldSweepParticles = DefaultConfig.Candy.OLD_SWEEP_PARTICLES;
        static { CandyTweak.SWEEP.setKey("oldSweepParticles"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.PARTICLE_CANDY)
        public boolean oldOpaqueExperience = DefaultConfig.Candy.OLD_OPAQUE_EXPERIENCE;
        static { CandyTweak.OPAQUE_EXPERIENCE.setKey("oldOpaqueExperience"); }

        @TweakEntry.Gui.Server
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.PARTICLE_CANDY)
        public boolean oldNoDamageParticles = DefaultConfig.Candy.OLD_NO_DAMAGE_PARTICLES;
        static { CandyTweak.NO_DAMAGE_PARTICLES.setKey("oldNoDamageParticles"); }

        @TweakEntry.Gui.Server
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.PARTICLE_CANDY)
        public boolean oldNoCritParticles = DefaultConfig.Candy.OLD_NO_CRIT_PARTICLES;
        static { CandyTweak.NO_CRIT_PARTICLES.setKey("oldNoCritParticles"); }

        @TweakEntry.Gui.Server
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.PARTICLE_CANDY)
        public boolean oldNoMagicHitParticles = DefaultConfig.Candy.OLD_NO_MAGIC_HIT_PARTICLES;
        static { CandyTweak.NO_MAGIC_HIT_PARTICLES.setKey("oldNoMagicHitParticles"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.PARTICLE_CANDY)
        public boolean oldExplosionParticles = DefaultConfig.Candy.OLD_EXPLOSION_PARTICLES;
        static { CandyTweak.EXPLOSION_PARTICLES.setKey("oldExplosionParticles"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.IgnoreDisable
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.PARTICLE_CANDY)
        public boolean oldMixedExplosionParticles = DefaultConfig.Candy.OLD_MIXED_EXPLOSION_PARTICLES;
        static { CandyTweak.MIXED_EXPLOSION_PARTICLES.setKey("oldMixedExplosionParticles"); }

        /**
         * Lighting Candy
         */

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.LIGHTING_CANDY)
        public boolean oldLightFlicker = DefaultConfig.Candy.OLD_LIGHT_FLICKER;
        static { CandyTweak.LIGHT_FLICKER.setKey("oldLightFlicker"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.LIGHTING_CANDY)
        public boolean oldNetherLighting = DefaultConfig.Candy.OLD_NETHER_LIGHTING;
        static { CandyTweak.NETHER_LIGHTING.setKey("oldNetherLighting"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.LIGHTING_CANDY)
        public boolean oldLighting = DefaultConfig.Candy.OLD_LIGHTING;
        static { CandyTweak.LIGHTING.setKey("oldLighting"); }

        @TweakEntry.Gui.New
        @TweakEntry.Run.ReloadChunks
        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.LIGHTING_CANDY)
        public boolean oldSmoothLighting = DefaultConfig.Candy.OLD_SMOOTH_LIGHTING;
        static { CandyTweak.SMOOTH_LIGHTING.setKey("oldSmoothLighting"); }

        @TweakEntry.Gui.New
        @TweakEntry.Run.ReloadChunks
        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.LIGHTING_CANDY)
        public boolean oldLeavesLighting = DefaultConfig.Candy.OLD_LEAVES_LIGHTING;
        static { CandyTweak.LEAVES_LIGHTING.setKey("oldLeavesLighting"); }

        @TweakEntry.Gui.New
        @TweakEntry.Gui.Warning
        @TweakEntry.Gui.IgnoreDisable
        @TweakEntry.Gui.Server
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.LIGHTING_CANDY)
        public boolean oldWaterLighting = DefaultConfig.Candy.OLD_WATER_LIGHTING;
        static { CandyTweak.WATER_LIGHTING.setKey("oldWaterLighting"); }

        /**
         * World Candy
         */

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.WORLD_CANDY)
        public boolean oldTerrainFog = DefaultConfig.Candy.OLD_TERRAIN_FOG;
        static { CandyTweak.TERRAIN_FOG.setKey("oldTerrainFog"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.WORLD_CANDY)
        public boolean oldHorizonFog = DefaultConfig.Candy.OLD_HORIZON_FOG;
        static { CandyTweak.HORIZON_FOG.setKey("oldHorizonFog"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.WORLD_CANDY)
        public boolean oldNetherFog = DefaultConfig.Candy.OLD_NETHER_FOG;
        static { CandyTweak.NETHER_FOG.setKey("oldNetherFog"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.WORLD_CANDY)
        public boolean oldSunriseAtNorth = DefaultConfig.Candy.OLD_SUNRISE_AT_NORTH;
        static { CandyTweak.SUNRISE_AT_NORTH.setKey("oldSunriseAtNorth"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.WORLD_CANDY)
        public boolean oldBlueVoidOverride = DefaultConfig.Candy.OLD_BLUE_VOID_OVERRIDE;
        static { CandyTweak.BLUE_VOID_OVERRIDE.setKey("oldBlueVoidOverride"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.WORLD_CANDY)
        public boolean oldSunriseSunsetFog = DefaultConfig.Candy.OLD_SUNRISE_SUNSET_FOG;
        static { CandyTweak.SUNRISE_SUNSET_FOG.setKey("oldSunriseSunsetFog"); }

        @TweakEntry.Gui.Server
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.WORLD_CANDY)
        public boolean oldSquareBorder = DefaultConfig.Candy.OLD_SQUARE_BORDER;
        static { CandyTweak.SQUARE_BORDER.setKey("oldSquareBorder"); }

        @TweakEntry.Gui.New
        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.WORLD_CANDY)
        public boolean oldStars = DefaultConfig.Candy.OLD_STARS;
        static { CandyTweak.STARS.setKey("oldStars"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.WORLD_CANDY)
        public TweakVersion.GENERIC oldSkyColor = DefaultConfig.Candy.OLD_SKY_COLOR;
        static { CandyTweak.SKY_COLOR.setKey("oldSkyColor"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.WORLD_CANDY)
        public TweakVersion.GENERIC oldFogColor = DefaultConfig.Candy.OLD_FOG_COLOR;
        static { CandyTweak.FOG_COLOR.setKey("oldFogColor"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.WORLD_CANDY)
        public TweakVersion.GENERIC oldBlueVoid = DefaultConfig.Candy.OLD_BLUE_VOID;
        static { CandyTweak.BLUE_VOID.setKey("oldBlueVoid"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        @TweakEntry.Gui.Sub(group = TweakEntry.Category.WORLD_CANDY)
        @TweakEntry.Gui.SliderType(slider = TweakEntry.Gui.Slider.CLOUD_SLIDER)
        @TweakEntry.Gui.DisabledInteger(disabled = 192)
        @ConfigEntry.BoundedDiscrete(min = 108, max = 192)
        public int oldCloudHeight = DefaultConfig.Candy.OLD_CLOUD_HEIGHT;
        static { CandyTweak.CLOUD_HEIGHT.setKey("oldCloudHeight"); }
    }

    @TweakEntry.Gui.Ignore
    public Animation animation = new Animation();
    public static class Animation
    {
        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        public boolean oldSwing = DefaultConfig.Animation.OLD_SWING;
        static { AnimationTweak.ITEM_SWING.setKey("oldSwing"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        public boolean oldItemCooldown = DefaultConfig.Animation.OLD_ITEM_COOLDOWN;
        static { AnimationTweak.COOLDOWN.setKey("oldItemCooldown"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        public boolean oldItemReequip = DefaultConfig.Animation.OLD_ITEM_REEQUIP;
        static { AnimationTweak.REEQUIP.setKey("oldItemReequip"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        public boolean oldArmSway = DefaultConfig.Animation.OLD_ARM_SWAY;
        static { AnimationTweak.ARM_SWAY.setKey("oldArmSway"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.IgnoreDisable
        @TweakEntry.Gui.EntryStatus(status = StatusType.OKAY)
        public boolean armSwayMirror = DefaultConfig.Animation.ARM_SWAY_MIRROR;
        static { AnimationTweak.ARM_SWAY_MIRROR.setKey("armSwayMirror"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @TweakEntry.Gui.SliderType(slider = TweakEntry.Gui.Slider.INTENSITY_SLIDER)
        @ConfigEntry.BoundedDiscrete(min = 0, max = 300)
        public int armSwayIntensity = DefaultConfig.Animation.ARM_SWAY_INTENSITY;
        static { AnimationTweak.ARM_SWAY_INTENSITY.setKey("armSwayIntensity"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        public boolean oldCollideBobbing = DefaultConfig.Animation.OLD_COLLIDE_BOBBING;
        static { AnimationTweak.COLLIDE_BOB.setKey("oldCollideBobbing"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        public boolean oldVerticalBobbing = DefaultConfig.Animation.OLD_VERTICAL_BOBBING;
        static { AnimationTweak.BOB_VERTICAL.setKey("oldVerticalBobbing"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        public boolean oldSneaking = DefaultConfig.Animation.OLD_SNEAKING;
        static { AnimationTweak.SNEAK_SMOOTH.setKey("oldSneaking"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        public boolean oldSwingDropping = DefaultConfig.Animation.OLD_SWING_DROPPING;
        static { AnimationTweak.SWING_DROP.setKey("oldSwingDropping"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        public boolean oldToolExplosion = DefaultConfig.Animation.OLD_TOOL_EXPLOSION;
        static { AnimationTweak.TOOL_EXPLODE.setKey("oldToolExplosion"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        public boolean oldZombieArms = DefaultConfig.Animation.OLD_ZOMBIE_ARMS;
        static { AnimationTweak.ZOMBIE_ARMS.setKey("oldZombieArms"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        public boolean oldSkeletonArms = DefaultConfig.Animation.OLD_SKELETON_ARMS;
        static { AnimationTweak.SKELETON_ARMS.setKey("oldSkeletonArms"); }

        @TweakEntry.Gui.New
        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        public boolean oldGhastCharging = DefaultConfig.Animation.OLD_GHAST_CHARGING;
        static { AnimationTweak.GHAST_CHARGING.setKey("oldGhastCharging"); }
    }

    @TweakEntry.Gui.Ignore
    public Swing swing = new Swing();
    public static class Swing
    {
        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus
        public boolean overrideSpeeds = DefaultConfig.Swing.OVERRIDE_SPEEDS;
        static { SwingTweak.OVERRIDE_SPEEDS.setKey("overrideSpeeds"); }

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @ConfigEntry.BoundedDiscrete(min = MIN, max = MAX)
        public int tool = DefaultConfig.Swing.TOOL;

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @ConfigEntry.BoundedDiscrete(min = MIN, max = MAX)
        public int block = DefaultConfig.Swing.BLOCK;

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @ConfigEntry.BoundedDiscrete(min = MIN, max = MAX)
        public int sword = DefaultConfig.Swing.SWORD;

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @ConfigEntry.BoundedDiscrete(min = MIN, max = MAX)
        public int item = DefaultConfig.Swing.ITEM;

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @ConfigEntry.BoundedDiscrete(min = DefaultConfig.Swing.GLOBAL, max = MAX)
        public int haste = DefaultConfig.Swing.HASTE;

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @ConfigEntry.BoundedDiscrete(min = DefaultConfig.Swing.GLOBAL, max = MAX)
        public int fatigue = DefaultConfig.Swing.FATIGUE;

        @TweakEntry.Gui.Client
        @TweakEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @ConfigEntry.BoundedDiscrete(min = DefaultConfig.Swing.GLOBAL, max = MAX)
        public int global = DefaultConfig.Swing.GLOBAL;
    }

    @TweakEntry.Gui.Ignore
    public Gui gui = new Gui();
    public static class Gui
    {
        @TweakEntry.Gui.EntryStatus(status = StatusType.OKAY)
        public SettingsScreen.OptionScreen defaultScreen = DefaultConfig.Gui.DEFAULT_SCREEN;
        static { GuiTweak.DEFAULT_SCREEN.setKey("defaultScreen"); }

        @TweakEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @SuppressWarnings("unused")
        public boolean displayNewTags = DefaultConfig.Gui.DISPLAY_NEW_TAGS;
        static { GuiTweak.DISPLAY_NEW_TAGS.setKey("displayNewTags"); }

        @TweakEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @SuppressWarnings("unused")
        public boolean displaySidedTags = DefaultConfig.Gui.DISPLAY_SIDED_TAGS;
        static { GuiTweak.DISPLAY_SIDED_TAGS.setKey("displaySidedTags"); }

        @TweakEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @SuppressWarnings("unused")
        public boolean displayTagTooltips = DefaultConfig.Gui.DISPLAY_TAG_TOOLTIPS;
        static { GuiTweak.DISPLAY_TAG_TOOLTIPS.setKey("displayTagTooltips"); }

        @TweakEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @SuppressWarnings("unused")
        public boolean displayFeatureStatus = DefaultConfig.Gui.DISPLAY_FEATURE_STATUS;
        static { GuiTweak.DISPLAY_FEATURE_STATUS.setKey("displayFeatureStatus"); }
    }

    @TweakEntry.Gui.Ignore
    public Map<String, Integer> custom = Maps.newHashMap();
}