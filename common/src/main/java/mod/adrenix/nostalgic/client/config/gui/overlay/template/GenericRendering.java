package mod.adrenix.nostalgic.client.config.gui.overlay.template;

import mod.adrenix.nostalgic.client.config.gui.overlay.Overlay;
import net.minecraft.client.gui.GuiGraphics;

/**
 * This interface provides implementable methods for injecting rendering instructions at specific points during generic
 * overlay rendering.
 */

@SuppressWarnings("unused") // Rendering parameters must remain consistent
public interface GenericRendering
{
    /**
     * Define rendering instructions that are performed before any other rendering instructions.
     *
     * The pose stack will be in a default state. Any changes made to the pose stack should be popped before exiting the
     * method. After these instructions are performed, the pose stack will be shifted in the z-axis by the amount
     * defined in {@link Overlay#Z_OFFSET}. This offset on the z-axis will be popped before running post-rendering
     * instructions.
     *
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick The change in game frame time.
     */
    default void onPreRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {}

    /**
     * Rendering instructions that happen after pre-rendering and before post-rendering.
     * See the pre-rendering documentation about pose stack management.
     *
     * Only the overlay background will be rendered by this point. The pose stack will have already been translated.
     * The overlay's border, title, icon, and close button will be rendered after this is called. The tooltip hint from
     * the overlay icon will be rendered after this is called and before post-rendering instructions are performed.
     *
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick The change in game frame time.
     */
    default void onMainRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {}

    /**
     * Rendering instructions that happen after main-rendering.
     * See the pre-rendering documentation about pose stack management.
     *
     * The pose stack z-axis offset will be popped before this method is called. Any rendering that needs to be
     * performed before the render cycle is over happens here.
     *
     * @param graphics The current GuiGraphics object.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick The change in game frame time.
     */
    default void onPostRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {}
}
