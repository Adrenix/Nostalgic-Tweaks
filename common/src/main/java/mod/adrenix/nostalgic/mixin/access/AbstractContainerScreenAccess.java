package mod.adrenix.nostalgic.mixin.access;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccess
{
    @Accessor("leftPos")
    int nt$getLeftPos();

    @Accessor("topPos")
    int nt$getTopPos();
}
