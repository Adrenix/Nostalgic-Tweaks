package mod.adrenix.nostalgic.mixin.tweak.gameplay.mechanics_player;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
    /**
     * Prevents players from sprinting and/or swimming.
     */
    @ModifyVariable(
        argsOnly = true,
        method = "setSprinting",
        at = @At("HEAD")
    )
    private boolean nt_mechanics_player$modifySprintingFlag(boolean sprinting)
    {
        boolean canSprint = !GameplayTweak.DISABLE_SPRINT.get();
        boolean canSwim = !GameplayTweak.DISABLE_SWIM.get();

        if (NostalgicTweaks.isClient() && GameplayTweak.DISABLE_SPRINT.fromDisk())
            canSprint = false;

        if (NostalgicTweaks.isClient() && GameplayTweak.DISABLE_SWIM.fromDisk())
            canSwim = false;

        if (canSprint && canSwim)
            return sprinting;

        if ((Object) this instanceof Player player)
        {
            if (player.isCreative() || player.isSpectator())
                return sprinting;

            if (player.isUnderWater())
                return canSwim;

            if (!canSprint)
                return false;
        }

        return sprinting;
    }
}
