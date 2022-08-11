package mod.adrenix.nostalgic.mixin.widen;

import net.minecraft.client.gui.widget.EntryListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntryListWidget.class)
public interface IMixinAbstractSelectionList
{
    @Accessor("renderSelection") boolean NT$getRenderSelection();
}
