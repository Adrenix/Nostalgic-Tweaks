package mod.adrenix.nostalgic.forge.api.test;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.forge.api.event.NostalgicHudEvent;
import mod.adrenix.nostalgic.util.client.RenderUtil;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * This is a test class event handler that implements the mod's Forge API events.
 *
 * This class is created in a {@link FMLClientSetupEvent} and an example of how this checks for Nostalgic Tweaks being
 * present is in {@link mod.adrenix.nostalgic.forge.register.ClientRegistry}.
 */

public class ApiTestEventHandler
{
    /*
        HUD Event Testing
     */

    int heartX = 0;
    int heartY = 0;

    int foodX = 0;
    int foodY = 0;

    int armorX = 0;
    int armorY = 0;

    int bubbleX = 0;
    int bubbleY = 0;

    int height = 0;

    @SubscribeEvent
    public void onRenderHeartEvent(NostalgicHudEvent.RenderHeart event)
    {
        if (!NostalgicTweaks.isDebugging())
            return;

        this.heartX = 2 + (event.getIconIndex() * 10);
        this.heartY = 12 + (event.getRowIndex() * 10);

        event.setX(this.heartX);
        event.setY(this.heartY);

        if (event.isHungerBarOff() && event.isExperienceBarOff())
        {
            event.setCanceled(true);
            this.height = 12;
        }
        else
            this.height = this.heartY + 10;
    }

    @SubscribeEvent
    public void onRenderFoodEvent(NostalgicHudEvent.RenderFood event)
    {
        if (!NostalgicTweaks.isDebugging() || event.isHungerBarOff())
            return;

        this.foodX = 2 + (event.getIconIndex() * 10);

        if (event.getIconIndex() == 0)
            this.foodY = this.height;

        event.setX(this.foodX);
        event.setY(this.foodY);

        this.height = this.foodY + 10;
    }

    @SubscribeEvent
    public void onRenderArmorEvent(NostalgicHudEvent.RenderArmor event)
    {
        if (!NostalgicTweaks.isDebugging())
            return;

        this.armorX = 2 + (event.getIconIndex() * 10);

        if (event.getIconIndex() == 0)
            this.armorY = this.height;

        event.setX(this.armorX);
        event.setY(this.armorY);

        this.height = this.armorY + 10;
    }

    @SubscribeEvent
    public void onRenderBubbleEvent(NostalgicHudEvent.RenderBubble event)
    {
        if (!NostalgicTweaks.isDebugging())
            return;

        this.bubbleX = 2 + (event.getIconIndex() * 10);

        if (event.getIconIndex() == 0)
            this.bubbleY = this.height + (event.isHungerBarOff() ? 0 : 10);

        event.setX(this.bubbleX);
        event.setY(this.bubbleY);

        if (event.isExperienceBarOff())
            RenderUtil.fill(event.getGraphics(), 2.0F, 4.0F, 2.0F, 4.0F, 0xFFFF00FF);
    }
}
