package mod.adrenix.nostalgic.util.client;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.mixin.duck.IReequipSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.*;

import javax.annotation.Nullable;

/**
 * This utility class uses client only Minecraft code. For safety, the server should not interface with this utility.
 * For a server safe mixin utility use {@link mod.adrenix.nostalgic.util.server.ItemServerUtil}.
 */

public abstract class ItemClientUtil
{
    // Used to enhance the old reequipping logic
    public static ItemStack getLastItem(ItemStack originalItemStack, ItemStack rendererItemStack, ItemStack playerItemStack, IReequipSlot player)
    {
        // Item from main hand turns to air as soon as the player pulls it out. When this happens, the following strings appear in each property respectively.
        boolean isUnequipped = rendererItemStack.toString().equals("0 air") && playerItemStack.toString().equals("1 air");
        if (!ModConfig.Animation.oldItemReequip() || !isUnequipped)
            return originalItemStack;

        return player.NT$getLastItem();
    }

    // Tells the item renderer if we're rendering a flat item.
    private static boolean isRenderingFlat = false;

    // Used to cache the current level pose stack position matrix for re-enabling diffused lighting after flat rendering.
    @Nullable
    public static PoseStack.Pose levelPoseStack;

    // Used to cache the current buffer source during the entity render cycle.
    // This is needed so we can end the batch early to apply flat lighting to vertices.
    public static MultiBufferSource.BufferSource levelBufferSource;

    // Used to check if a model should be rendered in 2D.
    public static boolean isModelFlat(BakedModel model)
    {
        return !model.usesBlockLight();
    }

    // Flattens an item to be as close to 2D as possible via scaling.
    public static void flatten(PoseStack poseStack)
    {
        poseStack.scale(1.0F, 1.0F, 0.001F);
    }

    // Getter for checking if diffused lighting is disabled.
    public static boolean isLightingFlat()
    {
        return isRenderingFlat;
    }

    // Turns off diffused lighting.
    public static void disableDiffusedLighting()
    {
        if (levelBufferSource != null)
            levelBufferSource.endBatch();

        Lighting.setupForFlatItems();
        isRenderingFlat = true;
    }

    // Turns on diffused lighting.
    public static void enableDiffusedLighting()
    {
        isRenderingFlat = false;

        if (levelBufferSource != null)
            levelBufferSource.endBatch();

        if (Minecraft.getInstance().level == null || levelPoseStack == null)
            return;

        if (Minecraft.getInstance().level.effects().constantAmbientLight())
            Lighting.setupNetherLevel(levelPoseStack.pose());
        else
            Lighting.setupLevel(levelPoseStack.pose());
    }

    // Used to change the normal based on which quad side we're rendering.
    public static void setNormalQuad(PoseStack.Pose pose, BakedQuad quad)
    {
        pose.normal().setIdentity();
        if (quad.getDirection() == Direction.NORTH)
            pose.normal().mul(-1.0F);
    }

    // Checks if an item stack can be colored
    public static boolean isValidColorItem(ItemStack stack)
    {
        BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(stack, null, null, 0);
        return ModConfig.Candy.oldFlatColors() && isModelFlat(model);
    }

    private static void shiftItemColor(final float[] SHIFT_RGB, final float SHIFT)
    {
        SHIFT_RGB[0] = Mth.clamp(SHIFT_RGB[0] + (SHIFT_RGB[0] * SHIFT), 0.0F, 255.0F);
        SHIFT_RGB[1] = Mth.clamp(SHIFT_RGB[1] + (SHIFT_RGB[1] * SHIFT), 0.0F, 255.0F);
        SHIFT_RGB[2] = Mth.clamp(SHIFT_RGB[2] + (SHIFT_RGB[2] * SHIFT), 0.0F, 255.0F);
    }

    private static void shiftLeatherItemColor(final float[] SHIFT_RGB)
    {
        final float LIGHT = 0.3F * SHIFT_RGB[0] + 0.6F * SHIFT_RGB[1] + 0.1F * SHIFT_RGB[2];

        SHIFT_RGB[0] = Mth.clamp(SHIFT_RGB[0] + 0.1F * (LIGHT - SHIFT_RGB[0]), 0.0F, 255.0F);
        SHIFT_RGB[1] = Mth.clamp(SHIFT_RGB[1] + 0.1F * (LIGHT - SHIFT_RGB[1]), 0.0F, 255.0F);
        SHIFT_RGB[2] = Mth.clamp(SHIFT_RGB[2] + 0.1F * (LIGHT - SHIFT_RGB[2]), 0.0F, 255.0F);

        shiftItemColor(SHIFT_RGB, 0.3F);
    }

    // Gets a modified color for old 2D item colors
    public static int getOldColor(ItemColor itemColor, ItemStack stack, int tintIndex)
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
