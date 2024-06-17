package mod.adrenix.nostalgic.mixin.tweak.gameplay.animal_spawn;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import mod.adrenix.nostalgic.util.common.data.RecursionAvoidance;
import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MobCategory.class)
public abstract class MobCategoryMixin
{
    /* Shadow & Unique */

    @Shadow @Final private String name;
    @Unique private final RecursionAvoidance nt$animalProcessor = RecursionAvoidance.create();

    /* Injections */

    /**
     * Changes the number of animals that can spawn in a single chunk.
     */
    @ModifyReturnValue(
        method = "getMaxInstancesPerChunk",
        at = @At("RETURN")
    )
    private int nt_animal_spawn$modifyMaxInstancesPerChunk(int maxInstancesPerChunk)
    {
        if (NostalgicTweaks.isMixinEarly())
            return maxInstancesPerChunk;

        if (GameplayTweak.OLD_ANIMAL_SPAWNING.get() && this.nt$animalProcessor.isParked() && this.name.equals(MobCategory.MONSTER.getName()))
            return this.nt$animalProcessor.process(GameplayTweak.ANIMAL_SPAWN_CAP::get, maxInstancesPerChunk);

        return maxInstancesPerChunk;
    }

    /**
     * Disables persistence for friendly creatures.
     */
    @ModifyReturnValue(
        method = "isPersistent",
        at = @At("RETURN")
    )
    private boolean nt_animal_spawn$modifyAnimalPersistence(boolean isPersistent)
    {
        if (GameplayTweak.OLD_ANIMAL_SPAWNING.get() && this.name.equals(MobCategory.CREATURE.getName()))
            return false;

        return isPersistent;
    }
}
