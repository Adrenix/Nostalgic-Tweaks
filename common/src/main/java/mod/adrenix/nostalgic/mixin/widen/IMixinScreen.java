package mod.adrenix.nostalgic.mixin.widen;

import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Screen.class)
public interface IMixinScreen
{
    @Accessor List<Widget> getRenderables();
    @Accessor Component getTitle();
}
