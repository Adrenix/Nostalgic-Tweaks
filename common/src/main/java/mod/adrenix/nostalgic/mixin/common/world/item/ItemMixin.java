package mod.adrenix.nostalgic.mixin.common.world.item;

import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.util.server.ItemServerUtil;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin
{
    /* Shadows */

    @Shadow @Final private @Nullable FoodProperties foodProperties;

    /* Injections */

    /**
     * Changes food stack size to be 1 (or 8 if a cookie).
     * Controlled by the old food stacking tweak.
     */
    @Inject(method = "getMaxStackSize", at = @At("TAIL"), cancellable = true)
    private void NT$onGetMaxStackSize(CallbackInfoReturnable<Integer> callback)
    {
        Item item = ((Item) (Object) this);

        boolean isFlesh = item.equals(Items.ROTTEN_FLESH);
        boolean isSpiderEye = item.equals(Items.SPIDER_EYE);
        boolean isLootDrop = ItemServerUtil.isDroppingLoot;
        boolean isIgnored = isFlesh || isSpiderEye || isLootDrop;

        if (!ModConfig.Gameplay.oldFoodStacking() || this.foodProperties == null || isIgnored)
            return;

        // 8 per Stack
        boolean isEightStack = item.equals(Items.COOKIE) ||
            item.equals(Items.BEETROOT) ||
            item.equals(Items.CARROT) ||
            item.equals(Items.CHORUS_FRUIT) ||
            item.equals(Items.DRIED_KELP) ||
            item.equals(Items.MELON_SLICE) ||
            item.equals(Items.POTATO) ||
            item.equals(Items.POISONOUS_POTATO) ||
            item.equals(Items.SWEET_BERRIES) ||
            item.equals(Items.GLOW_BERRIES)
        ;

        // 1 per Stack
        boolean isSingleStack = item.equals(Items.APPLE) ||
            item.equals(Items.BAKED_POTATO) ||
            item.equals(Items.BEEF) ||
            item.equals(Items.BEETROOT_SOUP) ||
            item.equals(Items.BREAD) ||
            item.equals(Items.CHICKEN) ||
            item.equals(Items.COD) ||
            item.equals(Items.COOKED_BEEF) ||
            item.equals(Items.COOKED_CHICKEN) ||
            item.equals(Items.COOKED_COD) ||
            item.equals(Items.COOKED_MUTTON) ||
            item.equals(Items.COOKED_PORKCHOP) ||
            item.equals(Items.COOKED_RABBIT) ||
            item.equals(Items.COOKED_SALMON) ||
            item.equals(Items.ENCHANTED_GOLDEN_APPLE) ||
            item.equals(Items.GOLDEN_APPLE) ||
            item.equals(Items.GOLDEN_CARROT) ||
            item.equals(Items.HONEY_BOTTLE) ||
            item.equals(Items.MUSHROOM_STEW) ||
            item.equals(Items.MUTTON) ||
            item.equals(Items.PORKCHOP) ||
            item.equals(Items.PUFFERFISH) ||
            item.equals(Items.PUMPKIN_PIE) ||
            item.equals(Items.RABBIT) ||
            item.equals(Items.RABBIT_STEW) ||
            item.equals(Items.SALMON) ||
            item.equals(Items.SUSPICIOUS_STEW) ||
            item.equals(Items.TROPICAL_FISH)
        ;

        // Any item that is not included above will follow its original stacking rules

        if (isEightStack)
            callback.setReturnValue(8);
        else if (isSingleStack)
            callback.setReturnValue(1);
    }

    /**
     * Simulates the old instant eating mechanics.
     * Controlled by the old hunger tweak.
     */
    @Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
    private void NT$onGetUseDuration(ItemStack stack, CallbackInfoReturnable<Integer> callback)
    {
        if (ModConfig.Gameplay.instantEat() && stack.isEdible())
            callback.setReturnValue(1);
    }
}
