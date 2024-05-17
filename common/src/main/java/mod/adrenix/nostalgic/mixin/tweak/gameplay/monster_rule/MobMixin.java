package mod.adrenix.nostalgic.mixin.tweak.gameplay.monster_rule;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mob.class)
public abstract class MobMixin
{
    /**
     * Prevents monsters from picking up items on the ground.
     */
    @WrapWithCondition(
        method = "aiStep",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Mob;pickUpItem(Lnet/minecraft/world/entity/item/ItemEntity;)V"
        )
    )
    private boolean nt_monster_rule$shouldPickupItem(Mob mob, ItemEntity itemEntity)
    {
        if (mob instanceof Monster)
            return !GameplayTweak.DISABLE_MONSTER_ITEM_PICKUP.get();

        return true;
    }
}
