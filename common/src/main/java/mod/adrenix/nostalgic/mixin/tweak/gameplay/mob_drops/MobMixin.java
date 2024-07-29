package mod.adrenix.nostalgic.mixin.tweak.gameplay.mob_drops;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mob.class)
public abstract class MobMixin
{
    /**
     * Prevents custom mob death loot from being spawned.
     */
    @WrapWithCondition(
        method = "dropCustomDeathLoot",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Mob;spawnAtLocation(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/item/ItemEntity;"
        )
    )
    private boolean nt_mob_drops$shouldDropCustomDeathLoot(Mob mob, ItemStack itemStack)
    {
        boolean oldZombiePigman = GameplayTweak.OLD_ZOMBIE_PIGMEN_DROPS.get() && mob.getType() == EntityType.ZOMBIFIED_PIGLIN;
        boolean oldSkeleton = GameplayTweak.OLD_SKELETON_DROPS.get() && mob.getType() == EntityType.SKELETON;
        boolean oldStray = GameplayTweak.OLD_STYLE_STRAY_DROPS.get() && mob.getType() == EntityType.STRAY;

        return !oldZombiePigman && !oldSkeleton && !oldStray;
    }
}
