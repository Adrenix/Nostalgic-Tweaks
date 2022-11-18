package mod.adrenix.nostalgic.client.config.gui.widget.button;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.nostalgic.client.config.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.config.gui.widget.input.ColorInput;
import mod.adrenix.nostalgic.client.config.gui.widget.list.ConfigRowList;
import mod.adrenix.nostalgic.client.config.reflect.TweakClientCache;
import mod.adrenix.nostalgic.util.common.LangUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

/**
 * The reset button is the last button to the right of all tweak cache rows within a config row list row.
 * Depending on the type of tweak that is cached, different resetting logic is required.
 */

public class ResetButton extends Button
{
    /* Fields */

    private static final Component TITLE = Component.translatable(LangUtil.Cloth.RESET);
    private final AbstractWidget controller;

    @Nullable
    private final TweakClientCache<?> tweak;

    /* Private Helpers */

    /**
     * Changes the width of the reset button based on the translation font width of this button title.
     * @return A reset button width.
     */
    private static int getResetWidth() { return Minecraft.getInstance().font.width(TITLE.getString()) + 8; }

    /**
     * Resets the tweak client cache based on the neighboring widget controller.
     * @param cache A nullable tweak client cache instance.
     * @param controller A neighboring widget controller.
     */
    private static void reset(@Nullable TweakClientCache<?> cache, AbstractWidget controller)
    {
        if (cache != null)
        {
            cache.reset();

            if (controller instanceof EditBox input && cache.getValue() instanceof String value)
                input.setValue(value);
            else if (controller instanceof ColorInput color && cache.getValue() instanceof String value)
                ((EditBox) color.getWidget()).setValue(value);
        }
        else if (controller instanceof KeyBindButton key)
            key.reset();
    }

    /* Constructor */

    /**
     * Create a new reset button instance.
     * @param tweak A nullable tweak client cache instance.
     * @param controller The controller widget used in config row list row this reset button is attached to.
     */
    public ResetButton(@Nullable TweakClientCache<?> tweak, AbstractWidget controller)
    {
        super(0, 0, getResetWidth(), ConfigRowList.BUTTON_HEIGHT, TITLE, (button) -> reset(tweak, controller));

        this.tweak = tweak;
        this.controller = controller;

        this.updateX();
    }

    /* Methods */

    /**
     * Update the starting x-position based on controller position and standard row widget gap.
     */
    private void updateX() { this.x = this.controller.x + this.controller.getWidth() + ConfigRowList.ROW_WIDGET_GAP; }

    /**
     * Handler method for reset button rendering.
     * @param poseStack The current pose stack.
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param partialTick The change in game frame time.
     */
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        this.updateX();

        if (this.tweak != null)
            this.active = this.tweak.isResettable();
        else if (this.controller instanceof KeyBindButton key)
            this.active = key.isResettable();

        if (Overlay.isOpened())
            this.active = false;

        super.render(poseStack, mouseX, mouseY, partialTick);
    }
}
