package mod.adrenix.nostalgic.mixin.tweak.candy.block_hitbox;

import mod.adrenix.nostalgic.mixin.util.candy.HitboxMixinHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(RenderStateShard.LineStateShard.class)
public abstract class LineStateShardMixin
{
    /**
     * Changes the line state shard line thickness for the hitbox outline renderer.
     */
    @ModifyArg(
        method = "method_23554",
        at = @At(
            ordinal = 1,
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/systems/RenderSystem;lineWidth(F)V"
        )
    )
    private static float nt_block_hitbox$modifyLineWidth(float lineWidth)
    {
        if (HitboxMixinHelper.CUSTOM_HITBOX_OUTLINE.ifEnabledThenDisable())
            return Minecraft.getInstance().getWindow().getWidth() / 1920.0F * CandyTweak.BLOCK_OUTLINE_THICKNESS.get();

        return lineWidth;
    }
}
