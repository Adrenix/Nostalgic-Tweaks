package mod.adrenix.nostalgic.mixin.common.world.entity.animal;

import mod.adrenix.nostalgic.common.config.ModConfig;
import mod.adrenix.nostalgic.mixin.widen.MobAccessor;
import mod.adrenix.nostalgic.mixin.widen.SheepAccessor;
import mod.adrenix.nostalgic.util.common.WorldCommonUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
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

    /**
     * Shears a sheep when a player 'punches' the animal.
     * Controlled by various 'punch' for wool tweaks.
     */
    @Inject(method = "hurt", at = @At("HEAD"))
    private void NT$onHurt(DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> callback)
    {
        if (ModConfig.Gameplay.oldSheepPunching() && (Animal) (Object) this instanceof Sheep sheep)
        {
            boolean isHurtByPlayer = damageSource.getEntity() instanceof Player;
            boolean canShearSheep = sheep.readyForShearing() && !this.level().isClientSide;

            if (!isHurtByPlayer || !canShearSheep)
                return;

            sheep.setSheared(true);
            int cap = ModConfig.Gameplay.oneWoolPunch() ? 1 : 1 + this.random.nextInt(3);

            for (int i = 0; i < cap; i++)
            {
                ItemEntity entity = sheep.spawnAtLocation(SheepAccessor.NT$ITEM_BY_DYE().get(sheep.getColor()), 1);

                if (entity == null)
                    continue;

                double x = (this.random.nextFloat() - this.random.nextFloat()) * 0.1F;
                double y = this.random.nextFloat() * 0.05F;
                double z = (this.random.nextFloat() - this.random.nextFloat()) * 0.1F;

                entity.setDeltaMovement(entity.getDeltaMovement().add(x, y, z));
            }
        }
    }
}
