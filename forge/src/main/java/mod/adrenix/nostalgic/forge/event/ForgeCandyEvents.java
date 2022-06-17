package mod.adrenix.nostalgic.forge.event;

import mod.adrenix.nostalgic.client.config.MixinConfig;
import mod.adrenix.nostalgic.util.EventHelper;
import mod.adrenix.nostalgic.util.MixinUtil;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.ScreenOpenEvent;

public abstract class ForgeCandyEvents
{
    // Old Version Overlay
    public static void versionOverlay(RenderGameOverlayEvent.PreLayer event)
    {
        if (MixinConfig.Candy.oldVersionOverlay())
            Minecraft.getInstance().font.drawShadow(event.getMatrixStack(), MixinConfig.Candy.getOverlayText(), 2.0F, 2.0F, 0xFFFFFF);
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
        if (MixinUtil.Fog.isOverworld(event.getCamera()))
            MixinUtil.Fog.setupFog(event.getCamera(), event.getMode());
        else if (MixinUtil.Fog.isNether(event.getCamera()))
            MixinUtil.Fog.setupNetherFog(event.getCamera(), event.getMode());
    }
}
