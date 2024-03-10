package mod.adrenix.nostalgic.util.client;

import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class GameUtil
{
    /**
     * @return The current version of the game.
     */
    public static String getVersion()
    {
        return SharedConstants.getCurrentVersion().getName();
    }

    /**
     * Check if the current game mode is creative.
     *
     * @return Whether Minecraft is in creative mode.
     */
    public static boolean isCreativeMode()
    {
        return Minecraft.getInstance().gameMode != null && Minecraft.getInstance().gameMode.hasInfiniteItems();
    }

    /**
     * Check if the current game mode is not creative.
     *
     * @return Whether Minecraft is not in creative mode.
     */
    public static boolean isNotCreativeMode()
    {
        return !isCreativeMode();
    }

    /**
     * Used to check if a model should be rendered in 2D.
     *
     * @param model The {@link BakedModel} to check.
     * @return Whether the given model uses block light.
     */
    public static boolean isModelFlat(BakedModel model)
    {
        return !model.usesBlockLight();
    }

    /**
     * Shortcut for checking if a model is flat based on the given item stack.
     *
     * @param itemStack The {@link ItemStack} to get model data from.
     * @return Whether the item stack is rendered flat.
     */
    public static boolean isModelFlat(ItemStack itemStack)
    {
        return isModelFlat(Minecraft.getInstance().getItemRenderer().getModel(itemStack, null, null, 0));
    }

    /**
     * Shortcut for checking if a model is flat based on the given item.
     *
     * @param item The {@link Item} to get model data from.
     * @return Whether the item is rendered flat.
     */
    public static boolean isModelFlat(Item item)
    {
        return isModelFlat(item.getDefaultInstance());
    }
}
