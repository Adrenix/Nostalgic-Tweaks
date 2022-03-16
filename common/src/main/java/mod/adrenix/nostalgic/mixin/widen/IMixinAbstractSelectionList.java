package mod.adrenix.nostalgic.mixin.widen;

import net.minecraft.client.gui.components.AbstractSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractSelectionList.class)
public interface IMixinAbstractSelectionList
{
    @Accessor boolean getRenderSelection();
}
