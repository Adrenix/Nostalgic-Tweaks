package mod.adrenix.nostalgic.mixin.widen;

import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemEntity.class)
public interface ItemEntityAccessor
{
    @Invoker("isMergable") boolean NT$isMergable();
}
