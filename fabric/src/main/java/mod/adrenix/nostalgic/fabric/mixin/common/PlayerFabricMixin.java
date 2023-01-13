package mod.adrenix.nostalgic.fabric.mixin.common;

import mod.adrenix.nostalgic.common.config.ModConfig;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Player.class)
public abstract class PlayerFabricMixin
{
    /**
     * Prevents the player entity from being able to perform a critical attack.
     * Controlled by the disabled critical hits tweak.
     */
    @ModifyVariable
    (
        method = "attack",
        at = @At("STORE"),
        ordinal = 0,
        slice = @Slice
        (
            from = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/entity/player/Player;isSprinting()Z"),
            to = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/entity/player/Player;isSprinting()Z")
        )
    )
    private boolean NT$isCriticalHit(boolean vanilla)
    {
        return !ModConfig.Gameplay.disableCriticalHit() && vanilla;
    }
}
