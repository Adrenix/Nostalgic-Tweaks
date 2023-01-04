package mod.adrenix.nostalgic.util.common;

import mod.adrenix.nostalgic.NostalgicTweaks;
import net.minecraft.resources.ResourceLocation;

/**
 * This class may be used by both the client and server.
 * Do not use vanilla client code here.
 */

public abstract class ModUtil
{
    public static class Resource
    {
        public static final ResourceLocation BLACK_RESOURCE = new ResourceLocation(NostalgicTweaks.MOD_ID + ":textures/black.png");
        public static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation(NostalgicTweaks.MOD_ID + ":textures/gui/widgets.png");
        public static final ResourceLocation COLOR_PICKER = new ResourceLocation(NostalgicTweaks.MOD_ID + ":textures/gui/overlay_picker.png");
        public static final ResourceLocation CATEGORY_LIST = new ResourceLocation(NostalgicTweaks.MOD_ID + ":textures/gui/overlay_list.png");
        public static final ResourceLocation OLD_INVENTORY = new ResourceLocation(NostalgicTweaks.MOD_ID + ":textures/gui/inventory.png");
        public static final ResourceLocation MOJANG_ALPHA = new ResourceLocation(NostalgicTweaks.MOD_ID + ":textures/gui/mojang_alpha.png");
        public static final ResourceLocation MOJANG_BETA = new ResourceLocation(NostalgicTweaks.MOD_ID + ":textures/gui/mojang_beta.png");
        public static final ResourceLocation MOJANG_RELEASE_ORANGE = new ResourceLocation(NostalgicTweaks.MOD_ID + ":textures/gui/mojang_release_orange.png");
        public static final ResourceLocation MOJANG_RELEASE_BLACK = new ResourceLocation(NostalgicTweaks.MOD_ID + ":textures/gui/mojang_release_black.png");
        public static final ResourceLocation NOSTALGIC_LOGO = new ResourceLocation(NostalgicTweaks.MOD_ID + ":textures/title.png");
        public static final ResourceLocation MINECRAFT_LOGO = new ResourceLocation("textures/gui/title/minecraft.png");
    }

    public static class Run
    {
        /**
         * Used in loops that want to "simulate" work being done.
         * This is used mostly in progress screens.
         */
        public static void nothing() {}
    }
}
