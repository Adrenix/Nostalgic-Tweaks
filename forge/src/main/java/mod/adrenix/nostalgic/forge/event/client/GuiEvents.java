package mod.adrenix.nostalgic.forge.event.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.client.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;

/**
 * This helper class defines instructions for overriding the in-game overlay and for overriding Forge's debugging
 * overlay.
 */

public abstract class GuiEvents
{
    /**
     * Vanilla debug overlay reference. This is needed so that the vanilla debug overlay can be rendered instead of the
     * modded Forge version.
     */
    private static final DebugScreenOverlay DEBUG_OVERLAY = new DebugScreenOverlay(Minecraft.getInstance());

    /**
     * Overrides the rendering of the in-game overlay and debugging overlay.
     * @param event A pre-render gui overlay event instance.
     */
    public static void overlayOverride(RenderGameOverlayEvent.PreLayer event)
    {
        Minecraft minecraft = Minecraft.getInstance();

        // Prevent Rendering in F1 Mode
        if (minecraft.options.hideGui)
        {
            event.setCanceled(true);
            return;
        }

        // Old Version & Alternative HUD Elements

        GuiUtil.renderOverlays(event.getMatrixStack());

        // Overlay Overrides

        IIngameOverlay overlay = event.getOverlay();
        boolean isDebug = overlay.equals(ForgeIngameGui.HUD_TEXT_ELEMENT) || overlay.equals(ForgeIngameGui.FPS_GRAPH_ELEMENT);
        boolean isArmor = overlay.equals(ForgeIngameGui.ARMOR_LEVEL_ELEMENT);
        boolean isFood = overlay.equals(ForgeIngameGui.FOOD_LEVEL_ELEMENT);
        boolean isAir = overlay.equals(ForgeIngameGui.AIR_LEVEL_ELEMENT);
        boolean isOverlay = isArmor || isFood || isAir;
        boolean isVehiclePresent = false;
        boolean isSurvivalMode = true;

        if (minecraft.getCameraEntity() instanceof Player player)
        {
            if (player.isCreative() || player.isSpectator())
                isSurvivalMode = false;

            if (player.getVehicle() instanceof LivingEntity)
                isVehiclePresent = true;
        }

        // Run Modified Vanilla Overlays and Cancel Event

        if (minecraft.options.renderDebug && isDebug)
        {
            DEBUG_OVERLAY.render(event.getMatrixStack());
            event.setCanceled(true);
        }

        if (isOverlay && isSurvivalMode && !isVehiclePresent)
        {
            PoseStack poseStack = event.getMatrixStack();
            ForgeIngameGui gui = (ForgeIngameGui) minecraft.gui;

            gui.setupOverlayRenderState(true, false);

            int width = event.getWindow().getGuiScaledWidth();
            int height = event.getWindow().getGuiScaledHeight();

            if (isArmor)
                renderArmor(gui, width, height, poseStack);
            else if (isFood)
                renderFood(gui, width, height, poseStack);
            else
                renderAir(gui, width, height, poseStack);
        }
    }

    /**
     * Renders a modified food overlay.
     * @param gui A forge GUI instance.
     * @param width The current screen width.
     * @param height The current screen height.
     * @param poseStack The current pose stack.
     */
    private static void renderFood(ForgeIngameGui gui, int width, int height, PoseStack poseStack)
    {
        if (ModConfig.Gameplay.disableHungerBar())
            return;

        Minecraft minecraft = Minecraft.getInstance();
        Player player = (Player) minecraft.getCameraEntity();

        if (player == null)
            return;

        minecraft.getProfiler().push("food");
        RenderSystem.enableBlend();

        GuiUtil.renderFood(gui, poseStack, player, width, height, gui.right_height);

        RenderSystem.disableBlend();
        minecraft.getProfiler().pop();
    }

    /**
     * Renders a modified armor overlay.
     * @param gui A forge GUI instance.
     * @param width The current screen width.
     * @param height The current screen height.
     * @param poseStack The current pose stack.
     */
    private static void renderArmor(ForgeIngameGui gui, int width, int height, PoseStack poseStack)
    {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null)
            return;

        minecraft.getProfiler().push("armor");
        RenderSystem.enableBlend();

        GuiUtil.renderArmor(gui, poseStack, minecraft.player, width, height, gui.left_height, gui.right_height);

        RenderSystem.disableBlend();
        minecraft.getProfiler().pop();
    }

    /**
     * Boolean supplier that is used by the common GUI utility to check if the player is losing air.
     * @param player A player instance.
     * @return Whether the player is losing air.
     */
    public static boolean isPlayerLosingAir(Player player)
    {
        return player.isEyeInFluid(FluidTags.WATER) || player.getAirSupply() < 300;
    }

    /**
     * Renders a modified air bubble overlay.
     * @param gui A forge GUI instance.
     * @param width The current screen width.
     * @param height The current screen height.
     * @param poseStack The current pose stack.
     */
    private static void renderAir(ForgeIngameGui gui, int width, int height, PoseStack poseStack)
    {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = (Player) minecraft.getCameraEntity();

        if (player == null)
            return;

        minecraft.getProfiler().push("air");
        RenderSystem.enableBlend();

        GuiUtil.renderAir(GuiEvents::isPlayerLosingAir, gui, poseStack, player, width, height, gui.left_height, gui.right_height);

        RenderSystem.disableBlend();
        minecraft.getProfiler().pop();
    }
}
