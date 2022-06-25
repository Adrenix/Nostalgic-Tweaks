package mod.adrenix.nostalgic.client.config;

import com.google.common.collect.Maps;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.config.annotation.TweakClient;
import mod.adrenix.nostalgic.client.config.gui.screen.MenuOption;
import mod.adrenix.nostalgic.common.config.DefaultConfig;
import mod.adrenix.nostalgic.common.config.annotation.TweakSide;
import mod.adrenix.nostalgic.common.config.reflect.StatusType;
import mod.adrenix.nostalgic.common.config.tweak.*;

import java.util.Map;

/**
 * The server controlled tweaks in this config need to stay in sync with the fields in the server config.
 * Any updates in this class or that class will require an update in both config classes.
 *
 * @see mod.adrenix.nostalgic.server.config.ServerConfig
 */

@Config(name = NostalgicTweaks.MOD_ID)
public class ClientConfig implements ConfigData
{
    @TweakSide.Ignore public static final int MIN = 0;
    @TweakSide.Ignore public static final int MAX = 16;
    @TweakSide.Ignore public static final String ROOT_KEY = "isModEnabled";

    @TweakSide.Client
    @TweakSide.EntryStatus(status = StatusType.LOADED)
    @TweakClient.Gui.NoTooltip
    public boolean isModEnabled = true;

    @TweakSide.Ignore
    public Sound sound = new Sound();
    public static class Sound
    {
        @TweakSide.Client
        @TweakSide.EntryStatus
        public boolean oldAttack = DefaultConfig.Sound.OLD_ATTACK;
        static { SoundTweak.OLD_ATTACK.setKey("oldAttack"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        public boolean oldHurt = DefaultConfig.Sound.OLD_HURT;
        static { SoundTweak.OLD_HURT.setKey("oldHurt"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        public boolean oldFall = DefaultConfig.Sound.OLD_FALL;
        static { SoundTweak.OLD_FALL.setKey("oldFall"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        public boolean oldStep = DefaultConfig.Sound.OLD_STEP;
        static { SoundTweak.OLD_STEP.setKey("oldStep"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.IgnoreDisable
        public boolean oldXP = DefaultConfig.Sound.OLD_XP;
        static { SoundTweak.OLD_XP.setKey("oldXP"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        public boolean oldDoor = DefaultConfig.Sound.OLD_DOOR;
        static { SoundTweak.OLD_DOOR.setKey("oldDoor"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        public boolean oldBed = DefaultConfig.Sound.OLD_BED;
        static { SoundTweak.OLD_BED.setKey("oldBed"); }
    }

    @TweakSide.Ignore
    public EyeCandy eyeCandy = new EyeCandy();
    public static class EyeCandy
    {
        /**
         * Block Candy
         */

        @TweakSide.Client
        @TweakClient.Run.ReloadChunks
        @TweakClient.Gui.Sub(group = TweakClient.Category.BLOCK_CANDY)
        public boolean fixAmbientOcclusion = DefaultConfig.Candy.FIX_AMBIENT_OCCLUSION;
        static { CandyTweak.FIX_AO.setKey("fixAmbientOcclusion"); }

        @TweakSide.Client
        @TweakClient.Run.ReloadResources
        @TweakClient.Gui.Sub(group = TweakClient.Category.BLOCK_CANDY)
        public boolean oldChest = DefaultConfig.Candy.OLD_CHEST;
        static { CandyTweak.CHEST.setKey("oldChest"); }

        @TweakSide.Server
        @TweakClient.Run.ReloadChunks
        @TweakClient.Gui.Warning
        @TweakClient.Gui.Sub(group = TweakClient.Category.BLOCK_CANDY)
        public boolean oldChestVoxel = DefaultConfig.Candy.OLD_CHEST_VOXEL;
        static { CandyTweak.CHEST_VOXEL.setKey("oldChestVoxel"); }

        @TweakSide.Client
        @TweakClient.Run.ReloadResources
        @TweakClient.Gui.Sub(group = TweakClient.Category.BLOCK_CANDY)
        public boolean oldEnderChest = DefaultConfig.Candy.OLD_ENDER_CHEST;
        static { CandyTweak.ENDER_CHEST.setKey("oldEnderChest"); }

        @TweakSide.Client
        @TweakClient.Run.ReloadResources
        @TweakClient.Gui.Sub(group = TweakClient.Category.BLOCK_CANDY)
        public boolean oldTrappedChest = DefaultConfig.Candy.OLD_TRAPPED_CHEST;
        static { CandyTweak.TRAPPED_CHEST.setKey("oldTrappedChest"); }

        /**
         * Interface Candy
         */

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Sub(group = TweakClient.Category.INTERFACE_CANDY)
        public String oldOverlayText = DefaultConfig.Candy.OLD_OVERLAY_TEXT;

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 1)
        @TweakClient.Gui.Sub(group = TweakClient.Category.INTERFACE_CANDY)
        public TweakVersion.Overlay oldLoadingOverlay = DefaultConfig.Candy.OLD_LOADING_OVERLAY;
        static { CandyTweak.LOADING_OVERLAY.setKey("oldLoadingOverlay"); }

        @TweakSide.Server
        @TweakClient.Gui.Sub(group = TweakClient.Category.INTERFACE_CANDY)
        public TweakVersion.Hotbar oldCreativeHotbar = DefaultConfig.Candy.OLD_CREATIVE_HOTBAR;
        static { CandyTweak.CREATIVE_HOTBAR.setKey("oldCreativeHotbar"); }

        @TweakSide.Client
        @TweakClient.Gui.IgnoreDisable
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 2)
        @TweakClient.Gui.Sub(group = TweakClient.Category.INTERFACE_CANDY)
        public boolean removeLoadingBar = DefaultConfig.Candy.REMOVE_LOADING_BAR;
        static { CandyTweak.REMOVE_LOADING_BAR.setKey("removeLoadingBar"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Category.INTERFACE_CANDY)
        public boolean oldVersionOverlay = DefaultConfig.Candy.OLD_VERSION_OVERLAY;
        static { CandyTweak.VERSION_OVERLAY.setKey("oldVersionOverlay"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Category.INTERFACE_CANDY)
        public boolean oldChatInput = DefaultConfig.Candy.OLD_CHAT_INPUT;
        static { CandyTweak.CHAT_INPUT.setKey("oldChatInput"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Category.INTERFACE_CANDY)
        public boolean oldChatBox = DefaultConfig.Candy.OLD_CHAT_BOX;
        static { CandyTweak.CHAT_BOX.setKey("oldChatBox"); }

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.FAIL)
        @TweakClient.Gui.Sub(group = TweakClient.Category.INTERFACE_CANDY)
        public boolean oldButtonHover = DefaultConfig.Candy.OLD_BUTTON_HOVER;
        static { CandyTweak.BUTTON_HOVER.setKey("oldButtonHover"); }

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.FAIL)
        @TweakClient.Gui.Sub(group = TweakClient.Category.INTERFACE_CANDY)
        public boolean oldTooltipBoxes = DefaultConfig.Candy.OLD_TOOLTIP_BOXES;
        static { CandyTweak.TOOLTIP_BOXES.setKey("oldTooltipBoxes"); }

        @TweakSide.Client
        @TweakClient.Gui.IgnoreDisable
        @TweakClient.Gui.Sub(group = TweakClient.Category.INTERFACE_CANDY)
        public boolean oldNoItemTooltips = DefaultConfig.Candy.OLD_NO_ITEM_TOOLTIPS;
        static { CandyTweak.NO_ITEM_TOOLTIPS.setKey("oldNoItemTooltips"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Category.INTERFACE_CANDY)
        public boolean oldDurabilityColors = DefaultConfig.Candy.OLD_DURABILITY_COLORS;
        static { CandyTweak.DURABILITY_COLORS.setKey("oldDurabilityColors"); }

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.FAIL)
        @TweakClient.Gui.Sub(group = TweakClient.Category.INTERFACE_CANDY)
        public boolean oldLoadingScreens = DefaultConfig.Candy.OLD_LOADING_SCREENS;
        static { CandyTweak.LOADING_SCREENS.setKey("oldLoadingScreens"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Category.INTERFACE_CANDY)
        public boolean oldNoSelectedItemName = DefaultConfig.Candy.OLD_NO_SELECTED_ITEM_NAME;
        static { CandyTweak.NO_SELECTED_ITEM_NAME.setKey("oldNoSelectedItemName"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.IgnoreDisable
        @TweakClient.Gui.Sub(group = TweakClient.Category.INTERFACE_CANDY)
        public boolean oldPlainSelectedItemName = DefaultConfig.Candy.OLD_PLAIN_SELECTED_ITEM_NAME;
        static { CandyTweak.PLAIN_SELECTED_ITEM_NAME.setKey("oldPlainSelectedItemName"); }

        /**
         * Item Candy
         */

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Run.ReloadResources
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 1)
        @TweakClient.Gui.Sub(group = TweakClient.Category.ITEM_CANDY)
        public boolean fixItemModelGap = DefaultConfig.Candy.FIX_ITEM_MODEL_GAP;
        static { CandyTweak.FIX_ITEM_MODEL_GAP.setKey("fixItemModelGap"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Category.ITEM_CANDY)
        public boolean oldItemHolding = DefaultConfig.Candy.OLD_ITEM_HOLDING;
        static { CandyTweak.ITEM_HOLDING.setKey("oldItemHolding"); }

        @TweakSide.Server
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Sub(group = TweakClient.Category.ITEM_CANDY)
        public boolean oldItemMerging = DefaultConfig.Candy.OLD_ITEM_MERGING;
        static { CandyTweak.ITEM_MERGING.setKey("oldItemMerging"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Category.ITEM_CANDY)
        public boolean old2dItems = DefaultConfig.Candy.OLD_2D_ITEMS;
        static { CandyTweak.FLAT_ITEMS.setKey("old2dItems"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Category.ITEM_CANDY)
        public boolean old2dFrames = DefaultConfig.Candy.OLD_2D_FRAMES;
        static { CandyTweak.FLAT_FRAMES.setKey("old2dFrames"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Category.ITEM_CANDY)
        public boolean old2dThrownItems = DefaultConfig.Candy.OLD_2D_THROWN_ITEMS;
        static { CandyTweak.FLAT_THROW_ITEMS.setKey("old2dThrownItems"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.IgnoreDisable
        @TweakClient.Gui.Sub(group = TweakClient.Category.ITEM_CANDY)
        public boolean old2dEnchantedItems = DefaultConfig.Candy.OLD_2D_ENCHANTED_ITEMS;
        static { CandyTweak.FLAT_ENCHANTED_ITEMS.setKey("old2dEnchantedItems"); }

        /**
         * Lighting Candy
         */

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Category.LIGHTING_CANDY)
        public boolean oldLightFlicker = DefaultConfig.Candy.OLD_LIGHT_FLICKER;
        static { CandyTweak.LIGHT_FLICKER.setKey("oldLightFlicker"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Category.LIGHTING_CANDY)
        public boolean oldNetherLighting = DefaultConfig.Candy.OLD_NETHER_LIGHTING;
        static { CandyTweak.NETHER_LIGHTING.setKey("oldNetherLighting"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Category.LIGHTING_CANDY)
        public boolean oldLighting = DefaultConfig.Candy.OLD_LIGHTING;
        static { CandyTweak.LIGHTING.setKey("oldLighting"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Run.ReloadChunks
        @TweakClient.Gui.Sub(group = TweakClient.Category.LIGHTING_CANDY)
        public boolean oldSmoothLighting = DefaultConfig.Candy.OLD_SMOOTH_LIGHTING;
        static { CandyTweak.SMOOTH_LIGHTING.setKey("oldSmoothLighting"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Run.ReloadChunks
        @TweakClient.Gui.Sub(group = TweakClient.Category.LIGHTING_CANDY)
        public boolean oldLeavesLighting = DefaultConfig.Candy.OLD_LEAVES_LIGHTING;
        static { CandyTweak.LEAVES_LIGHTING.setKey("oldLeavesLighting"); }

        @TweakSide.Server
        @TweakSide.EntryStatus
        @TweakClient.Gui.Warning
        @TweakClient.Gui.IgnoreDisable
        @TweakClient.Gui.Sub(group = TweakClient.Category.LIGHTING_CANDY)
        public boolean oldWaterLighting = DefaultConfig.Candy.OLD_WATER_LIGHTING;
        static { CandyTweak.WATER_LIGHTING.setKey("oldWaterLighting"); }

        /**
         * Particle Candy
         */

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Category.PARTICLE_CANDY)
        public boolean oldSweepParticles = DefaultConfig.Candy.OLD_SWEEP_PARTICLES;
        static { CandyTweak.SWEEP.setKey("oldSweepParticles"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Category.PARTICLE_CANDY)
        public boolean oldOpaqueExperience = DefaultConfig.Candy.OLD_OPAQUE_EXPERIENCE;
        static { CandyTweak.OPAQUE_EXPERIENCE.setKey("oldOpaqueExperience"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Category.PARTICLE_CANDY)
        public boolean oldNoDamageParticles = DefaultConfig.Candy.OLD_NO_DAMAGE_PARTICLES;
        static { CandyTweak.NO_DAMAGE_PARTICLES.setKey("oldNoDamageParticles"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Category.PARTICLE_CANDY)
        public boolean oldNoCritParticles = DefaultConfig.Candy.OLD_NO_CRIT_PARTICLES;
        static { CandyTweak.NO_CRIT_PARTICLES.setKey("oldNoCritParticles"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Category.PARTICLE_CANDY)
        public boolean oldNoMagicHitParticles = DefaultConfig.Candy.OLD_NO_MAGIC_HIT_PARTICLES;
        static { CandyTweak.NO_MAGIC_HIT_PARTICLES.setKey("oldNoMagicHitParticles"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Category.PARTICLE_CANDY)
        public boolean oldExplosionParticles = DefaultConfig.Candy.OLD_EXPLOSION_PARTICLES;
        static { CandyTweak.EXPLOSION_PARTICLES.setKey("oldExplosionParticles"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.IgnoreDisable
        @TweakClient.Gui.Sub(group = TweakClient.Category.PARTICLE_CANDY)
        public boolean oldMixedExplosionParticles = DefaultConfig.Candy.OLD_MIXED_EXPLOSION_PARTICLES;
        static { CandyTweak.MIXED_EXPLOSION_PARTICLES.setKey("oldMixedExplosionParticles"); }

        /**
         * Title Screen Candy
         */

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.FAIL)
        @TweakClient.Gui.Sub(group = TweakClient.Category.TITLE_CANDY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.TOP, order = 1)
        public boolean overrideTitleScreen = DefaultConfig.Candy.OVERRIDE_TITLE_SCREEN;
        static { CandyTweak.OVERRIDE_TITLE_SCREEN.setKey("overrideTitleScreen"); }

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Sub(group = TweakClient.Category.TITLE_CANDY)
        public boolean oldAlphaLogo = DefaultConfig.Candy.OLD_ALPHA_LOGO;
        static { CandyTweak.ALPHA_LOGO.setKey("oldAlphaLogo"); }

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Sub(group = TweakClient.Category.TITLE_CANDY)
        public TweakVersion.ButtonLayout oldButtonLayout = DefaultConfig.Candy.TITLE_BUTTON_LAYOUT;
        static { CandyTweak.TITLE_BUTTON_LAYOUT.setKey("oldButtonLayout"); }

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Sub(group = TweakClient.Category.TITLE_CANDY)
        public boolean oldLogoOutline = DefaultConfig.Candy.OLD_LOGO_OUTLINE;
        static { CandyTweak.LOGO_OUTLINE.setKey("oldLogoOutline"); }

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Sub(group = TweakClient.Category.TITLE_CANDY)
        public boolean oldTitleBackground = DefaultConfig.Candy.OLD_TITLE_BACKGROUND;
        static { CandyTweak.TITLE_BACKGROUND.setKey("oldTitleBackground"); }

        @TweakSide.Client
        @TweakClient.Gui.Sub(group = TweakClient.Category.TITLE_CANDY)
        public boolean uncapTitleFPS = DefaultConfig.Candy.UNCAP_TITLE_FPS;
        static { CandyTweak.UNCAP_TITLE_FPS.setKey("uncapTitleFPS"); }

        @TweakSide.Client
        @TweakClient.Gui.IgnoreDisable
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Sub(group = TweakClient.Category.TITLE_CANDY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.BOTTOM, order = 1)
        public boolean removeTitleAccessibilityButton = DefaultConfig.Candy.REMOVE_TITLE_ACCESSIBILITY;
        static { CandyTweak.TITLE_ACCESSIBILITY.setKey("removeTitleAccessibilityButton"); }

        @TweakSide.Client
        @TweakClient.Gui.IgnoreDisable
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Sub(group = TweakClient.Category.TITLE_CANDY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.BOTTOM, order = 2)
        public boolean removeTitleLanguageButton = DefaultConfig.Candy.REMOVE_TITLE_LANGUAGE;
        static { CandyTweak.TITLE_LANGUAGE.setKey("removeTitleLanguageButton"); }

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Sub(group = TweakClient.Category.TITLE_CANDY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.BOTTOM, order = 3)
        public boolean removeTitleModLoaderText = DefaultConfig.Candy.REMOVE_TITLE_MOD_LOADER_TEXT;
        static { CandyTweak.TITLE_MOD_LOADER_TEXT.setKey("removeTitleModLoaderText"); }

        @TweakSide.Client
        @TweakClient.Gui.IgnoreDisable
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Sub(group = TweakClient.Category.TITLE_CANDY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.BOTTOM, order = 4)
        public boolean titleBottomLeftText = DefaultConfig.Candy.TITLE_BOTTOM_LEFT_TEXT;
        static { CandyTweak.TITLE_BOTTOM_LEFT_TEXT.setKey("titleBottomLeftText"); }

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.Sub(group = TweakClient.Category.TITLE_CANDY)
        @TweakClient.Gui.Placement(pos = TweakClient.Gui.Position.BOTTOM, order = 5)
        public String titleVersionText = DefaultConfig.Candy.TITLE_VERSION_TEXT;

        /**
         * World Candy
         */

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Category.WORLD_CANDY)
        public boolean oldTerrainFog = DefaultConfig.Candy.OLD_TERRAIN_FOG;
        static { CandyTweak.TERRAIN_FOG.setKey("oldTerrainFog"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Category.WORLD_CANDY)
        public boolean oldHorizonFog = DefaultConfig.Candy.OLD_HORIZON_FOG;
        static { CandyTweak.HORIZON_FOG.setKey("oldHorizonFog"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Category.WORLD_CANDY)
        public boolean oldNetherFog = DefaultConfig.Candy.OLD_NETHER_FOG;
        static { CandyTweak.NETHER_FOG.setKey("oldNetherFog"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Category.WORLD_CANDY)
        public boolean oldSunriseAtNorth = DefaultConfig.Candy.OLD_SUNRISE_AT_NORTH;
        static { CandyTweak.SUNRISE_AT_NORTH.setKey("oldSunriseAtNorth"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Category.WORLD_CANDY)
        public boolean oldBlueVoidOverride = DefaultConfig.Candy.OLD_BLUE_VOID_OVERRIDE;
        static { CandyTweak.BLUE_VOID_OVERRIDE.setKey("oldBlueVoidOverride"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Category.WORLD_CANDY)
        public boolean oldDarkVoidHeight = DefaultConfig.Candy.OLD_DARK_VOID_HEIGHT;
        static { CandyTweak.DARK_VOID_HEIGHT.setKey("oldDarkVoidHeight"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Category.WORLD_CANDY)
        public boolean oldSunriseSunsetFog = DefaultConfig.Candy.OLD_SUNRISE_SUNSET_FOG;
        static { CandyTweak.SUNRISE_SUNSET_FOG.setKey("oldSunriseSunsetFog"); }

        @TweakSide.Server
        @TweakClient.Run.ReloadChunks
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Category.WORLD_CANDY)
        public boolean oldSquareBorder = DefaultConfig.Candy.OLD_SQUARE_BORDER;
        static { CandyTweak.SQUARE_BORDER.setKey("oldSquareBorder"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Category.WORLD_CANDY)
        public boolean oldStars = DefaultConfig.Candy.OLD_STARS;
        static { CandyTweak.STARS.setKey("oldStars"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Category.WORLD_CANDY)
        public TweakVersion.Generic oldSkyColor = DefaultConfig.Candy.OLD_SKY_COLOR;
        static { CandyTweak.SKY_COLOR.setKey("oldSkyColor"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Category.WORLD_CANDY)
        public TweakVersion.Generic oldFogColor = DefaultConfig.Candy.OLD_FOG_COLOR;
        static { CandyTweak.FOG_COLOR.setKey("oldFogColor"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Category.WORLD_CANDY)
        public TweakVersion.Generic oldBlueVoid = DefaultConfig.Candy.OLD_BLUE_VOID;
        static { CandyTweak.BLUE_VOID.setKey("oldBlueVoid"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        @TweakClient.Gui.Sub(group = TweakClient.Category.WORLD_CANDY)
        @TweakClient.Gui.SliderType(slider = TweakClient.Gui.Slider.CLOUD_SLIDER)
        @TweakClient.Gui.DisabledInteger(disabled = 192)
        @ConfigEntry.BoundedDiscrete(min = 108, max = 192)
        public int oldCloudHeight = DefaultConfig.Candy.OLD_CLOUD_HEIGHT;
        static { CandyTweak.CLOUD_HEIGHT.setKey("oldCloudHeight"); }
    }

    @TweakSide.Ignore
    public Animation animation = new Animation();
    public static class Animation
    {
        @TweakSide.Client
        @TweakSide.EntryStatus
        public boolean oldSwing = DefaultConfig.Animation.OLD_SWING;
        static { AnimationTweak.ITEM_SWING.setKey("oldSwing"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        public boolean oldItemCooldown = DefaultConfig.Animation.OLD_ITEM_COOLDOWN;
        static { AnimationTweak.COOLDOWN.setKey("oldItemCooldown"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        public boolean oldItemReequip = DefaultConfig.Animation.OLD_ITEM_REEQUIP;
        static { AnimationTweak.REEQUIP.setKey("oldItemReequip"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        public boolean oldArmSway = DefaultConfig.Animation.OLD_ARM_SWAY;
        static { AnimationTweak.ARM_SWAY.setKey("oldArmSway"); }

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.IgnoreDisable
        public boolean armSwayMirror = DefaultConfig.Animation.ARM_SWAY_MIRROR;
        static { AnimationTweak.ARM_SWAY_MIRROR.setKey("armSwayMirror"); }

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @TweakClient.Gui.SliderType(slider = TweakClient.Gui.Slider.INTENSITY_SLIDER)
        @ConfigEntry.BoundedDiscrete(min = 0, max = 300)
        public int armSwayIntensity = DefaultConfig.Animation.ARM_SWAY_INTENSITY;
        static { AnimationTweak.ARM_SWAY_INTENSITY.setKey("armSwayIntensity"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        public boolean oldCollideBobbing = DefaultConfig.Animation.OLD_COLLIDE_BOBBING;
        static { AnimationTweak.COLLIDE_BOB.setKey("oldCollideBobbing"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        public boolean oldVerticalBobbing = DefaultConfig.Animation.OLD_VERTICAL_BOBBING;
        static { AnimationTweak.BOB_VERTICAL.setKey("oldVerticalBobbing"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        public boolean oldSneaking = DefaultConfig.Animation.OLD_SNEAKING;
        static { AnimationTweak.SNEAK_SMOOTH.setKey("oldSneaking"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        public boolean oldSwingDropping = DefaultConfig.Animation.OLD_SWING_DROPPING;
        static { AnimationTweak.SWING_DROP.setKey("oldSwingDropping"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        public boolean oldToolExplosion = DefaultConfig.Animation.OLD_TOOL_EXPLOSION;
        static { AnimationTweak.TOOL_EXPLODE.setKey("oldToolExplosion"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        public boolean oldZombieArms = DefaultConfig.Animation.OLD_ZOMBIE_ARMS;
        static { AnimationTweak.ZOMBIE_ARMS.setKey("oldZombieArms"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        public boolean oldSkeletonArms = DefaultConfig.Animation.OLD_SKELETON_ARMS;
        static { AnimationTweak.SKELETON_ARMS.setKey("oldSkeletonArms"); }

        @TweakSide.Client
        @TweakSide.EntryStatus
        public boolean oldGhastCharging = DefaultConfig.Animation.OLD_GHAST_CHARGING;
        static { AnimationTweak.GHAST_CHARGING.setKey("oldGhastCharging"); }
    }

    @TweakSide.Ignore
    public Swing swing = new Swing();
    public static class Swing
    {
        @TweakSide.Client
        @TweakSide.EntryStatus
        public boolean overrideSpeeds = DefaultConfig.Swing.OVERRIDE_SPEEDS;
        static { SwingTweak.OVERRIDE_SPEEDS.setKey("overrideSpeeds"); }

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @ConfigEntry.BoundedDiscrete(min = MIN, max = MAX)
        public int tool = DefaultConfig.Swing.TOOL;

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @ConfigEntry.BoundedDiscrete(min = MIN, max = MAX)
        public int block = DefaultConfig.Swing.BLOCK;

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @ConfigEntry.BoundedDiscrete(min = MIN, max = MAX)
        public int sword = DefaultConfig.Swing.SWORD;

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @ConfigEntry.BoundedDiscrete(min = MIN, max = MAX)
        public int item = DefaultConfig.Swing.ITEM;

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @ConfigEntry.BoundedDiscrete(min = DefaultConfig.Swing.GLOBAL, max = MAX)
        public int haste = DefaultConfig.Swing.HASTE;

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @ConfigEntry.BoundedDiscrete(min = DefaultConfig.Swing.GLOBAL, max = MAX)
        public int fatigue = DefaultConfig.Swing.FATIGUE;

        @TweakSide.Client
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @ConfigEntry.BoundedDiscrete(min = DefaultConfig.Swing.GLOBAL, max = MAX)
        public int global = DefaultConfig.Swing.GLOBAL;
    }

    @TweakSide.Ignore
    public Gui gui = new Gui();
    public static class Gui
    {
        @TweakSide.EntryStatus(status = StatusType.LOADED)
        public MenuOption defaultScreen = DefaultConfig.Gui.DEFAULT_SCREEN;
        static { GuiTweak.DEFAULT_SCREEN.setKey("defaultScreen"); }

        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @SuppressWarnings("unused")
        public boolean displayNewTags = DefaultConfig.Gui.DISPLAY_NEW_TAGS;
        static { GuiTweak.DISPLAY_NEW_TAGS.setKey("displayNewTags"); }

        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @SuppressWarnings("unused")
        public boolean displaySidedTags = DefaultConfig.Gui.DISPLAY_SIDED_TAGS;
        static { GuiTweak.DISPLAY_SIDED_TAGS.setKey("displaySidedTags"); }

        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @SuppressWarnings("unused")
        public boolean displayTagTooltips = DefaultConfig.Gui.DISPLAY_TAG_TOOLTIPS;
        static { GuiTweak.DISPLAY_TAG_TOOLTIPS.setKey("displayTagTooltips"); }

        @TweakSide.EntryStatus(status = StatusType.LOADED)
        @SuppressWarnings("unused")
        public boolean displayFeatureStatus = DefaultConfig.Gui.DISPLAY_FEATURE_STATUS;
        static { GuiTweak.DISPLAY_FEATURE_STATUS.setKey("displayFeatureStatus"); }
    }

    @TweakSide.Ignore public Map<String, Integer> custom = Maps.newHashMap();
}