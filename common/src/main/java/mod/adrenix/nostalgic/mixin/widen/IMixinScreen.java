package mod.adrenix.nostalgic.mixin.widen;

import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(Screen.class)
public interface IMixinScreen
{
    @Accessor("renderables")
    List<Widget> NT$getRenderables();

    @Invoker("removeWidget")
    void NT$removeWidget(GuiEventListener listener);

    @SuppressWarnings("UnusedReturnValue")
    @Invoker("addRenderableWidget")
    <T extends GuiEventListener & Widget> T NT$addRenderableWidget(T widget);
}
