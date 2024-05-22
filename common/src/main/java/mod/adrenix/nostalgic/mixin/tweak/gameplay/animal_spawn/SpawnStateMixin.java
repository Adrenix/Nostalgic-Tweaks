package mod.adrenix.nostalgic.mixin.tweak.gameplay.animal_spawn;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LocalMobCapCalculator;
import net.minecraft.world.level.NaturalSpawner;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NaturalSpawner.SpawnState.class)
public abstract class SpawnStateMixin
{
    /* Shadows */

    @Shadow @Final private int spawnableChunkCount;
    @Shadow @Final private Object2IntOpenHashMap<MobCategory> mobCategoryCounts;
    @Shadow @Final private LocalMobCapCalculator localMobCapCalculator;

    /* Injections */

    /**
     * Changes the animal spawning cap.
     */
    @ModifyReturnValue(
        method = "canSpawnForCategory",
        at = @At("RETURN")
    )
    private boolean nt_animal_spawn$modifyCanSpawnForCategory(boolean canSpawnForCategory, MobCategory mobCategory, ChunkPos chunkPos)
    {
        if (!GameplayTweak.OLD_ANIMAL_SPAWNING.get())
            return canSpawnForCategory;

        int cap = mobCategory.getMaxInstancesPerChunk() * this.spawnableChunkCount / (int) Math.pow(15.0D, 2.0D);

        boolean isBelowCap = this.mobCategoryCounts.getInt(mobCategory) < cap;
        boolean canSpawn = this.localMobCapCalculator.canSpawn(mobCategory, chunkPos);

        return isBelowCap && canSpawn;
    }
}
