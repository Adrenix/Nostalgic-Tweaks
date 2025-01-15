package mod.adrenix.nostalgic.mixin.tweak.gameplay.monster_spawn;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobMixin
{
    /* Shadows */

    @Shadow
    public abstract void setItemSlot(EquipmentSlot slot, ItemStack stack);

    /* Injections */

    /**
     * Prevents monsters from spawning with armor.
     */
    @Inject(
        method = "populateDefaultEquipmentEnchantments",
        at = @At("HEAD")
    )
    private void nt_monster_spawn$removeEquipment(ServerLevelAccessor level, RandomSource random, DifficultyInstance difficulty, CallbackInfo callback)
    {
        if (ClassUtil.isNotInstanceOf(this, Monster.class))
            return;

        for (EquipmentSlot slot : EquipmentSlot.values())
        {
            if (slot.isArmor() && GameplayTweak.DISABLE_MONSTER_ARMOR_SPAWN.get())
                this.setItemSlot(slot, ItemStack.EMPTY);
        }
    }

    /**
     * Prevents the enchantment of weapons.
     */
    @WrapWithCondition(
        method = "populateDefaultEquipmentEnchantments",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Mob;enchantSpawnedWeapon(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/DifficultyInstance;)V"
        )
    )
    private boolean nt_monster_spawn$shouldEnchantWeapon(Mob mob, ServerLevelAccessor level, RandomSource random, DifficultyInstance difficulty)
    {
        if (mob instanceof Monster)
            return !GameplayTweak.DISABLE_MONSTER_ENCHANT_SPAWN.get();

        return true;
    }

    /**
     * Prevents the enchantment of armor.
     */
    @WrapWithCondition(
        method = "populateDefaultEquipmentEnchantments",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Mob;enchantSpawnedArmor(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/DifficultyInstance;)V"
        )
    )
    private boolean nt_monster_spawn$shouldEnchantArmor(Mob mob, ServerLevelAccessor level, RandomSource random, EquipmentSlot slot, DifficultyInstance difficulty)
    {
        if (mob instanceof Monster)
            return !GameplayTweak.DISABLE_MONSTER_ENCHANT_SPAWN.get();

        return true;
    }
}
