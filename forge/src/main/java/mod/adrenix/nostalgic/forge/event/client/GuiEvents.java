package mod.adrenix.nostalgic.forge.event.client;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.client.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.NamedGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.ForgeMod;

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
    public static void overlayOverride(RenderGuiOverlayEvent.Pre event)
    {
        Minecraft minecraft = Minecraft.getInstance();

        // Prevent Rendering in F1 Mode
        if (minecraft.options.hideGui)
        {
            event.setCanceled(true);
            return;
        }

        // Old Version & Alternative HUD Elements

        GuiUtil.renderOverlays(event.getGuiGraphics());

        // Overlay Overrides

        NamedGuiOverlay overlay = event.getOverlay();
        boolean isDebug = overlay.equals(VanillaGuiOverlay.DEBUG_TEXT.type());
        boolean isArmor = overlay.equals(VanillaGuiOverlay.ARMOR_LEVEL.type());
        boolean isFood = overlay.equals(VanillaGuiOverlay.FOOD_LEVEL.type());
        boolean isAir = overlay.equals(VanillaGuiOverlay.AIR_LEVEL.type());
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
            DEBUG_OVERLAY.render(event.getGuiGraphics());
            event.setCanceled(true);
        }

        if (isOverlay && isSurvivalMode && !isVehiclePresent)
        {
            GuiGraphics graphics = event.getGuiGraphics();
            ForgeGui gui = (ForgeGui) minecraft.gui;

            gui.setupOverlayRenderState(true, false);

            int width = event.getWindow().getGuiScaledWidth();
            int height = event.getWindow().getGuiScaledHeight();

            if (isArmor)
                renderArmor(gui, width, height, graphics);
            else if (isFood)
                renderFood(gui, width, height, graphics);
            else
                renderAir(gui, width, height, graphics);
        }
    }

    /**
     * Renders a modified food overlay.
     * @param gui A forge GUI instance.
     * @param width The current screen width.
     * @param height The current screen height.
     * @param graphics The current GuiGraphics object.
     */
    private static void renderFood(ForgeGui gui, int width, int height, GuiGraphics graphics)
    {
        if (ModConfig.Gameplay.disableHungerBar())
            return;

        Minecraft minecraft = Minecraft.getInstance();
        Player player = (Player) minecraft.getCameraEntity();

        if (player == null)
            return;

        minecraft.getProfiler().push("food");
        RenderSystem.enableBlend();

        GuiUtil.renderFood(graphics, player, width, height, gui.rightHeight);

        RenderSystem.disableBlend();
        minecraft.getProfiler().pop();
    }

    /**
     * Renders a modified armor overlay.
     * @param gui A forge GUI instance.
     * @param width The current screen width.
     * @param height The current screen height.
     * @param graphics The current GuiGraphics object.
     */
    private static void renderArmor(ForgeGui gui, int width, int height, GuiGraphics graphics)
    {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null)
            return;

        minecraft.getProfiler().push("armor");
        RenderSystem.enableBlend();

        GuiUtil.renderArmor(graphics, minecraft.player, width, height, gui.leftHeight, gui.rightHeight);

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
        return player.isEyeInFluidType(ForgeMod.WATER_TYPE.get()) || player.getAirSupply() < 300;
    }

    /**
     * Renders a modified air bubble overlay.
     * @param gui A forge GUI instance.
     * @param width The current screen width.
     * @param height The current screen height.
     * @param graphics The current GuiGraphics object.
     */
    private static void renderAir(ForgeGui gui, int width, int height, GuiGraphics graphics)
    {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = (Player) minecraft.getCameraEntity();

        if (player == null)
            return;

        minecraft.getProfiler().push("air");
        RenderSystem.enableBlend();

        GuiUtil.renderAir(GuiEvents::isPlayerLosingAir, graphics, player, width, height, gui.leftHeight, gui.rightHeight);

        RenderSystem.disableBlend();
        minecraft.getProfiler().pop();
    }
}
