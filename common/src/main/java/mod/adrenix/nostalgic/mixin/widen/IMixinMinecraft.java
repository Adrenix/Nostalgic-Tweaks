package mod.adrenix.nostalgic.mixin.widen;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface IMixinMinecraft
{
    @Accessor("fps") static int NT$getFPS() { throw new AssertionError(); }
}
