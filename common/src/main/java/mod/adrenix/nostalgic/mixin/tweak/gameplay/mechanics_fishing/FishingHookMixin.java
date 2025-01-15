package mod.adrenix.nostalgic.mixin.tweak.gameplay.mechanics_fishing;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mod.adrenix.nostalgic.helper.gameplay.FishingHelper;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin extends Entity
{
    /* Shadows */

    @Shadow private boolean biting;

    /* Fake Constructor */

    private FishingHookMixin(EntityType<?> entityType, Level level)
    {
        super(entityType, level);
    }

    /* Injections */

    /**
     * Brings back the old fishing loot pool where the only item that can be caught is cod.
     */
    @ModifyExpressionValue(
        method = "retrieve",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootParams;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;"
        )
    )
    private ObjectArrayList<ItemStack> nt_mechanics_fishing$modifyFishingLootPool(ObjectArrayList<ItemStack> caught)
    {
        if (GameplayTweak.OLD_FISHING_LOOT.get())
        {
            caught.clear();
            caught.add(Items.COD.getDefaultInstance());
        }

        return caught;
    }

    /**
     * Prevents the randomized particles that simulate "fish jumping" around the bobber.
     */
    @WrapWithCondition(
        method = "catchingFish",
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/level/Level;canSeeSky(Lnet/minecraft/core/BlockPos;)Z"
            ),
            to = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/entity/projectile/FishingHook;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"
            )
        ),
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"
        )
    )
    private <T extends ParticleOptions> boolean nt_mechanics_fishing$modifyRandomFishParticles(ServerLevel level, T particle, double x, double y, double z, int particleCount, double xOffset, double yOffset, double zOffset, double speed)
    {
        return !GameplayTweak.OLD_FISHING_LURING.get();
    }

    /**
     * Prevents the fishing particles that simulate a fish approaching the bobber.
     */
    @WrapWithCondition(
        method = "catchingFish",
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/util/Mth;nextFloat(Lnet/minecraft/util/RandomSource;FF)F"
            )
        ),
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"
        )
    )
    private <T extends ParticleOptions> boolean nt_mechanics_fishing$modifyLuredFishParticles(ServerLevel level, T particle, double x, double y, double z, int particleCount, double xOffset, double yOffset, double zOffset, double speed)
    {
        return !GameplayTweak.OLD_FISHING_LURING.get();
    }

    /**
     * Prevents the bobber from bobbing up and down while in the water.
     */
    @ModifyArg(
        method = "tick",
        index = 1,
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/projectile/FishingHook;setDeltaMovement(DDD)V"
        )
    )
    private double nt_mechanics_fishing$modifyHookBobbingAmount(double y)
    {
        if (GameplayTweak.OLD_FISHING_LURING.get() && !this.biting)
            return FishingHelper.getBobbingAmount(this);

        return y;
    }
}
