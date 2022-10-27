package mod.adrenix.nostalgic.fabric.mixin.client;

import mod.adrenix.nostalgic.util.client.ItemClientUtil;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.core.IdMapper;
import net.minecraft.core.Registry;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemColors.class)
public abstract class ItemColorsFabricMixin
{
    /* Shadows */

    @Shadow @Final private IdMapper<ItemColor> itemColors;

    /* Injections */

    /**
     * Brings back the old 2d item colors.
     * Controlled by the old 2d item colors tweak.
     */
    @Inject(method = "getColor", at = @At("HEAD"), cancellable = true)
    private void NT$onGetColor(ItemStack stack, int tintIndex, CallbackInfoReturnable<Integer> callback)
    {
        ItemColor itemColor = this.itemColors.byId(Registry.ITEM.getId(stack.getItem()));

        if (ItemClientUtil.isValidColorItem() && itemColor != null)
            callback.setReturnValue(ItemClientUtil.getOldColor(itemColor, stack, tintIndex));
    }
}
