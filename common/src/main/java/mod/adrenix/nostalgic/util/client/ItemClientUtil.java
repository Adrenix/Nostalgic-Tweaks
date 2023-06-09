package mod.adrenix.nostalgic.util.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.mixin.duck.SlotTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This utility class uses client only Minecraft code. For safety, the server should not interface with this utility.
 * For a server safe mixin utility use {@link mod.adrenix.nostalgic.util.server.ItemServerUtil}.
 */

public abstract class ItemClientUtil
{
    /**
     * Used to enhance the old reequipping logic by preventing visual glitches when pulling items out of the player's
     * hand.
     *
     * @param originalItemStack The original item stack.
     * @param rendererItemStack The renderer's item stack.
     * @param playerItemStack   The player's item stack.
     * @param player            The current player.
     * @return What should be considered the last item stack.
     */
    public static ItemStack getLastItem(ItemStack originalItemStack, ItemStack rendererItemStack, ItemStack playerItemStack, SlotTracker player)
    {
        // Item from main hand turns to air as soon as the player pulls it out. When this happens, the following strings appear in each property respectively.
        boolean isUnequipped = rendererItemStack.toString().equals("0 air") &&
            playerItemStack.toString().equals("1 air");

        if (!ModConfig.Animation.oldItemReequip() || !isUnequipped)
            return originalItemStack;

        return player.NT$getLastItem();
    }

    /**
     * Tells the item renderer if we're rendering a flat item.
     */
    private static boolean isRenderingFlat = false;

    /**
     * Used to cache the current level pose stack position matrix for re-enabling diffused lighting after flat
     * rendering.
     */
    public static PoseStack.Pose levelPoseStack;

    /**
     * Used to cache the current buffer source during the entity render cycle. This is needed so we can end the batch
     * early to apply flat lighting to vertices.
     */
    public static MultiBufferSource.BufferSource levelBufferSource;

    /**
     * Used to check if a model should be rendered in 2D.
     *
     * @param model The model to check.
     * @return Whether the given model uses block light.
     */
    public static boolean isModelFlat(BakedModel model) { return !model.usesBlockLight(); }

    /**
     * Shortcut for checking if a model is flat based on the given item stack.
     *
     * @param itemStack The item stack to get model data from.
     * @return Whether the item stack can render as flat.
     */
    public static boolean isModelFlat(ItemStack itemStack)
    {
        return isModelFlat(Minecraft.getInstance().getItemRenderer().getModel(itemStack, null, null, 0));
    }

    /**
     * Flattens an item to be as close to 2D as possible via scaling.
     *
     * @param graphics The current GuiGraphics object.
     */
    public static void flatten(PoseStack poseStack) { poseStack.scale(1.0F, 1.0F, 0.001F); }

    /**
     * Getter for checking if diffused lighting is disabled.
     *
     * @return Whether the item renderer should be rendering flat items.
     */
    public static boolean isLightingFlat() { return isRenderingFlat; }

    /**
     * Turns off diffused lighting.
     */
    public static void disableDiffusedLighting()
    {
        if (levelBufferSource != null)
            levelBufferSource.endBatch();

        Lighting.setupForFlatItems();

        isRenderingFlat = true;
    }

    /**
     * Turns on diffused lighting.
     */
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

    /**
     * Used to change the normal based on which quad side we're rendering.
     *
     * @param pose The current matrix in the pose stack.
     * @param quad The quad to make changes to.
     */
    public static void setNormalQuad(PoseStack.Pose pose, BakedQuad quad)
    {
        pose.normal().identity();

        if (quad.getDirection() == Direction.NORTH)
            pose.normal().scale(-1.0F);
    }

    /**
     * Returns a singleton list of the front facing quad, or all quads if not rendering in 2D.
     *
     * @param quads A list of model quads.
     * @return A new list of quads or same list if not rendering in 2D.
     */
    public static List<BakedQuad> getSprites(List<BakedQuad> quads)
    {
        if (!isRenderingFlat)
            return quads;

        List<BakedQuad> southQuads = new ArrayList<>();

        for (BakedQuad baked : quads)
        {
            if (baked.getDirection() == Direction.SOUTH)
                southQuads.add(baked);
        }

        return southQuads;
    }

    /**
     * Checks if an item stack can be colored.
     *
     * @return Whether the given item stack should have its color modified.
     */
    public static boolean isValidColorItem()
    {
        return ModConfig.Candy.oldFlatColors() && isRenderingFlat;
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
     * @param itemColor The current item color.
     * @param stack     The item stack reference.
     * @param tintIndex The tint index.
     * @return An RGB integer for coloring.
     */
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

    /**
     * Similar to {@link ItemRenderer#renderGuiItem(PoseStack, ItemStack, int, int)} except that this method changes
     * render scaling.
     *
     * @param itemStack The item stack to render.
     * @param x         Where the rendering starts on the x-axis.
     * @param y         Where the rendering starts on the y-axis.
     * @param scale     The custom scaling for rendering.
     * @param dx        The change in x-axis translation.
     * @param dy        The change in y-axis translation.
     */
    public static void renderGuiItem(ItemStack itemStack, int x, int y, float scale, float dx, float dy)
    {
        Minecraft.getInstance().getTextureManager().getTexture(InventoryMenu.BLOCK_ATLAS).setFilter(false, false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        PoseStack viewStack = RenderSystem.getModelViewStack();
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        viewStack.pushPose();
        viewStack.translate(x, y, 100.0F);
        viewStack.translate(8.0F + dx, 8.0F + dy, 0.0F);
        viewStack.scale(1.0F, -1.0F, 1.0F);
        viewStack.scale(16.0f, 16.0f, 16.0f);
        viewStack.scale(scale, scale, scale);
        RenderSystem.applyModelViewMatrix();

        PoseStack poseStack = new PoseStack();
        BakedModel bakedModel = itemRenderer.getModel(itemStack, null, null, 0);
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        boolean isLightingFlat = !bakedModel.usesBlockLight();

        if (isLightingFlat)
            Lighting.setupForFlatItems();

        itemRenderer.render(itemStack, ItemDisplayContext.GUI, false, poseStack, buffer, 0xF000F0, OverlayTexture.NO_OVERLAY, bakedModel);

        buffer.endBatch();
        RenderSystem.enableDepthTest();

        if (isLightingFlat)
            Lighting.setupFor3DItems();

        viewStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    /**
     * Shortcut for rendering a GUI item with a custom scale and translation in the y-axis.
     *
     * @see ItemClientUtil#renderGuiItem(ItemStack, int, int, float, float)
     */
    public static void renderGuiItem(ItemStack itemStack, int x, int y, float scale, float dy)
    {
        ItemClientUtil.renderGuiItem(itemStack, x, y, scale, 0.0F, dy);
    }
}
