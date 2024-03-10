package mod.adrenix.nostalgic.mixin.tweak.candy.chest_block;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.common.world.ItemUtil;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemBlockRenderTypes.class)
public abstract class ItemBlockRenderTypesMixin
{
    /**
     * Allows for transparent chests for mods that make use of translucency.
     */
    @ModifyReturnValue(
        method = "getChunkRenderType",
        at = @At("RETURN")
    )
    private static RenderType nt_chest_block$modifyChunkRenderType(RenderType renderType, BlockState blockState)
    {
        String resourceKey = ItemUtil.getResourceKey(blockState.getBlock());

        if (CandyTweak.OLD_MOD_CHESTS.get().containsKey(resourceKey))
            return RenderType.cutout();

        return renderType;
    }
}
