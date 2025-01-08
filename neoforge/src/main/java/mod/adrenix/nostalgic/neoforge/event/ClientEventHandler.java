package mod.adrenix.nostalgic.neoforge.event;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.helper.candy.level.fog.OverworldFogRenderer;
import mod.adrenix.nostalgic.helper.candy.level.fog.VoidFogRenderer;
import mod.adrenix.nostalgic.helper.candy.level.fog.WaterFogRenderer;
import mod.adrenix.nostalgic.helper.gameplay.InteractionHelper;
import mod.adrenix.nostalgic.helper.gameplay.stamina.StaminaRenderer;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.data.FlagHolder;
import mod.adrenix.nostalgic.util.common.data.NullableResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(
    modid = NostalgicTweaks.MOD_ID,
    bus = EventBusSubscriber.Bus.GAME,
    value = Dist.CLIENT
)
public abstract class ClientEventHandler
{
    /* State Holders */

    private static final FlagHolder ARMOR_LEVEL_PUSHED = FlagHolder.off();
    private static final FlagHolder AIR_LEVEL_PUSHED = FlagHolder.off();
    private static final FlagHolder FOOD_DISABLED = FlagHolder.off();

    /**
     * Prevents various gui overlays from rendering depending on tweak context.
     *
     * @param event The {@link RenderGuiLayerEvent.Pre} event instance.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void setupHighestGuiOverlayPre(RenderGuiLayerEvent.Pre event)
    {
        ResourceLocation overlay = event.getName();
        GuiGraphics graphics = event.getGuiGraphics();
        LocalPlayer player = Minecraft.getInstance().player;
        Gui gui = Minecraft.getInstance().gui;

        boolean isExperienceOff = CandyTweak.HIDE_EXPERIENCE_BAR.get();
        boolean isFoodOff = CandyTweak.HIDE_HUNGER_BAR.get();
        boolean isMounted = NullableResult.getOrElse(player, false, local -> local.jumpableVehicle() != null);

        if (overlay == VanillaGuiLayers.HOTBAR)
        {
            if (isExperienceOff)
            {
                gui.leftHeight -= 7;
                gui.rightHeight -= 7;

                if (isMounted)
                {
                    gui.leftHeight += 7;
                    gui.rightHeight += 7;
                }
            }
        }

        if (overlay == VanillaGuiLayers.EXPERIENCE_BAR && isExperienceOff)
            event.setCanceled(true);

        if (overlay == VanillaGuiLayers.EXPERIENCE_LEVEL && isExperienceOff)
            event.setCanceled(true);

        if (overlay == VanillaGuiLayers.FOOD_LEVEL && isFoodOff)
            event.setCanceled(true);

        if (overlay == VanillaGuiLayers.ARMOR_LEVEL && isFoodOff)
        {
            graphics.pose().pushPose();
            graphics.pose().translate((float) (GuiUtil.getGuiWidth() / 2 + 90), 0.0F, 0.0F);

            ARMOR_LEVEL_PUSHED.enable();
            FOOD_DISABLED.enable();
        }

        if (overlay == VanillaGuiLayers.AIR_LEVEL && isFoodOff)
        {
            graphics.pose().pushPose();
            graphics.pose().translate((float) (GuiUtil.getGuiWidth() / 2 - 100), 0.0F, 0.0F);

            AIR_LEVEL_PUSHED.enable();
        }

        if (overlay == VanillaGuiLayers.FOOD_LEVEL && event.isCanceled())
            FOOD_DISABLED.enable();
    }

    /**
     * Handles the tear-down of previous graphics changes during the overlay pre-phase.
     *
     * @param event The {@link RenderGuiLayerEvent.Post} event instance.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void setupGuiOverlayPost(RenderGuiLayerEvent.Post event)
    {
        Gui gui = Minecraft.getInstance().gui;
        GuiGraphics graphics = event.getGuiGraphics();
        ResourceLocation overlay = event.getName();

        if (ARMOR_LEVEL_PUSHED.ifEnabledThenDisable())
            graphics.pose().popPose();

        if (AIR_LEVEL_PUSHED.ifEnabledThenDisable())
            graphics.pose().popPose();

        boolean useArmor = overlay == VanillaGuiLayers.ARMOR_LEVEL && FOOD_DISABLED.get();
        boolean useFood = overlay == VanillaGuiLayers.FOOD_LEVEL && !FOOD_DISABLED.get();

        if (useArmor || useFood)
        {
            StaminaRenderer.render(graphics, gui.rightHeight);

            if (StaminaRenderer.isVisible())
                gui.rightHeight += 10;
        }

        FOOD_DISABLED.disable();
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
     * Prevents the use of specific items based on tweak context. On servers with the mod installed, the interaction
     * listener will prevent connected players from using items where needed.
     *
     * @param event The {@link InputEvent.InteractionKeyMappingTriggered} event instance.
     */
    @SubscribeEvent
    public static void onUseItem(InputEvent.InteractionKeyMappingTriggered event)
    {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null || !event.isUseItem())
            return;

        Item itemInHand = player.getItemInHand(event.getHand()).getItem();

        if (InteractionHelper.shouldNotUseItem(itemInHand))
        {
            event.setCanceled(true);
            event.setSwingHand(false);
        }
    }
}
