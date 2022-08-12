package mod.adrenix.nostalgic.mixin.widen;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface IMixinAbstractContainerScreen
{
    @Accessor("leftPos") int NT$getLeftPos();
    @Accessor("topPos") int NT$getTopPos();
}
