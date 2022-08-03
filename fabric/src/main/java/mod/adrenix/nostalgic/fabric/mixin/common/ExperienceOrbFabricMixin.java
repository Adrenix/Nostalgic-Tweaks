package mod.adrenix.nostalgic.fabric.mixin.common;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbFabricMixin extends Entity
{
    /* Dummy Constructor */

    private ExperienceOrbFabricMixin(EntityType<?> type, Level level)
    {
        super(type, level);
    }

    /* Private Helpers */

    private static void removeOrb(ExperienceOrbFabricMixin orb)
    {
        if (ModConfig.Gameplay.disableOrbSpawn())
            orb.discard();
    }

    /* Injections */

    /**
     * Removes experience orb entities from the world (1st constructor).
     * Controlled by the old experience tweak.
     */
    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;DDDI)V", at = @At("TAIL"))
    private void NT$onInitPosition(Level level, double x, double y, double z, int amount, CallbackInfo callback)
    {
        ExperienceOrbFabricMixin.removeOrb(this);
    }

    /**
     * Removes experience orb entities from the world (2nd constructor).
     * Controlled by the old experience tweak.
     */
    @Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V", at = @At("TAIL"))
    private void NT$onInitLevel(EntityType<?> type, Level level, CallbackInfo callback)
    {
        ExperienceOrbFabricMixin.removeOrb(this);
    }
}
