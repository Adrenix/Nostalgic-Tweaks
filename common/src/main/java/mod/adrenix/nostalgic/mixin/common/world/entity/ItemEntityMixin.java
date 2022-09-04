package mod.adrenix.nostalgic.mixin.common.world.entity;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin
{
    /**
     * Multiplayer:
     *
     * If server merging optimization is disabled, then this injection will completely disable item merging.
     * Controlled by the old item merging tweak and server optimization limits.
     */
    @Inject(method = "isMergable", at = @At(value = "HEAD"), cancellable = true)
    private void NT$onIsMergable(CallbackInfoReturnable<Boolean> callback)
    {
        if (ModConfig.Candy.oldItemMerging() && ModConfig.Candy.getItemMergeLimit() == 64)
            callback.setReturnValue(false);
    }
}
