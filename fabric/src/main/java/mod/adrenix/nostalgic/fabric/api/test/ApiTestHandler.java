package mod.adrenix.nostalgic.fabric.api.test;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.fabric.api.NostalgicFabricApi;
import mod.adrenix.nostalgic.fabric.api.event.NostalgicHudEvent;
import mod.adrenix.nostalgic.util.client.RenderUtil;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * This is a test class handler that implements the mod's Fabric API events.
 *
 * This class path is added to the fabric.mod.json "entrypoints" map.
 * For example:
 *
 * "entrypoints": {
 *   "nostalgic_tweaks": [
 *      "mod.adrenix.nostalgic.fabric.api.test.ApiTestHandler
 *   ]
 * }
 */

public class ApiTestHandler implements NostalgicFabricApi
{
    /**
     * Fabric Nostalgic API events are registered here.
     */
    @Override
    public void registerEvents()
    {
        if (!NostalgicTweaks.isEventTesting())
            return;

        ApiTestHandler.registerHudEvents();

        NostalgicTweaks.LOGGER.debug("Registered Mod API (Fabric) Event Tests");
    }

    /**
     * HUD event testing.
     */
    public static void registerHudEvents()
    {
        AtomicInteger heartX = new AtomicInteger(0);
        AtomicInteger heartY = new AtomicInteger(0);

        AtomicInteger foodX = new AtomicInteger(0);
        AtomicInteger foodY = new AtomicInteger(0);

        AtomicInteger armorX = new AtomicInteger(0);
        AtomicInteger armorY = new AtomicInteger(0);

        AtomicInteger bubbleX = new AtomicInteger(0);
        AtomicInteger bubbleY = new AtomicInteger(0);

        AtomicInteger height = new AtomicInteger(0);

        NostalgicHudEvent.RenderHeart.EVENT.register(event ->
        {
            if (!NostalgicTweaks.isDebugging())
                return;

            heartX.set(2 + (event.getIconIndex() * 10));
            heartY.set(12 + (event.getRowIndex() * 10));

            event.setX(heartX.get());
            event.setY(heartY.get());

            if (event.isHungerBarOff() && event.isExperienceBarOff())
            {
                event.setCanceled(true);
                height.set(12);
            }
            else
                height.set(heartY.get() + 10);
        });

        NostalgicHudEvent.RenderFood.EVENT.register(event ->
        {
            if (!NostalgicTweaks.isDebugging() || event.isHungerBarOff())
                return;

            foodX.set(2 + (event.getIconIndex() * 10));

            if (event.getIconIndex() == 0)
                foodY.set(height.get());

            event.setX(foodX.get());
            event.setY(foodY.get());

            height.set(foodY.get() + 10);
        });

        NostalgicHudEvent.RenderArmor.EVENT.register(event ->
        {
            if (!NostalgicTweaks.isDebugging())
                return;

            armorX.set(2 + (event.getIconIndex() * 10));

            if (event.getIconIndex() == 0)
                armorY.set(height.get());

            event.setX(armorX.get());
            event.setY(armorY.get());

            height.set(armorY.get() + 10);
        });

        NostalgicHudEvent.RenderBubble.EVENT.register(event ->
        {
            if (!NostalgicTweaks.isDebugging())
                return;

            bubbleX.set(2 + (event.getIconIndex() * 10));

            if (event.getIconIndex() == 0)
                bubbleY.set(height.get() + (event.isHungerBarOff() ? 0 : 10));

            event.setX(bubbleX.get());
            event.setY(bubbleY.get());

            if (event.isExperienceBarOff())
                RenderUtil.fill(event.getGraphics(), 2.0F, 4.0F, 2.0F, 4.0F, 0xFFFF00FF);
        });

        NostalgicTweaks.LOGGER.debug("Registered Mod API Hud Events Test");
    }
}
