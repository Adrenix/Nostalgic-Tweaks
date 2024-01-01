package mod.adrenix.nostalgic.util.common.asset;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * This class is server-safe and must remain server-safe since config definitions use icons. The client will render
 * icons via an icon widget.
 */
public class TextureIcon
{
    /* Static */

    public static final TextureIcon EMPTY = new TextureIcon(0, 0, 0, 0);

    /* Fields */

    private final int u;
    private final int v;

    private final int width;
    private final int height;
    private float brightenAmount;

    private ResourceLocation textureSheet;
    @Nullable private TextureLocation textureLocation;
    @Nullable private Block block;
    @Nullable private Item item;

    /* Constructors */

    /**
     * Create a new texture icon instance.
     *
     * @param u      The u-position of where the icon sits on the texture sheet.
     * @param v      The v-position of where the icon sits on the texture sheet.
     * @param width  The width of the icon as defined in the texture sheet.
     * @param height The height of the icon as defined in the texture sheet.
     */
    private TextureIcon(int u, int v, int width, int height)
    {
        this.u = u;
        this.v = v;
        this.width = width;
        this.height = height;
        this.brightenAmount = 1.02F;
        this.textureSheet = TextureLocation.WIDGETS;
    }

    /**
     * Create a new texture icon instance using an item.
     *
     * @param item The item to render an icon from.
     */
    private TextureIcon(@Nullable Item item)
    {
        this(0, 0, 16, 16);

        this.item = item;
    }

    /**
     * Create a new texture icon instance using a texture location.
     *
     * @param location A texture location instance.
     */
    private TextureIcon(TextureLocation location)
    {
        this(0, 0, location.getWidth(), location.getHeight());

        this.textureLocation = location;
    }

    /* Getters */

    /**
     * @return The u-coordinate of this icon on the texture sheet.
     */
    public int getU()
    {
        return this.u;
    }

    /**
     * @return The v-coordinate of this icon on the texture sheet.
     */
    public int getV()
    {
        return this.v;
    }

    /**
     * @return The width of this icon on the texture sheet.
     */
    public int getWidth()
    {
        return this.width;
    }

    /**
     * @return The height of this icon on the texture sheet.
     */
    public int getHeight()
    {
        return this.height;
    }

    /**
     * @return The maximum amount of brightness that can be applied to an icon.
     */
    public float getBrightness()
    {
        return this.brightenAmount;
    }

    /**
     * @return The {@link TextureLocation} of this icon, if it exists.
     */
    public Optional<TextureLocation> getTextureLocation()
    {
        return Optional.ofNullable(this.textureLocation);
    }

    /**
     * @return The block instance associated with this icon if it is present.
     */
    public Optional<Block> getBlock()
    {
        return Optional.ofNullable(this.block);
    }

    /**
     * @return The item instance associated with this icon if it is present.
     */
    public Optional<Item> getItem()
    {
        return Optional.ofNullable(this.item);
    }

    /**
     * @return The resource location of the texture sheet.
     */
    public ResourceLocation getTextureSheet()
    {
        return this.textureSheet;
    }

    /* Methods */

    /**
     * Checks if this instance uses {@link TextureIcon#EMPTY}.
     *
     * @return Whether this icon is {@code EMPTY}.
     */
    public boolean isEmpty()
    {
        return this.equals(TextureIcon.EMPTY);
    }

    /**
     * Functional shortcut to check if this instance is not using {@link TextureIcon#EMPTY}.
     *
     * @return Whether this icon is not {@code EMPTY}.
     */
    public boolean isPresent()
    {
        return !this.isEmpty();
    }

    /**
     * Change the resource location of where this icon's texture is found.
     * <p><br><b color=red>Important:</b> Texture sheets must be 256x256.
     *
     * @param location A resource location instance.
     * @return The invoking instance so that functional chaining can be used.
     */
    public TextureIcon setResourceLocation(ResourceLocation location)
    {
        this.textureSheet = location;
        return this;
    }

    /**
     * @param amount The amount of brightness to use when the icon is brightened.
     * @return The invoking instance so that functional chaining can be used.
     */
    public TextureIcon setBrightenAmount(float amount)
    {
        this.brightenAmount = amount;
        return this;
    }

    /* Builders */

    /**
     * Create a new icon instance using a factory.
     *
     * @return An icon factory builder instance.
     */
    public static Builder create()
    {
        return new Builder();
    }

    /**
     * Create a new icon instance from an item.
     *
     * @param item An item instance.
     * @return A new {@link TextureIcon} instance.
     */
    public static TextureIcon fromItem(Item item)
    {
        TextureIcon icon = new TextureIcon(item);
        icon.item = item;

        return icon;
    }

    /**
     * Create a new icon instance from a block.
     *
     * @param block A block instance.
     * @return A new {@link TextureIcon} instance.
     */
    public static TextureIcon fromBlock(Block block)
    {
        TextureIcon icon = new TextureIcon(block.asItem());
        icon.block = block;

        return icon;
    }

    /**
     * Create a new icon instance from a texture location.
     *
     * @param location A texture location instance.
     * @return A new {@link TextureIcon} instance.
     */
    public static TextureIcon fromTexture(TextureLocation location)
    {
        return new TextureIcon(location);
    }

    public static class Builder
    {
        private ResourceLocation location = TextureLocation.WIDGETS;

        private int u = 0;
        private int v = 0;
        private int width = 0;
        private int height = 0;
        private float brightenAmount = 1.02F;

        private Builder()
        {
        }

        /**
         * Set the (u, v) location of this icon on the texture sheet.
         *
         * @param u The u-position.
         * @param v The v-position.
         */
        public Builder uv(int u, int v)
        {
            this.u = u;
            this.v = v;

            return this;
        }

        /**
         * If the icon has a square size (i.e., the same width and height) then use this shortcut method to define the
         * width of height as defined in the texture sheet.
         *
         * @param size The size of the square.
         */
        public Builder square(int size)
        {
            this.width = size;
            this.height = size;

            return this;
        }

        /**
         * Set the width and height of this icon as defined in the texture sheet.
         *
         * @param width  The icon's width.
         * @param height The icon's height.
         */
        public Builder size(int width, int height)
        {
            this.width(width);
            this.height(height);

            return this;
        }

        /**
         * Set the width of this icon as defined in the texture sheet.
         *
         * @param width The icon's width.
         */
        public Builder width(int width)
        {
            this.width = width;
            return this;
        }

        /**
         * Set the height of this icon as defined in the texture sheet.
         *
         * @param height The icon's height.
         */
        public Builder height(int height)
        {
            this.height = height;
            return this;
        }

        /**
         * Change the resource location of where this icon's texture is found.
         * <br><br><b color=red>Important:</b> Texture sheets must be 256x256.
         *
         * @param location A resource location instance.
         */
        public Builder location(ResourceLocation location)
        {
            this.location = location;
            return this;
        }

        /**
         * Change the amount of brightness that is applied to the icon when it is brightened. The default brightness
         * amount is set to {@code 1.02F}. This is useful in situations such as brightening an icon when the mouse
         * hovers over it.
         *
         * @param amount The amount of brightness to apply when the icon is brightened.
         */
        public Builder maxBrightness(float amount)
        {
            this.brightenAmount = amount;
            return this;
        }

        /**
         * Finalize the building process of this icon.
         *
         * @return A new icon instance.
         */
        public TextureIcon build()
        {
            return new TextureIcon(this.u, this.v, this.width, this.height).setResourceLocation(this.location)
                .setBrightenAmount(this.brightenAmount);
        }
    }
}
