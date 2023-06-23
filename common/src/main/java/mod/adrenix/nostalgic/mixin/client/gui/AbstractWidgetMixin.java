package mod.adrenix.nostalgic.mixin.client.gui;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin implements GuiEventListener
{
    /**
     * Removes the focus of a widget when there is a successful button click. Controlled by the remove focus on mouse
     * click tweak.
     */
    @Inject(
        method = "isValidClickButton",
        at = @At("RETURN")
    )
    private void NT$onIsValidClickButton(int button, CallbackInfoReturnable<Boolean> callback)
    {
        ComponentPath path = this.getCurrentFocusPath();

        if (ModConfig.Candy.removeFocusOnClick() && callback.getReturnValue() && path != null)
            path.applyFocus(false);
    }
}
