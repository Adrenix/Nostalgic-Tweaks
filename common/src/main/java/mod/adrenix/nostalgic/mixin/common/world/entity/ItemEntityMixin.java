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
     * Splits up the item stacks dropped by entities.
     * Controlled by the old item merging tweak.
     */
    @Inject(method = "isMergable", at = @At(value = "HEAD"), cancellable = true)
    private void NT$onIsMergable(CallbackInfoReturnable<Boolean> callback)
    {
        if (ModConfig.Candy.oldItemMerging())
            callback.setReturnValue(false);
    }
}
