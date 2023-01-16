package mod.adrenix.nostalgic.mixin.client.gui;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin
{
    /* Shadows */

    @Shadow protected boolean isHovered;
    @Shadow public abstract boolean isActive();

    /**
     * Renders old school style buttons by rendering yellow text on hover and slightly gray text off hover.
     * Controlled by the old button hover tweak.
     */
    @ModifyArg
    (
        method = "renderButton",
        index = 5,
        at = @At
        (
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/AbstractWidget;drawCenteredString(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V"
        )
    )
    private int NT$onRenderButton(int current)
    {
        if (!ModConfig.Candy.oldButtonHover())
            return current;
        if (this.isHovered && this.isActive())
            return 0xFFFFA0;

        return 0xE0E0E0;
    }
}
