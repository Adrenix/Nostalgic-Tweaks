package mod.adrenix.nostalgic.mixin.widen;

import net.minecraft.client.gui.widget.ClickableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ClickableWidget.class)
public interface IMixinAbstractWidget
{
    @Invoker("setFocused") void NT$setFocus(boolean focused);
}
