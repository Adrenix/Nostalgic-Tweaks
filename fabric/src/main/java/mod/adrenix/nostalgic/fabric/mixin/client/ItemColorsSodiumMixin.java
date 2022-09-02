package mod.adrenix.nostalgic.fabric.mixin.client;

import mod.adrenix.nostalgic.util.client.ItemClientUtil;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.core.IdMapper;
import net.minecraft.core.Registry;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemColors.class)
public abstract class ItemColorsSodiumMixin
{
    /* Shadows */

    @Shadow @Final private IdMapper<ItemColor> itemColors;

    /* Injections */

    /**
     * If sodium is installed, this alternative injection will provide a modified color provider.
     * Controlled by the old 2d item colors tweak and whether sodium is installed.
     */
    @Dynamic("Method getColorProvider is added by Sodium. See: net.caffeinemc.sodium.mixin.core.model.MixinItemColors")
    @Inject(remap = false, method = "getColorProvider", at = @At("HEAD"), cancellable = true)
    private void NT$onGetColorProvider(ItemStack itemStack, CallbackInfoReturnable<ItemColor> callback)
    {
        ItemColor itemColor = this.itemColors.byId(Registry.ITEM.getId(itemStack.getItem()));

        if (ItemClientUtil.isValidColorItem(itemStack) && itemColor != null)
            callback.setReturnValue((stack, tintIndex) -> ItemClientUtil.getOldColor(itemColor, stack, tintIndex));
    }
}
