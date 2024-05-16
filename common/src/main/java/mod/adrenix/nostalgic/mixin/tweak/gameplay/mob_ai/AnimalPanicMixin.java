package mod.adrenix.nostalgic.mixin.tweak.gameplay.mob_ai;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.AnimalPanic;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AnimalPanic.class)
public abstract class AnimalPanicMixin
{
    /**
     * Prevents animals from panicking.
     */
    @WrapWithCondition(
        method = "start(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/PathfinderMob;J)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/Brain;setMemory(Lnet/minecraft/world/entity/ai/memory/MemoryModuleType;Ljava/lang/Object;)V"
        )
    )
    private <U> boolean nt_mob_ai$shouldSetPanicMemory(Brain<?> brain, MemoryModuleType<U> memoryType, @Nullable U memory)
    {
        return !GameplayTweak.DISABLE_ANIMAL_PANIC.get();
    }

    /**
     * Prevents the erasing of the previous walking target.
     */
    @WrapWithCondition(
        method = "start(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/PathfinderMob;J)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/Brain;eraseMemory(Lnet/minecraft/world/entity/ai/memory/MemoryModuleType;)V"
        )
    )
    private <U> boolean nt_mob_ai$shouldEraseWalkTarget(Brain<?> brain, MemoryModuleType<U> type)
    {
        return !GameplayTweak.DISABLE_ANIMAL_PANIC.get();
    }
}
