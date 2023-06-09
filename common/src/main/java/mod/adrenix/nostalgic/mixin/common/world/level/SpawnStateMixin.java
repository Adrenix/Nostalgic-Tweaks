package mod.adrenix.nostalgic.mixin.common.world.level;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LocalMobCapCalculator;
import net.minecraft.world.level.NaturalSpawner;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NaturalSpawner.SpawnState.class)
public abstract class SpawnStateMixin
{
    /* Shadows */

    @Shadow @Final private int spawnableChunkCount;
    @Shadow @Final private Object2IntOpenHashMap<MobCategory> mobCategoryCounts;
    @Shadow @Final private LocalMobCapCalculator localMobCapCalculator;

    /* Injections */

    /**
     * Changes the mob spawning cap.
     * Controlled by old animal spawning tweak.
     */
    @Inject(method = "canSpawnForCategory", at = @At("HEAD"), cancellable = true)
    private void NT$onCanSpawnForCategory(MobCategory category, ChunkPos pos, CallbackInfoReturnable<Boolean> callback)
    {
        if (!ModConfig.Gameplay.oldAnimalSpawning())
            return;

        int cap = category.getMaxInstancesPerChunk() * this.spawnableChunkCount / (int) Math.pow(15.0D, 2.0D);

        boolean isBelowCap = this.mobCategoryCounts.getInt(category) < cap;
        boolean canSpawn = this.localMobCapCalculator.canSpawn(category, pos);

        callback.setReturnValue(isBelowCap && canSpawn);
    }
}
