package mod.adrenix.nostalgic.mixin.widen;

import net.minecraft.client.gui.screens.ProgressScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ProgressScreen.class)
public interface ProgressScreenAccessor
{
    @Accessor("clearScreenAfterStop") boolean NT$getClearScreenAfterStop();
}
