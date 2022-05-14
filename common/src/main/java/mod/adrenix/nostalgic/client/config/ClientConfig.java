package mod.adrenix.nostalgic.client.config;

import com.google.common.collect.Maps;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.annotation.NostalgicEntry;
import mod.adrenix.nostalgic.client.config.feature.*;
import mod.adrenix.nostalgic.client.config.gui.screen.SettingsScreen;
import mod.adrenix.nostalgic.client.config.reflect.StatusType;

import java.util.Map;

@Config(name = NostalgicTweaks.MOD_ID)
public class ClientConfig implements ConfigData
{
    @NostalgicEntry.Gui.Ignore public static final int MIN = 0;
    @NostalgicEntry.Gui.Ignore public static final int MAX = 16;
    @NostalgicEntry.Gui.Ignore public static final String ROOT_KEY = "isModEnabled";

    @NostalgicEntry.Gui.Client
    @NostalgicEntry.Gui.NoTooltip
    @NostalgicEntry.Gui.EntryStatus(status = StatusType.OKAY)
    public boolean isModEnabled = true;

    @NostalgicEntry.Gui.Ignore
    public Sound sound = new Sound();
    public static class Sound
    {
        @NostalgicEntry.Gui.Server
        @NostalgicEntry.Gui.EntryStatus
        public boolean oldAttack = DefaultConfig.Sound.OLD_ATTACK;
        static { SoundFeature.OLD_ATTACK.setKey("oldAttack"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        public boolean oldHurt = DefaultConfig.Sound.OLD_HURT;
        static { SoundFeature.OLD_HURT.setKey("oldHurt"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        public boolean oldFall = DefaultConfig.Sound.OLD_FALL;
        static { SoundFeature.OLD_FALL.setKey("oldFall"); }

        @NostalgicEntry.Gui.Server
        @NostalgicEntry.Gui.EntryStatus
        public boolean oldStep = DefaultConfig.Sound.OLD_STEP;
        static { SoundFeature.OLD_STEP.setKey("oldStep"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        public boolean oldXP = DefaultConfig.Sound.OLD_XP;
        static { SoundFeature.OLD_XP.setKey("oldXP"); }
    }

    @NostalgicEntry.Gui.Ignore
    public EyeCandy eyeCandy = new EyeCandy();
    public static class EyeCandy
    {
        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.TITLE_CANDY)
        public String titleVersionText = DefaultConfig.Candy.TITLE_VERSION_TEXT;

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.INTERFACE_CANDY)
        public String oldOverlayText = DefaultConfig.Candy.OLD_OVERLAY_TEXT;

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.INTERFACE_CANDY)
        public boolean oldVersionOverlay = DefaultConfig.Candy.OLD_VERSION_OVERLAY;
        static { CandyFeature.VERSION_OVERLAY.setKey("oldVersionOverlay"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus(status = StatusType.FAIL)
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.INTERFACE_CANDY)
        public boolean oldButtonHover = DefaultConfig.Candy.OLD_BUTTON_HOVER;
        static { CandyFeature.BUTTON_HOVER.setKey("oldButtonHover"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus(status = StatusType.FAIL)
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.INTERFACE_CANDY)
        public boolean oldTooltipBoxes = DefaultConfig.Candy.OLD_TOOLTIP_BOXES;
        static { CandyFeature.TOOLTIP_BOXES.setKey("oldTooltipBoxes"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.WORLD_CANDY)
        public boolean oldLightFlicker = DefaultConfig.Candy.OLD_LIGHT_FLICKER;
        static { CandyFeature.LIGHT_FLICKER.setKey("oldLightFlicker"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.INTERFACE_CANDY)
        public boolean oldDurabilityColors = DefaultConfig.Candy.OLD_DURABILITY_COLORS;
        static { CandyFeature.DURABILITY_COLORS.setKey("oldDurabilityColors"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.ITEM_CANDY)
        public boolean oldItemHolding = DefaultConfig.Candy.OLD_ITEM_HOLDING;
        static { CandyFeature.ITEM_HOLDING.setKey("oldItemHolding"); }

        @NostalgicEntry.Gui.Server
        @NostalgicEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.ITEM_CANDY)
        public boolean oldItemMerging = DefaultConfig.Candy.OLD_ITEM_MERGING;
        static { CandyFeature.ITEM_MERGING.setKey("oldItemMerging"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.ITEM_CANDY)
        public boolean old2dItems = DefaultConfig.Candy.OLD_2D_ITEMS;
        static { CandyFeature.FLAT_ITEMS.setKey("old2dItems"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.ITEM_CANDY)
        public boolean old2dFrames = DefaultConfig.Candy.OLD_2D_FRAMES;
        static { CandyFeature.FLAT_FRAMES.setKey("old2dFrames"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.ITEM_CANDY)
        public boolean old2dThrownItems = DefaultConfig.Candy.OLD_2D_THROWN_ITEMS;
        static { CandyFeature.FLAT_THROWN_ITEMS.setKey("old2dThrownItems"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.ITEM_CANDY)
        public boolean old2dEnchantedItems = DefaultConfig.Candy.OLD_2D_ENCHANTED_ITEMS;
        static { CandyFeature.FLAT_ENCHANTED_ITEMS.setKey("old2dEnchantedItems"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        @NostalgicEntry.Gui.Reload
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.ITEM_CANDY)
        public boolean fixItemModelGap = DefaultConfig.Candy.FIX_ITEM_MODEL_GAP;
        static { CandyFeature.FIX_ITEM_MODEL_GAP.setKey("fixItemModelGap"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus(status = StatusType.FAIL)
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.TITLE_CANDY)
        public boolean oldTitleScreen = DefaultConfig.Candy.OLD_TITLE_SCREEN;
        static { CandyFeature.TITLE_SCREEN.setKey("oldTitleScreen"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.TITLE_CANDY)
        public boolean oldTitleBackground = DefaultConfig.Candy.OLD_TITLE_BACKGROUND;
        static { CandyFeature.TITLE_BACKGROUND.setKey("oldTitleBackground"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.TITLE_CANDY)
        public boolean removeTitleModLoaderText = DefaultConfig.Candy.REMOVE_TITLE_MOD_LOADER_TEXT;
        static { CandyFeature.TITLE_MOD_LOADER_TEXT.setKey("removeTitleModLoaderText"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.TITLE_CANDY)
        public boolean oldAlphaLogo = DefaultConfig.Candy.OLD_ALPHA_LOGO;
        static { CandyFeature.ALPHA_LOGO.setKey("oldAlphaLogo"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.TITLE_CANDY)
        public boolean oldLogoOutline = DefaultConfig.Candy.OLD_LOGO_OUTLINE;
        static { CandyFeature.LOGO_OUTLINE.setKey("oldLogoOutline"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus(status = StatusType.FAIL)
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.INTERFACE_CANDY)
        public boolean oldLoadingScreens = DefaultConfig.Candy.OLD_LOADING_SCREENS;
        static { CandyFeature.LOADING_SCREENS.setKey("oldLoadingScreens"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.WORLD_CANDY)
        public boolean oldLighting = DefaultConfig.Candy.OLD_LIGHTING;
        static { CandyFeature.LIGHTING.setKey("oldLighting"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.WORLD_CANDY)
        public boolean oldFog = DefaultConfig.Candy.OLD_FOG;
        static { CandyFeature.FOG.setKey("oldFog"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.WORLD_CANDY)
        public boolean oldNetherFog = DefaultConfig.Candy.OLD_NETHER_FOG;
        static { CandyFeature.NETHER_FOG.setKey("oldNetherFog"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.WORLD_CANDY)
        public boolean oldCloudHeight = DefaultConfig.Candy.OLD_CLOUD_HEIGHT;
        static { CandyFeature.CLOUD_HEIGHT.setKey("oldCloudHeight"); }

        @NostalgicEntry.Gui.Server
        @NostalgicEntry.Gui.EntryStatus
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.PARTICLE_CANDY)
        public boolean oldSweepParticles = DefaultConfig.Candy.OLD_SWEEP_PARTICLES;
        static { CandyFeature.SWEEP.setKey("oldSweepParticles"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.WORLD_CANDY)
        public boolean oldExplosionParticles = DefaultConfig.Candy.OLD_EXPLOSION_PARTICLES;
        static { CandyFeature.EXPLOSION_PARTICLES.setKey("oldExplosionParticles"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.WORLD_CANDY)
        public boolean oldMixedExplosionParticles = DefaultConfig.Candy.OLD_MIXED_EXPLOSION_PARTICLES;
        static { CandyFeature.MIXED_EXPLOSION_PARTICLES.setKey("oldMixedExplosionParticles"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.WORLD_CANDY)
        public boolean oldSunriseAtNorth = DefaultConfig.Candy.OLD_SUNRISE_AT_NORTH;
        static { CandyFeature.SUNRISE_AT_NORTH.setKey("oldSunriseAtNorth"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.PARTICLE_CANDY)
        public boolean oldOpaqueExperience = DefaultConfig.Candy.OLD_OPAQUE_EXPERIENCE;
        static { CandyFeature.OPAQUE_EXPERIENCE.setKey("oldOpaqueExperience"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.INTERFACE_CANDY)
        public boolean oldNoSelectedItemName = DefaultConfig.Candy.OLD_NO_SELECTED_ITEM_NAME;
        static { CandyFeature.NO_SELECTED_ITEM_NAME.setKey("oldNoSelectedItemName"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.INTERFACE_CANDY)
        public boolean oldPlainSelectedItemName = DefaultConfig.Candy.OLD_PLAIN_SELECTED_ITEM_NAME;
        static { CandyFeature.PLAIN_SELECTED_ITEM_NAME.setKey("oldPlainSelectedItemName"); }

        @NostalgicEntry.Gui.Server
        @NostalgicEntry.Gui.EntryStatus
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.PARTICLE_CANDY)
        public boolean oldNoDamageParticles = DefaultConfig.Candy.OLD_NO_DAMAGE_PARTICLES;
        static { CandyFeature.NO_DAMAGE_PARTICLES.setKey("oldNoDamageParticles"); }

        @NostalgicEntry.Gui.Server
        @NostalgicEntry.Gui.EntryStatus
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.PARTICLE_CANDY)
        public boolean oldNoCritParticles = DefaultConfig.Candy.OLD_NO_CRIT_PARTICLES;
        static { CandyFeature.NO_CRIT_PARTICLES.setKey("oldNoCritParticles"); }

        @NostalgicEntry.Gui.Server
        @NostalgicEntry.Gui.EntryStatus
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.PARTICLE_CANDY)
        public boolean oldNoMagicHitParticles = DefaultConfig.Candy.OLD_NO_MAGIC_HIT_PARTICLES;
        static { CandyFeature.NO_MAGIC_HIT_PARTICLES.setKey("oldNoMagicHitParticles"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.WORLD_CANDY)
        public DefaultConfig.VERSION oldSkyColor = DefaultConfig.Candy.OLD_SKY_COLOR;
        static { CandyFeature.SKY_COLOR.setKey("oldSkyColor"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.WORLD_CANDY)
        public DefaultConfig.VERSION oldFogColor = DefaultConfig.Candy.OLD_FOG_COLOR;
        static { CandyFeature.FOG_COLOR.setKey("oldFogColor"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.Restart
        @NostalgicEntry.Gui.EntryStatus
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.WORLD_CANDY)
        public DefaultConfig.VERSION oldBlueVoid = DefaultConfig.Candy.OLD_BLUE_VOID;
        static { CandyFeature.BLUE_VOID.setKey("oldBlueVoid"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.WORLD_CANDY)
        public boolean oldBlueVoidOverride = DefaultConfig.Candy.OLD_BLUE_VOID_OVERRIDE;
        static { CandyFeature.BLUE_VOID_OVERRIDE.setKey("oldBlueVoidOverride"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.WORLD_CANDY)
        public boolean oldSunriseSunsetFog = DefaultConfig.Candy.OLD_SUNRISE_SUNSET_FOG;
        static { CandyFeature.SUNRISE_SUNSET_FOG.setKey("oldSunriseSunsetFog"); }

        @NostalgicEntry.Gui.Server
        @NostalgicEntry.Gui.EntryStatus
        @NostalgicEntry.Gui.Sub(group = NostalgicEntry.Category.WORLD_CANDY)
        public boolean oldSquareBorder = DefaultConfig.Candy.OLD_SQUARE_BORDER;
        static { CandyFeature.SQUARE_BORDER.setKey("oldSquareBorder"); }
    }

    @NostalgicEntry.Gui.Ignore
    public Animation animation = new Animation();
    public static class Animation
    {
        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        public boolean oldSwing = DefaultConfig.Animation.OLD_SWING;
        static { AnimationFeature.ITEM_SWING.setKey("oldSwing"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        public boolean oldItemCooldown = DefaultConfig.Animation.OLD_ITEM_COOLDOWN;
        static { AnimationFeature.COOLDOWN.setKey("oldItemCooldown"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        public boolean oldItemReequip = DefaultConfig.Animation.OLD_ITEM_REEQUIP;
        static { AnimationFeature.REEQUIP.setKey("oldItemReequip"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        public boolean oldArmSway = DefaultConfig.Animation.OLD_ARM_SWAY;
        static { AnimationFeature.ARM_SWAY.setKey("oldArmSway"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.IgnoreDisable
        @NostalgicEntry.Gui.EntryStatus(status = StatusType.OKAY)
        public boolean armSwayMirror = DefaultConfig.Animation.ARM_SWAY_MIRROR;
        static { AnimationFeature.ARM_SWAY_MIRROR.setKey("armSwayMirror"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @NostalgicEntry.Gui.SliderType(slider = NostalgicEntry.Gui.Slider.INTENSITY_SLIDER)
        @ConfigEntry.BoundedDiscrete(min = 0, max = 300)
        public int armSwayIntensity = DefaultConfig.Animation.ARM_SWAY_INTENSITY;
        static { AnimationFeature.ARM_SWAY_INTENSITY.setKey("armSwayIntensity"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        public boolean oldCollideBobbing = DefaultConfig.Animation.OLD_COLLIDE_BOBBING;
        static { AnimationFeature.COLLIDE_BOB.setKey("oldCollideBobbing"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        public boolean oldVerticalBobbing = DefaultConfig.Animation.OLD_VERTICAL_BOBBING;
        static { AnimationFeature.BOB_VERTICAL.setKey("oldVerticalBobbing"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        public boolean oldSneaking = DefaultConfig.Animation.OLD_SNEAKING;
        static { AnimationFeature.SNEAK_SMOOTH.setKey("oldSneaking"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        public boolean oldSwingDropping = DefaultConfig.Animation.OLD_SWING_DROPPING;
        static { AnimationFeature.SWING_DROP.setKey("oldSwingDropping"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        public boolean oldToolExplosion = DefaultConfig.Animation.OLD_TOOL_EXPLOSION;
        static { AnimationFeature.TOOL_EXPLODE.setKey("oldToolExplosion"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        public boolean oldZombieArms = DefaultConfig.Animation.OLD_ZOMBIE_ARMS;
        static { AnimationFeature.ZOMBIE_ARMS.setKey("oldZombieArms"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        public boolean oldSkeletonArms = DefaultConfig.Animation.OLD_SKELETON_ARMS;
        static { AnimationFeature.SKELETON_ARMS.setKey("oldSkeletonArms"); }
    }

    @NostalgicEntry.Gui.Ignore
    public Swing swing = new Swing();
    public static class Swing
    {
        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus
        public boolean overrideSpeeds = DefaultConfig.Swing.OVERRIDE_SPEEDS;
        static { SwingFeature.OVERRIDE_SPEEDS.setKey("overrideSpeeds"); }

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @ConfigEntry.BoundedDiscrete(min = MIN, max = MAX)
        public int tool = DefaultConfig.Swing.TOOL;

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @ConfigEntry.BoundedDiscrete(min = MIN, max = MAX)
        public int block = DefaultConfig.Swing.BLOCK;

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @ConfigEntry.BoundedDiscrete(min = MIN, max = MAX)
        public int sword = DefaultConfig.Swing.SWORD;

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @ConfigEntry.BoundedDiscrete(min = MIN, max = MAX)
        public int item = DefaultConfig.Swing.ITEM;

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @ConfigEntry.BoundedDiscrete(min = DefaultConfig.Swing.GLOBAL, max = MAX)
        public int haste = DefaultConfig.Swing.HASTE;

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @ConfigEntry.BoundedDiscrete(min = DefaultConfig.Swing.GLOBAL, max = MAX)
        public int fatigue = DefaultConfig.Swing.FATIGUE;

        @NostalgicEntry.Gui.Client
        @NostalgicEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @ConfigEntry.BoundedDiscrete(min = DefaultConfig.Swing.GLOBAL, max = MAX)
        public int global = DefaultConfig.Swing.GLOBAL;
    }

    @NostalgicEntry.Gui.Ignore
    public Gui gui = new Gui();
    public static class Gui
    {
        @NostalgicEntry.Gui.EntryStatus(status = StatusType.OKAY)
        public SettingsScreen.OptionScreen defaultScreen = DefaultConfig.Gui.DEFAULT_SCREEN;
        static { GuiFeature.DEFAULT_SCREEN.setKey("defaultScreen"); }

        @NostalgicEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @SuppressWarnings("unused")
        public boolean displayNewTags = DefaultConfig.Gui.DISPLAY_NEW_TAGS;
        static { GuiFeature.DISPLAY_NEW_TAGS.setKey("displayNewTags"); }

        @NostalgicEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @SuppressWarnings("unused")
        public boolean displaySidedTags = DefaultConfig.Gui.DISPLAY_SIDED_TAGS;
        static { GuiFeature.DISPLAY_SIDED_TAGS.setKey("displaySidedTags"); }

        @NostalgicEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @SuppressWarnings("unused")
        public boolean displayTagTooltips = DefaultConfig.Gui.DISPLAY_TAG_TOOLTIPS;
        static { GuiFeature.DISPLAY_TAG_TOOLTIPS.setKey("displayTagTooltips"); }

        @NostalgicEntry.Gui.EntryStatus(status = StatusType.OKAY)
        @SuppressWarnings("unused")
        public boolean displayFeatureStatus = DefaultConfig.Gui.DISPLAY_FEATURE_STATUS;
        static { GuiFeature.DISPLAY_FEATURE_STATUS.setKey("displayFeatureStatus"); }
    }

    @NostalgicEntry.Gui.Ignore
    public Map<String, Integer> custom = Maps.newHashMap();
}