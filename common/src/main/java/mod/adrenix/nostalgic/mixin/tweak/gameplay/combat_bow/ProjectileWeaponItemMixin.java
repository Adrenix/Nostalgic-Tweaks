package mod.adrenix.nostalgic.mixin.tweak.gameplay.combat_bow;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ProjectileWeaponItem.class)
public abstract class ProjectileWeaponItemMixin
{
    /**
     * Prevents a bow from being destroyed before firing an arrow.
     */
    @Inject(
        method = "shoot",
        at = @At(
            shift = At.Shift.BEFORE,
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;)V"
        )
    )
    private void nt_combat_bow$onBeforeArrowRelease(Level level, LivingEntity shooter, InteractionHand hand, ItemStack itemStack, List<ItemStack> projectileItems, float velocity, float inaccuracy, boolean isCrit, @Nullable LivingEntity target, CallbackInfo callback)
    {
        if (GameplayTweak.INVINCIBLE_BOW.get() && itemStack.getItem() instanceof BowItem)
            itemStack.setDamageValue(0);
    }

    /**
     * Prevents a bow from receiving damage.
     */
    @WrapWithCondition(
        method = "shoot",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;hurtAndBreak(ILnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;)V"
        )
    )
    private boolean nt_combat_bow$isBowBreakable(ItemStack itemStack, int amount, LivingEntity entity, EquipmentSlot slot)
    {
        if (itemStack.getItem() instanceof BowItem)
            return !GameplayTweak.INVINCIBLE_BOW.get();

        return true;
    }
}
