package mod.adrenix.nostalgic.mixin.access;

import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(Sheep.class)
public interface SheepAccess
{
    @Accessor("ITEM_BY_DYE")
    static Map<DyeColor, ItemLike> NT$ITEM_BY_DYE()
    {
        return Map.of();
    }
}
