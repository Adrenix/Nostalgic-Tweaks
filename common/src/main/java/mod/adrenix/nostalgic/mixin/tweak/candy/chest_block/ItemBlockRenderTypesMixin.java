package mod.adrenix.nostalgic.mixin.tweak.candy.chest_block;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.helper.candy.block.ChestHelper;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemBlockRenderTypes.class)
public abstract class ItemBlockRenderTypesMixin
{
    /**
     * Allows for the addition of translucent chests for mods that may have them.
     */
    @ModifyReturnValue(
        method = "getChunkRenderType",
        at = @At("RETURN")
    )
    private static RenderType nt_chest_block$modifyChunkRenderType(RenderType renderType, BlockState blockState)
    {
        if (ChestHelper.isTranslucent(blockState))
            return RenderType.translucent();

        return renderType;
    }
}
