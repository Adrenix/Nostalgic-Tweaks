package mod.adrenix.nostalgic.forge.event;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.gui.screen.DynamicScreen;
import mod.adrenix.nostalgic.forge.mixin.tweak.candy.debug_screen.ForgeGuiAccess;
import mod.adrenix.nostalgic.helper.candy.level.fog.OverworldFogRenderer;
import mod.adrenix.nostalgic.helper.candy.level.fog.VoidFogRenderer;
import mod.adrenix.nostalgic.helper.candy.level.fog.WaterFogRenderer;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.tweak.enums.Generic;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.data.FlagHolder;
import mod.adrenix.nostalgic.util.common.data.NullableResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.NamedGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
    modid = NostalgicTweaks.MOD_ID,
    bus = Mod.EventBusSubscriber.Bus.FORGE,
    value = Dist.CLIENT
)
public abstract class ClientEventHandler
{
    /* State Holders */

    private static final FlagHolder ARMOR_LEVEL_PUSHED = FlagHolder.off();
    private static final FlagHolder AIR_LEVEL_PUSHED = FlagHolder.off();

    /**
     * Prevents various gui overlays from rendering depending on tweak context.
     *
     * @param event The {@link RenderGuiOverlayEvent.Pre} event instance.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void setupHighestGuiOverlayPre(RenderGuiOverlayEvent.Pre event)
    {
        NamedGuiOverlay overlay = event.getOverlay();
        GuiGraphics graphics = event.getGuiGraphics();
        LocalPlayer player = Minecraft.getInstance().player;
        ForgeGui forgeGui = (ForgeGui) Minecraft.getInstance().gui;

        boolean isForgeDebugOff = CandyTweak.OLD_DEBUG.get() != Generic.MODERN;
        boolean isExperienceOff = CandyTweak.HIDE_EXPERIENCE_BAR.get();
        boolean isFoodOff = CandyTweak.HIDE_HUNGER_BAR.get();
        boolean isMounted = NullableResult.getOrElse(player, false, local -> local.jumpableVehicle() != null);

        if (overlay.id() == VanillaGuiOverlay.HOTBAR.id())
        {
            if (isExperienceOff)
            {
                forgeGui.leftHeight -= 7;
                forgeGui.rightHeight -= 7;

                if (isMounted)
                {
                    forgeGui.leftHeight += 7;
                    forgeGui.rightHeight += 7;
                }
            }
        }

        if (overlay.id() == VanillaGuiOverlay.DEBUG_TEXT.id() && isForgeDebugOff)
        {
            if (Minecraft.getInstance().options.renderDebug)
                ((ForgeGuiAccess) forgeGui).nt$getDebugScreen().render(graphics);

            event.setCanceled(true);
        }

        if (overlay.id() == VanillaGuiOverlay.FPS_GRAPH.id() && isForgeDebugOff)
            event.setCanceled(true);

        if (overlay.id() == VanillaGuiOverlay.EXPERIENCE_BAR.id() && isExperienceOff)
            event.setCanceled(true);

        if (overlay.id() == VanillaGuiOverlay.FOOD_LEVEL.id() && isFoodOff)
            event.setCanceled(true);

        if (overlay.id() == VanillaGuiOverlay.ARMOR_LEVEL.id() && isFoodOff)
        {
            graphics.pose().pushPose();
            graphics.pose().translate((float) (GuiUtil.getGuiWidth() / 2 + 90), 0.0F, 0.0F);

            ARMOR_LEVEL_PUSHED.enable();
        }

        if (overlay.id() == VanillaGuiOverlay.AIR_LEVEL.id() && isFoodOff)
        {
            graphics.pose().pushPose();
            graphics.pose().translate((float) (GuiUtil.getGuiWidth() / 2 - 100), 0.0F, 0.0F);

            AIR_LEVEL_PUSHED.enable();
        }
    }

    /**
     * Handles the tear-down of previous graphics changes during the overlay pre-phase.
     *
     * @param event The {@link RenderGuiOverlayEvent.Post} event instance.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void setupGuiOverlayPost(RenderGuiOverlayEvent.Post event)
    {
        GuiGraphics graphics = event.getGuiGraphics();

        if (ARMOR_LEVEL_PUSHED.ifEnabledThenDisable())
            graphics.pose().popPose();

        if (AIR_LEVEL_PUSHED.ifEnabledThenDisable())
            graphics.pose().popPose();
    }

    /**
     * Changes various aspects of the world's fog depending on tweak context.
     *
     * @param event The {@link ViewportEvent.RenderFog} event instance.
     */
    @SubscribeEvent
    public static void renderFog(ViewportEvent.RenderFog event)
    {
        if (!ModTweak.ENABLED.get())
            return;

        if (OverworldFogRenderer.setupFog(event.getCamera(), event.getMode(), event::getNearPlaneDistance, event::getFarPlaneDistance, event::setFogShape, event::setNearPlaneDistance, event::setFarPlaneDistance))
            event.setCanceled(true);

        if (WaterFogRenderer.setupFog(event.getCamera(), event::setFogShape, event::setNearPlaneDistance, event::setFarPlaneDistance))
            event.setCanceled(true);

        if (VoidFogRenderer.setupFog(event.getCamera(), event.getMode(), event::getNearPlaneDistance, event::getFarPlaneDistance, event::setNearPlaneDistance, event::setFarPlaneDistance))
            event.setCanceled(true);
    }

    /**
     * Changes the fog color depending on tweak context.
     *
     * @param event The {@link ViewportEvent.ComputeFogColor} event instance.
     */
    @SubscribeEvent
    public static void computeFogColor(ViewportEvent.ComputeFogColor event)
    {
        if (!ModTweak.ENABLED.get())
            return;

        if (WaterFogRenderer.setupColor(event.getCamera(), event::setRed, event::setGreen, event::setBlue))
            return;

        OverworldFogRenderer.setupColor(event.getCamera(), event::getRed, event::getGreen, event::getBlue, event::setRed, event::setGreen, event::setBlue);
        VoidFogRenderer.setupColor(event.getCamera(), event::getRed, event::getGreen, event::getBlue, event::setRed, event::setGreen, event::setBlue);
    }

    /**
     * The mod uses a unique {@link DynamicScreen} system that can perform additional logic during screen events. If the
     * screen does not handle the key press, then the dynamic interface will perform additional logic.
     *
     * @param event The {@link ScreenEvent.KeyPressed.Post} event instance.
     */
    @SubscribeEvent
    public static void keyPressed(ScreenEvent.KeyPressed.Post event)
    {
        int keyCode = event.getKeyCode();
        int scanCode = event.getScanCode();
        int modifiers = event.getModifiers();

        if (event.getScreen() instanceof DynamicScreen<?> dynamic)
            event.setCanceled(dynamic.isKeyPressed(keyCode, scanCode, modifiers));
    }

    /**
     * The mod uses a unique {@link DynamicScreen} system that can perform additional logic during screen events. If the
     * screen does not handle the mouse click, then the dynamic interface will perform additional logic.
     *
     * @param event The {@link ScreenEvent.MouseButtonPressed.Post} event instance.
     */
    @SubscribeEvent
    public static void mouseClicked(ScreenEvent.MouseButtonPressed.Post event)
    {
        if (!event.wasHandled() && event.getScreen() instanceof DynamicScreen<?> dynamic)
        {
            double mouseX = event.getMouseX();
            double mouseY = event.getMouseY();
            int button = event.getButton();

            if (dynamic.isMouseClicked(mouseX, mouseY, button))
                event.setResult(Event.Result.ALLOW);
        }
    }

    /**
     * The mod uses a unique {@link DynamicScreen} system that can perform additional logic during screen events. If the
     * screen does not handle the mouse release, then the dynamic interface will perform additional logic.
     *
     * @param event The {@link ScreenEvent.MouseButtonReleased.Post} event instance.
     */
    @SubscribeEvent
    public static void mouseReleased(ScreenEvent.MouseButtonReleased.Post event)
    {
        if (!event.wasHandled() && event.getScreen() instanceof DynamicScreen<?> dynamic)
        {
            double mouseX = event.getMouseX();
            double mouseY = event.getMouseY();
            int button = event.getButton();

            if (dynamic.isMouseReleased(mouseX, mouseY, button))
                event.setResult(Event.Result.ALLOW);
        }
    }

    /**
     * The mod uses a unique {@link DynamicScreen} system that can perform additional logic during screen events. If the
     * screen does not handle the mouse drag, then the dynamic interface will perform additional logic.
     *
     * @param event The {@link ScreenEvent.MouseDragged.Post} event instance.
     */
    @SubscribeEvent
    public static void mouseDragged(ScreenEvent.MouseDragged.Post event)
    {
        if (event.getScreen() instanceof DynamicScreen<?> dynamic)
        {
            double mouseX = event.getMouseX();
            double mouseY = event.getMouseY();
            double dragX = event.getDragX();
            double dragY = event.getDragY();
            int button = event.getMouseButton();

            dynamic.isMouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
    }
}
