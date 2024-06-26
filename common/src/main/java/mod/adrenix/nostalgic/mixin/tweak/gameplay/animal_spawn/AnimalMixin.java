package mod.adrenix.nostalgic.mixin.tweak.gameplay.animal_spawn;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import mod.adrenix.nostalgic.util.common.data.FlagHolder;
import mod.adrenix.nostalgic.util.common.world.LevelUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Animal.class)
public abstract class AnimalMixin extends Mob
{
    /* Fake Constructor */

    private AnimalMixin(EntityType<? extends Mob> entityType, Level level)
    {
        super(entityType, level);
    }

    /* Injections */

    /**
     * Changes the behavior of animal removal.
     */
    @ModifyReturnValue(
        method = "removeWhenFarAway",
        at = @At("RETURN")
    )
    private boolean nt_animal_spawn$modifyRemoveWhenFarAway(boolean removeWhenFarAway)
    {
        FlagHolder leashed = FlagHolder.off();
        FlagHolder saddled = FlagHolder.off();
        FlagHolder tamed = FlagHolder.off();

        if (this.mayBeLeashed())
            leashed.enable();

        if (this instanceof Saddleable saddleable && saddleable.isSaddled())
            saddled.enable();

        ClassUtil.cast(this, TamableAnimal.class).ifPresent(tamable -> {
            if (tamable.isTame())
                tamed.enable();
        });

        if (GameplayTweak.OLD_ANIMAL_SPAWNING.get() && !leashed.get() && !saddled.get() && !tamed.get())
            return true;

        return removeWhenFarAway;
    }

    /**
     * Only allows passive animals to spawn in bright areas.
     */
    @ModifyReturnValue(
        method = "isBrightEnoughToSpawn",
        at = @At("RETURN")
    )
    private static boolean nt_animal_spawn$modifyIsBrightEnoughToSpawn(boolean isBrightEnoughToSpawn, BlockAndTintGetter lightGetter, BlockPos blockPos)
    {
        if (!GameplayTweak.OLD_ANIMAL_SPAWNING.get())
            return isBrightEnoughToSpawn;

        Level level = LevelUtil.getOverworld();

        if (level == null)
            return isBrightEnoughToSpawn;

        return level.getMaxLocalRawBrightness(blockPos) > 8;
    }
}
