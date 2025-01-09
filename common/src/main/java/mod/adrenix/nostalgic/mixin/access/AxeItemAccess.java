package mod.adrenix.nostalgic.mixin.access;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(AxeItem.class)
public interface AxeItemAccess
{
    @Accessor("STRIPPABLES")
    static Map<Block, Block> NT$STRIPPABLES()
    {
        return Map.of();
    }
}
