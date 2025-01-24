package mod.adrenix.nostalgic.mixin.tweak.gameplay.combat_item;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.helper.gameplay.combat.DamageHelper;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DataComponentHolder.class)
public interface DataComponentHolderMixin
{
    /**
     * Changes the damage item attribute for tiered items.
     */
    @SuppressWarnings("unchecked")
    @ModifyReturnValue(
        method = "getOrDefault",
        at = @At("RETURN")
    )
    default <T> T nt_combat_item$modifyGetOrDefaultAttributes(T value, DataComponentType<? extends T> component)
    {
        if (!GameplayTweak.OLD_DAMAGE_VALUES.get() || DataComponents.ATTRIBUTE_MODIFIERS != component)
            return value;

        ItemStack itemStack = ClassUtil.cast(this, ItemStack.class).orElse(null);

        if (value instanceof ItemAttributeModifiers modifiers && itemStack != null)
        {
            Tool tieredItem = itemStack.get(DataComponents.TOOL);

            if (tieredItem != null && DamageHelper.isApplicable(itemStack.getItem()))
                return (T) DamageHelper.get(itemStack, modifiers);
        }

        return value;
    }
}
