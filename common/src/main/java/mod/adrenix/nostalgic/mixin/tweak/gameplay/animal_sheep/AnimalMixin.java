package mod.adrenix.nostalgic.mixin.tweak.gameplay.animal_sheep;

import mod.adrenix.nostalgic.helper.gameplay.SheepHelper;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
     * Shears a sheep when a player "punches" the animal.
     */
    @Inject(
        method = "hurt",
        at = @At("HEAD")
    )
    private void nt_animal_sheep$onHurt(DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> callback)
    {
        if (!GameplayTweak.OLD_SHEEP_PUNCHING.get())
            return;

        Sheep sheep = ClassUtil.cast(this, Sheep.class).orElse(null);

        if (sheep == null)
            return;

        SheepHelper.punch(sheep, this.level(), damageSource, this.random);
    }
}
