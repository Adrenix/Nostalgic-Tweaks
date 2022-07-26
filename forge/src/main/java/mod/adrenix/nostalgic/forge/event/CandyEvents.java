package mod.adrenix.nostalgic.forge.event;

import mod.adrenix.nostalgic.client.config.ModConfig;
import mod.adrenix.nostalgic.util.EventHelper;
import mod.adrenix.nostalgic.util.ModUtil;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.ScreenOpenEvent;

public abstract class CandyEvents
{
    // Old Version Overlay
    public static void versionOverlay(RenderGameOverlayEvent.PreLayer event)
    {
        if (ModConfig.Candy.oldVersionOverlay())
            Minecraft.getInstance().font.drawShadow(event.getMatrixStack(), ModConfig.Candy.getOverlayText(), 2.0F, 2.0F, 0xFFFFFF);
    }

    // Old Title Screen
    public static void classicTitleScreen(ScreenOpenEvent event)
    {
        EventHelper.renderClassicTitle(event.getScreen(), event::setScreen);
    }

    // Old Loading Screens
    public static void classicLoadingScreens(ScreenOpenEvent event)
    {
        EventHelper.renderClassicProgress(event.getScreen(), event::setScreen);
    }

    // Fog Rendering
    public static void oldFogRendering(EntityViewRenderEvent.RenderFogEvent event)
    {
        if (ModUtil.Fog.isOverworld(event.getCamera()))
            ModUtil.Fog.setupFog(event.getCamera(), event.getMode());
        else if (ModUtil.Fog.isNether(event.getCamera()))
            ModUtil.Fog.setupNetherFog(event.getCamera(), event.getMode());
    }
}
