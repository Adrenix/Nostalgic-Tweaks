package mod.adrenix.nostalgic.mixin.common.world.entity;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobCategory.class)
public abstract class MobCategoryMixin
{
    /* Shadows */

    @Shadow @Final private String name;

    /* Injections */

    /**
     * Changes the amount of creature instances that can spawn per chunk.
     * Controlled by the old animal spawning tweak.
     */
    @Inject(method = "getMaxInstancesPerChunk", at = @At("HEAD"), cancellable = true)
    private void NT$onGetMaxInstancesPerChunk(CallbackInfoReturnable<Integer> callback)
    {
        if (ModConfig.Gameplay.oldAnimalSpawning() && this.name.equals(MobCategory.CREATURE.getName()))
            callback.setReturnValue(ModConfig.Gameplay.getAnimalSpawnCap());
        else if (NostalgicTweaks.isNetworkVerified() && this.name.equals(MobCategory.MONSTER.getName()))
            callback.setReturnValue(ModConfig.Gameplay.getMonsterSpawnCap());
    }

    /**
     * Changes the persistent flag for friendly creatures.
     * Controlled by the old animal spawning tweak.
     */
    @Inject(method = "isPersistent", at = @At("HEAD"), cancellable = true)
    private void NT$onIsPersistent(CallbackInfoReturnable<Boolean> callback)
    {
        if (ModConfig.Gameplay.oldAnimalSpawning() && this.name.equals(MobCategory.CREATURE.getName()))
            callback.setReturnValue(false);
    }
}
