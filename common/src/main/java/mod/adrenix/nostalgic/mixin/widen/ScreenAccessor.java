package mod.adrenix.nostalgic.mixin.widen;

import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Screen.class)
public interface ScreenAccessor
{
    @Accessor("renderables")
    List<Widget> NT$getRenderables();

    @Mutable
    @Accessor("title")
    void NT$setTitle(Component title);
}
