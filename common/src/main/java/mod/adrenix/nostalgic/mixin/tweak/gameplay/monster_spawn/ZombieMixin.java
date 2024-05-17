package mod.adrenix.nostalgic.mixin.tweak.gameplay.monster_spawn;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Zombie.class)
public abstract class ZombieMixin
{
    /**
     * Prevents zombies from spawning with items.
     */
    @WrapWithCondition(
        method = "populateDefaultEquipmentSlots",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/monster/Zombie;setItemSlot(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/item/ItemStack;)V"
        )
    )
    private boolean nt_monster_spawn$shouldZombieSpawnWithItems(Zombie zombie, EquipmentSlot slot, ItemStack itemStack)
    {
        return !GameplayTweak.DISABLE_MONSTER_ITEM_SPAWN.get();
    }

    /**
     * Prevents zombies from spawning as babies.
     */
    @ModifyExpressionValue(
        method = "getSpawnAsBabyOdds",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/RandomSource;nextFloat()F"
        )
    )
    private static float nt_monster_spawn$modifyBabyZombieOdds(float nextFloat)
    {
        if (GameplayTweak.DISABLE_BABY_ZOMBIE_SPAWN.get())
            return 1.0F;

        return nextFloat;
    }
}
