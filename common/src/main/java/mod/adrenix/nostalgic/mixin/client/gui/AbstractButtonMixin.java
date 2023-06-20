package mod.adrenix.nostalgic.mixin.client.gui;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AbstractButton.class)
public abstract class AbstractButtonMixin extends AbstractWidget
{
    /* Fake Constructor */

    private AbstractButtonMixin(int x, int y, int width, int height, Component component)
    {
        super(x, y, width, height, component);
    }

    /**
     * Renders old school style buttons by rendering yellow text on hover and slightly gray text off hover. Controlled
     * by the old button, hover tweak.
     */
    @ModifyVariable(
        method = "renderString",
        at = @At("HEAD"),
        argsOnly = true
    )
    private int NT$onRenderButton(int color)
    {
        if (!ModConfig.Candy.oldButtonHover())
            return color;

        if (!this.isActive())
            return 0xA0A0A0;

        if (this.isHoveredOrFocused() && this.isActive())
            return 0xFFFFA0;

        return 0xE0E0E0;
    }
}
