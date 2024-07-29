package mod.adrenix.nostalgic.helper.candy.flatten;

import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.SpawnEggItem;

/**
 * This utility class is used only by the client.
 */
public abstract class ItemColorHelper
{
    /**
     * @return Whether the item renderer can apply old item coloring simulations.
     */
    public static boolean isReady()
    {
        return CandyTweak.OLD_2D_COLORS.get() && FlatItemHelper.isRendering2D();
    }

    /**
     * Shifts an item's color.
     *
     * @param SHIFT_RGB The current color.
     * @param SHIFT     The color modifier.
     */
    private static void shiftItemColor(final float[] SHIFT_RGB, final float SHIFT)
    {
        SHIFT_RGB[0] = Mth.clamp(SHIFT_RGB[0] + (SHIFT_RGB[0] * SHIFT), 0.0F, 255.0F);
        SHIFT_RGB[1] = Mth.clamp(SHIFT_RGB[1] + (SHIFT_RGB[1] * SHIFT), 0.0F, 255.0F);
        SHIFT_RGB[2] = Mth.clamp(SHIFT_RGB[2] + (SHIFT_RGB[2] * SHIFT), 0.0F, 255.0F);
    }

    /**
     * Shifts leather item colors.
     *
     * @param SHIFT_RGB The current leather color.
     */
    private static void shiftLeatherItemColor(final float[] SHIFT_RGB)
    {
        final float LIGHT = 0.4F * SHIFT_RGB[0] + 0.6F * SHIFT_RGB[1] + 0.1F * SHIFT_RGB[2];

        SHIFT_RGB[0] = Mth.clamp(SHIFT_RGB[0] + 0.1F * (LIGHT - SHIFT_RGB[0]), 0.0F, 255.0F);
        SHIFT_RGB[1] = Mth.clamp(SHIFT_RGB[1] + 0.1F * (LIGHT - SHIFT_RGB[1]), 0.0F, 255.0F);
        SHIFT_RGB[2] = Mth.clamp(SHIFT_RGB[2] + 0.1F * (LIGHT - SHIFT_RGB[2]), 0.0F, 255.0F);

        shiftItemColor(SHIFT_RGB, 0.4F);
    }

    /**
     * Gets a modified color for old 2D item colors.
     *
     * @param itemColor The {@link ItemColor} instance.
     * @param stack     The {@link ItemStack} instance.
     * @param tintIndex The tint index.
     * @return An RGB integer for coloring.
     */
    public static int apply(ItemColor itemColor, ItemStack stack, int tintIndex)
    {
        final int COLOR = itemColor.getColor(stack, tintIndex);
        final int[] ITEM_RGB = new int[] { (COLOR & 0xFF0000) >> 16, (COLOR & 0xFF00) >> 8, COLOR & 0xFF };
        final float[] SHIFT_RGB = new float[] { ITEM_RGB[0], ITEM_RGB[1], ITEM_RGB[2] };

        if (stack.getItem() instanceof SpawnEggItem)
            shiftItemColor(SHIFT_RGB, 0.35F);
        else if (stack.getItem() instanceof PotionItem)
            shiftItemColor(SHIFT_RGB, 0.37F);
        else if (stack.getItem() instanceof DyeableLeatherItem)
            shiftLeatherItemColor(SHIFT_RGB);
        else
            shiftItemColor(SHIFT_RGB, 0.35F);

        return (int) SHIFT_RGB[0] << 16 | (int) SHIFT_RGB[1] << 8 | (int) SHIFT_RGB[2];
    }
}
