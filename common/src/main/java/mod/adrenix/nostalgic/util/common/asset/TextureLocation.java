package mod.adrenix.nostalgic.util.common.asset;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.common.LocateResource;
import net.minecraft.resources.ResourceLocation;

public class TextureLocation
{
    /* Locations */

    public static final ResourceLocation DEV_MODE = ModAsset.texture("gui/nt_dev.png");
    public static final ResourceLocation BLOCK_SHADOW = ModAsset.texture("block_shadow.png");
    public static final ResourceLocation INVENTORY = ModAsset.texture("gui/inventory.png");
    public static final ResourceLocation ALL_ITEMS = ModAsset.texture("gui/allitems.png");
    public static final ResourceLocation MOJANG_ALPHA = ModAsset.texture("gui/mojang_alpha.png");
    public static final ResourceLocation MOJANG_BETA = ModAsset.texture("gui/mojang_beta.png");
    public static final ResourceLocation MOJANG_RELEASE_ORANGE = ModAsset.texture("gui/mojang_release_orange.png");
    public static final ResourceLocation MOJANG_RELEASE_BLACK = ModAsset.texture("gui/mojang_release_black.png");
    public static final ResourceLocation PANORAMA_OVERLAY = ModAsset.texture("panorama/overlay.png");
    public static final ResourceLocation DIRT_BACKGROUND = GameAsset.texture("block/dirt.png");
    public static final ResourceLocation MENU_LIST_BACKGROUND = GameAsset.texture("gui/menu_list_background.png");
    public static final ResourceLocation LEVEL_MENU_LIST_BACKGROUND = GameAsset.texture("gui/inworld_menu_list_background.png");

    public static final TextureLocation NT_LOGO_64 = new TextureLocation(ModAsset.icon("nt_logo_64.png"), 64);
    public static final TextureLocation NT_SUPPORTER_64 = new TextureLocation(ModAsset.icon("nt_supporter_64.png"), 64);
    public static final TextureLocation SOUND_256 = new TextureLocation(ModAsset.twemoji("sound_256.png"), 256);
    public static final TextureLocation CANDY_256 = new TextureLocation(ModAsset.twemoji("candy_256.png"), 256);
    public static final TextureLocation JUGGLER_256 = new TextureLocation(ModAsset.twemoji("juggler_256.png"), 256);
    public static final TextureLocation CONTROLLER_256 = new TextureLocation(ModAsset.twemoji("controller_256.png"), 256);
    public static final TextureLocation NOSTALGIC_TWEAKS = new TextureLocation("nostalgic_tweaks.png", 1920, 182);

    /* Missing Textures */

    public static final String MISSING_BETA = String.format("assets/%s/textures/missing/beta.png", NostalgicTweaks.MOD_ID);
    public static final String MISSING_1_5 = String.format("assets/%s/textures/missing/1_5.png", NostalgicTweaks.MOD_ID);
    public static final String MISSING_1_6_1_12 = String.format("assets/%s/textures/missing/1_6-1_12.png", NostalgicTweaks.MOD_ID);

    /* Fields */

    private final ResourceLocation location;
    private final int width;
    private final int height;

    /* Constructor */

    /**
     * Create a new image texture location from a resource location.
     *
     * @param resourceLocation The {@link ResourceLocation} instance.
     * @param width            The image's width.
     * @param height           The image's height.
     */
    public TextureLocation(ResourceLocation resourceLocation, int width, int height)
    {
        this.location = resourceLocation;
        this.width = width;
        this.height = height;
    }

    /**
     * Create a new image texture location.
     *
     * @param path   A path in the mod's textures directory.
     * @param width  The image's width.
     * @param height The image's height.
     */
    public TextureLocation(String path, int width, int height)
    {
        this.location = LocateResource.mod("textures/" + path);
        this.width = width;
        this.height = height;
    }

    /**
     * Create a new image texture location.
     *
     * @param resourceLocation The {@link ResourceLocation} instance.
     * @param size             The square size of the texture.
     */
    public TextureLocation(ResourceLocation resourceLocation, int size)
    {
        this(resourceLocation, size, size);
    }

    /**
     * Create a new square texture location.
     *
     * @param path A path in the mod's textures directory.
     * @param size The square size of the texture.
     */
    public TextureLocation(String path, int size)
    {
        this(path, size, size);
    }

    /* Methods */

    public ResourceLocation getLocation()
    {
        return this.location;
    }

    public int getWidth()
    {
        return this.width;
    }

    public int getHeight()
    {
        return this.height;
    }

    public float getAverageSize()
    {
        return (this.width + this.height) / 2.0F;
    }
}
