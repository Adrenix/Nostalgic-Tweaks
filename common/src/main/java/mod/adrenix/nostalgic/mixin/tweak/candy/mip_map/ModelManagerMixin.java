package mod.adrenix.nostalgic.mixin.tweak.candy.mip_map;

import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(ModelManager.class)
public abstract class ModelManagerMixin
{
    /* Shadows */

    @Shadow private int maxMipmapLevels;

    /* Injections */

    /**
     * Defines the instructions to perform when the mipmap tweak is changed.
     */
    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    private void nt_mip_map$onInit(CallbackInfo callback)
    {
        CandyTweak.REMOVE_MIPMAP_TEXTURE.whenChanged(() -> {
            if (CandyTweak.REMOVE_MIPMAP_TEXTURE.get())
                this.maxMipmapLevels = 0;
            else
                this.maxMipmapLevels = Minecraft.getInstance().options.mipmapLevels().get();
        });
    }

    /**
     * Updates the mipmap level when the model manager is reloaded.
     */
    @Inject(
        method = "reload",
        at = @At("HEAD")
    )
    private void nt_mip_map$onReload(CallbackInfoReturnable<CompletableFuture<Void>> callback)
    {
        if (CandyTweak.REMOVE_MIPMAP_TEXTURE.get())
            this.maxMipmapLevels = 0;
        else
            this.maxMipmapLevels = Minecraft.getInstance().options.mipmapLevels().get();
    }
}
