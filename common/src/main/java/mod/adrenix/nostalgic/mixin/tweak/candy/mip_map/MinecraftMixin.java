package mod.adrenix.nostalgic.mixin.tweak.candy.mip_map;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    /**
     * Changes the initial mipmap value when the model manager is created.
     */
    @SuppressWarnings("unchecked")
    @ModifyExpressionValue(
        method = "<init>",
        slice = @Slice(
            from = @At(
                value = "NEW",
                target = "(Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/client/color/block/BlockColors;I)Lnet/minecraft/client/resources/model/ModelManager;"
            ),
            to = @At(
                value = "NEW",
                target = "()Lnet/minecraft/client/model/geom/EntityModelSet;"
            )
        ),
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;"
        )
    )
    private <T> T nt_mip_map$modifyLevel(T level)
    {
        return CandyTweak.REMOVE_MIPMAP_TEXTURE.get() ? (T) (Object) 0 : level;
    }
}
