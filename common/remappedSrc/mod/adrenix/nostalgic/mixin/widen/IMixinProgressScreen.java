package mod.adrenix.nostalgic.mixin.widen;

import net.minecraft.client.gui.screen.ProgressScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ProgressScreen.class)
public interface IMixinProgressScreen
{
    @Accessor("clearScreenAfterStop") boolean NT$getClearScreenAfterStop();
}
