package mod.adrenix.nostalgic.mixin.common.world.entity.animal;

import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.mixin.widen.MobAccessor;
import mod.adrenix.nostalgic.util.common.WorldCommonUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Animal.class)
public abstract class AnimalMixin extends Mob
{
    /* Dummy Constructor */

    private AnimalMixin(EntityType<? extends Mob> type, Level level) { super(type, level); }

    /* Injections */

    /**
     * Changes the behavior of animal removal.
     * Controlled by the old animal spawning tweak.
     */
    @Inject(method = "removeWhenFarAway", at = @At("HEAD"), cancellable = true)
    private void NT$onRemoveWhenFarAway(double distanceToClosestPlayer, CallbackInfoReturnable<Boolean> callback)
    {
        boolean isLeashed = ((MobAccessor) this).NT$getCompoundTag() != null || this.isLeashed();
        boolean isSaddled = false;
        boolean isTamed = false;

        if (this instanceof Saddleable saddleable)
            isSaddled = saddleable.isSaddled();

        if ((Animal) (Object) this instanceof TamableAnimal tamable)
            isTamed = tamable.isTame();

        if (ModConfig.Gameplay.oldAnimalSpawning() && !isLeashed && !isSaddled && !isTamed)
            callback.setReturnValue(true);
    }

    /**
     * Only allows passive animals to spawn in daylight.
     * Controlled by the old animal spawning tweak.
     */
    @Inject(method = "isBrightEnoughToSpawn", at = @At("HEAD"), cancellable = true)
    private static void NT$onIsBrightEnoughToSpawn(BlockAndTintGetter level, BlockPos pos, CallbackInfoReturnable<Boolean> callback)
    {
        if (ModConfig.Gameplay.oldAnimalSpawning())
        {
            int skyLight = WorldCommonUtil.getDayLight((LevelAccessor) level);
            int blockLight = level.getBrightness(LightLayer.BLOCK, pos);

            callback.setReturnValue(skyLight > 8 || blockLight > 8);
        }
    }
}
