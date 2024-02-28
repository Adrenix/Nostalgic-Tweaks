package mod.adrenix.nostalgic.mixin.access;

import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@SuppressWarnings("UnusedReturnValue")
@Mixin(Screen.class)
public interface ScreenAccessor
{
    @Invoker("addRenderableWidget")
    <T extends GuiEventListener & NarratableEntry> T nt$addRenderableWidget(T listener);

    @Invoker("removeWidget")
    void nt$removeWidget(GuiEventListener listener);
}
