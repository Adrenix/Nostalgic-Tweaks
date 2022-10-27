package mod.adrenix.nostalgic.mixin.widen;

import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(Sheep.class)
public interface SheepAccessor
{
    @Accessor("ITEM_BY_DYE") static Map<DyeColor, ItemLike> NT$ITEM_BYE_DYE() { throw new AssertionError(); }
}
