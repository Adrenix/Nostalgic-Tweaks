package mod.adrenix.nostalgic.mixin.tweak.gameplay.monster_spawn;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Piglin.class)
public abstract class PiglinMixin
{
    /**
     * Prevents Piglins from spawning with armor.
     */
    @WrapWithCondition(
        method = "maybeWearArmor",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/monster/piglin/Piglin;setItemSlot(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/item/ItemStack;)V"
        )
    )
    private boolean nt_monster_spawn$shouldPiglinSpawnWithArmor(Piglin piglin, EquipmentSlot slot, ItemStack itemStack)
    {
        return !GameplayTweak.DISABLE_MONSTER_ARMOR_SPAWN.get();
    }

    /**
     * Ensures Piglins only spawn with a golden sword equipped.
     */
    @ModifyExpressionValue(
        method = "createSpawnWeapon",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/RandomSource;nextFloat()F"
        )
    )
    private float nt_monster_spawn$shouldPiglinSpawnWithOnlyGoldenSword(float nextFloat)
    {
        return GameplayTweak.PIGLIN_ONLY_GOLD_SWORD_SPAWN.get() ? 1.0F : nextFloat;
    }

    /**
     * Prevents piglins from spawning as babies.
     */
    @ModifyExpressionValue(
        method = "finalizeSpawn",
        at = @At(
            ordinal = 0,
            value = "INVOKE",
            target = "Lnet/minecraft/util/RandomSource;nextFloat()F"
        )
    )
    private float nt_monster_spawn$modifyBabyPiglinOdds(float nextFloat)
    {
        if (GameplayTweak.DISABLE_BABY_PIGLIN_SPAWN.get())
            return 1.0F;

        return nextFloat;
    }
}
