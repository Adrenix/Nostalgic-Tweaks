package mod.adrenix.nostalgic.mixin.widen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;

@Mixin(Screen.class)
public interface IMixinScreen
{
    @Accessor("renderables") List<Drawable> NT$getRenderables();
}
