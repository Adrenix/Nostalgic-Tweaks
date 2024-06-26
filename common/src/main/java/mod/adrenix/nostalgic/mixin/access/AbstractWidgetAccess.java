package mod.adrenix.nostalgic.mixin.access;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractWidget.class)
public interface AbstractWidgetAccess
{
    @Invoker("renderScrollingString")
    static void nt$renderScrollingString(GuiGraphics graphics, Font font, Component text, int minX, int minY, int maxX, int maxY, int color)
    {
        throw new AssertionError();
    }
}
