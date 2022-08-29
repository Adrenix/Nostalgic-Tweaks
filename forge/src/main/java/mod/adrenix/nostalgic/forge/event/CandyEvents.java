package mod.adrenix.nostalgic.forge.event;

import mod.adrenix.nostalgic.client.config.ModConfig;
import mod.adrenix.nostalgic.util.EventHelper;
import mod.adrenix.nostalgic.util.ModUtil;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.event.ViewportEvent;

public abstract class CandyEvents
{
    // Old Version Overlay
    public static void versionOverlay(RenderGuiOverlayEvent.Pre event)
    {
        if (ModConfig.Candy.oldVersionOverlay())
            Minecraft.getInstance().font.drawShadow(event.getPoseStack(), ModConfig.Candy.getOverlayText(), 2.0F, 2.0F, 0xFFFFFF);
    }

    // Old Title Screen
    public static void classicTitleScreen(ScreenEvent.Opening event)
    {
        EventHelper.renderClassicTitle(event.getScreen(), event::setNewScreen);
    }

    // Old Loading Screens
    public static void classicLoadingScreens(ScreenEvent.Opening event)
    {
        EventHelper.renderClassicProgress(event.getScreen(), event::setNewScreen);
    }

    // Fog Rendering
    public static void oldFogRendering(ViewportEvent.RenderFog event)
    {
        if (ModUtil.Fog.isOverworld(event.getCamera()))
            ModUtil.Fog.setupFog(event.getCamera(), event.getMode());
        else if (ModUtil.Fog.isNether(event.getCamera()))
            ModUtil.Fog.setupNetherFog(event.getCamera(), event.getMode());
    }
}
