package mod.adrenix.nostalgic.mixin.tweak.gameplay.monster_spawn;

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
    @Unique private final RecursionAvoidance nt$monsterProcessor = RecursionAvoidance.create();

    /* Injections */

    /**
     * Changes the number of monsters that can spawn in a single chunk.
     */
    @ModifyReturnValue(
        method = "getMaxInstancesPerChunk",
        at = @At("RETURN")
    )
    private int nt_monster_spawn$modifyMaxInstancesPerChunk(int maxInstancesPerChunk)
    {
        if (NostalgicTweaks.isMixinEarly())
            return maxInstancesPerChunk;

        if (this.nt$monsterProcessor.isParked() && this.name.equals(MobCategory.CREATURE.getName()))
            return this.nt$monsterProcessor.process(GameplayTweak.MONSTER_SPAWN_CAP::get, maxInstancesPerChunk);

        return maxInstancesPerChunk;
    }
}
