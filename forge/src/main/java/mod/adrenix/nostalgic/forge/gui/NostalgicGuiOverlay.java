package mod.adrenix.nostalgic.forge.gui;

import mod.adrenix.nostalgic.helper.gameplay.stamina.StaminaRenderer;
import mod.adrenix.nostalgic.util.common.LocateResource;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;

public enum NostalgicGuiOverlay
{
    STAMINA("stamina", VanillaGuiOverlay.EXPERIENCE_BAR.id(), ((forgeGui, graphics, partialTick, screenWidth, screenHeight) -> {
        StaminaRenderer.render(graphics, forgeGui.rightHeight);

        if (StaminaRenderer.isVisible())
            forgeGui.rightHeight += 10;
    }));

    private final String id;
    private final ResourceLocation above;
    private final IGuiOverlay renderer;

    NostalgicGuiOverlay(String id, ResourceLocation above, IGuiOverlay renderer)
    {
        this.id = id;
        this.above = above;
        this.renderer = renderer;
    }

    /**
     * Use this if you need the location of this overlay, which is the key stored in the gui overlay manager.
     *
     * @return This overlay's unique {@link ResourceLocation}.
     */
    @PublicAPI
    public ResourceLocation key()
    {
        return LocateResource.mod(this.id);
    }

    /**
     * @return The overlay's unique string identifier.
     */
    public String id()
    {
        return this.id;
    }

    /**
     * This must be an overlay we have already registered or a vanilla overlay. Do not use other mods' overlays.
     *
     * @return The overlay this overlay will be above.
     */
    public ResourceLocation above()
    {
        return this.above;
    }

    /**
     * @return The custom renderer for this overlay.
     */
    public IGuiOverlay renderer()
    {
        return this.renderer;
    }
}
