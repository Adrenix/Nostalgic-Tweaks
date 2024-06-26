package mod.adrenix.nostalgic.mixin.tweak.candy.mip_map;

import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.resources.model.ModelManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ModelManager.class)
public abstract class ModelManagerMixin
{
    /**
     * Prevents changing of the mip level when changed by the video settings.
     */
    @ModifyVariable(
        method = "updateMaxMipLevel",
        argsOnly = true,
        ordinal = 0,
        at = @At("HEAD")
    )
    private int nt_mip_map$onSetMaxMipLevel(int value)
    {
        return CandyTweak.REMOVE_MIPMAP_TEXTURE.get() ? 0 : value;
    }
}
