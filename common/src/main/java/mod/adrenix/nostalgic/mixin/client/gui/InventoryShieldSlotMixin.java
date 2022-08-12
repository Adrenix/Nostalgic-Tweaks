package mod.adrenix.nostalgic.mixin.client.gui;

import com.mojang.datafixers.util.Pair;
import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/world/inventory/InventoryMenu$2")
public abstract class InventoryShieldSlotMixin
{
    /**
     * Disables the overlay texture that appears over the empty off-hand slot within the inventory screen.
     * Controlled by the hide empty off-hand slot tweak.
     */
    @Inject(method = "getNoItemIcon", at = @At("HEAD"), cancellable = true)
    private void NT$onGetNoItemIcon(CallbackInfoReturnable<Pair<ResourceLocation, ResourceLocation>> callback)
    {
        Minecraft minecraft = Minecraft.getInstance();
        boolean isCreative = minecraft.gameMode != null && minecraft.gameMode.hasInfiniteItems();

        if (ModConfig.Candy.disableEmptyShield() && !isCreative)
            callback.setReturnValue(null);
    }
}
