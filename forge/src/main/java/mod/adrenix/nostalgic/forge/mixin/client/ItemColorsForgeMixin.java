package mod.adrenix.nostalgic.forge.mixin.client;

import mod.adrenix.nostalgic.util.client.ItemClientUtil;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ItemColors.class)
public abstract class ItemColorsForgeMixin
{
    /* Shadows */

    @Shadow @Final private Map<Holder.Reference<Item>, ItemColor> f_92674_;

    /* Injections */

    /**
     * Brings back the old 2d item colors.
     * Controlled by the old 2d item colors tweak.
     */
    @Inject(method = "getColor", at = @At("HEAD"), cancellable = true)
    private void NT$onGetColor(ItemStack stack, int tintIndex, CallbackInfoReturnable<Integer> callback)
    {
        ItemColor itemColor = this.f_92674_.get(ForgeRegistries.ITEMS.getDelegateOrThrow(stack.getItem()));

        if (ItemClientUtil.isValidColorItem() && itemColor != null)
            callback.setReturnValue(ItemClientUtil.getOldColor(itemColor, stack, tintIndex));
    }
}
